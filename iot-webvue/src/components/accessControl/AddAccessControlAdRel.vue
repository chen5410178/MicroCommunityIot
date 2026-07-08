<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="关联门禁"
    width="600px"
    append-to-body
  >
    <el-checkbox-group v-model="selectedMachineIds">
      <el-checkbox
        v-for="item in machines"
        :key="item.machineId"
        :label="item.machineId"
        style="display: block; margin-bottom: 8px;"
      >
        {{ item.machineName }}
      </el-checkbox>
    </el-checkbox-group>
    <div v-if="machines.length === 0" style="text-align: center; color: #909399; padding: 20px;">暂无可用门禁设备</div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveAccessControlAdRel } from '@/api/accessControl/accessControlApi'
import { listAccessControl } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddAccessControlAdRel',
  props: {
    visible: { type: Boolean, default: false },
    adId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      machines: [],
      selectedMachineIds: []
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.selectedMachineIds = []
        this.fetchMachines()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchMachines() {
      try {
        const res = await listAccessControl({
          page: 1,
          row: 1000,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        this.machines = res.data || []
      } catch {
        this.machines = []
      }
    },
    async handleSubmit() {
      if (!this.selectedMachineIds.length) {
        this.$message.warning('请选择门禁设备')
        return
      }
      this.loading = true
      try {
        await saveAccessControlAdRel({
          adId: this.adId,
          machineIds: this.selectedMachineIds,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        this.$message.success('关联成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '关联失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
