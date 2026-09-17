<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="授权门禁"
    width="550px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="门禁设备" prop="machineIds">
        <el-checkbox-group v-model="form.machineIds">
          <div v-for="item in machineList" :key="item.machineId" class="checkbox-item">
            <el-checkbox :label="item.machineId" :disabled="item.disabled">{{ item.machineName }}{{ item.disabled ? '（已授权）' : '' }}</el-checkbox>
          </div>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="开始时间" prop="startDate">
        <el-date-picker
          v-model="form.startDate"
          type="datetime"
          placeholder="请选择开始时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate">
        <el-date-picker
          v-model="form.endDate"
          type="datetime"
          placeholder="请选择结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">授权</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveAccessControlOrg, listAccessControl, listAccessControlOrg } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AuthorizeAccessControlOrg',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      machineList: [],
      authorizedMachineIds: [],
      orgId: '',
      form: {
        machineIds: [],
        startDate: '',
        endDate: ''
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    open(orgId) {
      this.orgId = orgId || ''
      this.form = { machineIds: [], startDate: '', endDate: '' }
      this.authorizedMachineIds = []
      this.loadMachines()
      this.dialogVisible = true
    },
    async loadMachines() {
      try {
        const community = getCurrentCommunity() || {}
        const [machineRes, orgRes] = await Promise.all([
          listAccessControl({
            page: 1,
            row: 200,
            communityId: community.communityId
          }),
          listAccessControlOrg({
            page: 1,
            row: 200,
            orgId: this.orgId,
            communityId: community.communityId
          }).catch(() => null)
        ])
        const authorizedList = (orgRes && orgRes.data) || []
        this.authorizedMachineIds = authorizedList.map(item => item.machineId)
        this.machineList = (machineRes.data || []).map(item => {
          return {
            ...item,
            disabled: this.authorizedMachineIds.indexOf(item.machineId) > -1
          }
        })
      } catch (e) {
        console.error('加载门禁设备失败', e)
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      if (this.form.machineIds.length === 0) {
        this.$message.warning('请选择门禁设备')
        return
      }
      const waitMachineIds = this.form.machineIds.filter(machineId => this.authorizedMachineIds.indexOf(machineId) === -1)
      if (waitMachineIds.length === 0) {
        this.$message.warning('所选门禁已授权，请勿重复授权')
        return
      }
      this.loading = true
      try {
        const community = getCurrentCommunity() || {}
        await saveAccessControlOrg({
          orgId: this.orgId,
          machineIds: waitMachineIds,
          startDate: this.form.startDate,
          endDate: this.form.endDate,
          communityId: community.communityId
        })
        this.$message.success('授权成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '授权失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.checkbox-item { padding: 4px 0; }
</style>
