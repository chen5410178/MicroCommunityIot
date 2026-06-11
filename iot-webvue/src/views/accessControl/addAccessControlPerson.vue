<template>
  <div class="add-access-control-person">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>人员授权</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="门禁设备" prop="machineIds" :rules="[{ required: true, message: '请选择门禁设备', trigger: 'change' }]">
          <el-checkbox-group v-model="form.machineIds">
            <el-checkbox v-for="item in machines" :key="item.machineId" :label="item.machineId">{{ item.machineName }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="开始时间" prop="startDate" :rules="[{ required: true, message: '请选择开始时间', trigger: 'change' }]">
          <el-date-picker v-model="form.startDate" type="date" placeholder="请选择开始时间" value-format="yyyy-MM-dd" style="width: 240px" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endDate" :rules="[{ required: true, message: '请选择结束时间', trigger: 'change' }]">
          <el-date-picker v-model="form.endDate" type="date" placeholder="请选择结束时间" value-format="yyyy-MM-dd" style="width: 240px" />
        </el-form-item>
        <el-form-item label="人员" prop="ownerId" :rules="[{ required: true, message: '请选择人员', trigger: 'change' }]">
          <el-input v-model="form.ownerName" disabled placeholder="请选择人员" style="width: 240px" />
          <el-button style="margin-left: 8px" @click="chooseVisible = true">选择人员</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <ChooseOwner :visible.sync="chooseVisible" @confirm="onOwnerConfirm" />
  </div>
</template>

<script>
import { saveAccessControlFace, listAccessControl } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'
import ChooseOwner from '@/components/accessControl/ChooseOwner.vue'

export default {
  name: 'AddAccessControlPerson',
  components: { ChooseOwner },
  data() {
    return {
      machines: [],
      form: {
        machineIds: [],
        startDate: '',
        endDate: '',
        ownerId: '',
        ownerName: ''
      },
      chooseVisible: false,
      submitting: false
    }
  },
  mounted() {
    this.fetchMachines()
  },
  methods: {
    async fetchMachines() {
      try {
        const community = getCurrentCommunity()
        const res = await listAccessControl({
          page: 1,
          row: 50,
          communityId: community ? community.communityId : ''
        })
        this.machines = res.data || []
      } catch (e) {
        console.error('获取门禁设备失败', e)
      }
    },
    onOwnerConfirm(owner) {
      this.form.ownerId = owner.memberId
      this.form.ownerName = owner.name
    },
    handleBack() {
      this.$router.back()
    },
    async handleSubmit() {
      const valid = await this.$refs.formRef.validate().catch(() => false)
      if (!valid) return
      this.submitting = true
      try {
        const community = getCurrentCommunity()
        await saveAccessControlFace({
          ...this.form,
          communityId: community ? community.communityId : ''
        })
        this.$message.success('授权成功')
        this.$router.back()
      } catch (e) {
        this.$message.error(e.msg || '授权失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
