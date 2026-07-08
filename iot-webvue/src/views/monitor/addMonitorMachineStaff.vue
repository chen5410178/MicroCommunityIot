<template>
  <div class="add-monitor-machine-staff">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>新增员工授权</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="员工名称" prop="staffName">
          <el-input
            v-model="form.staffName"
            placeholder="请点击选择按钮选择员工"
            readonly
            style="width: 400px"
          />
          <el-button type="primary" size="small" style="margin-left: 8px" @click="chooseVisible = true">选择员工</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="$router.back()">返回</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading" style="margin-left: 16px">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <ChooseStaff :visible.sync="chooseVisible" @selected="handleSelected" />
  </div>
</template>

<script>
import { saveMonitorMachinePerson } from '@/api/monitor/monitorApi'
import ChooseStaff from '@/components/monitor/ChooseStaff.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMonitorMachineStaff',
  components: { ChooseStaff },
  data() {
    return {
      loading: false,
      chooseVisible: false,
      machineId: '',
      form: {
        staffId: '',
        staffName: ''
      },
      rules: {
        staffName: [
          { required: true, message: '请选择员工', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
  },
  methods: {
    handleSelected(row) {
      this.form.staffId = row.userId
      this.form.staffName = row.userName
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      if (!this.machineId) {
        this.$message.warning('设备ID不能为空')
        return
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveMonitorMachinePerson({
          machineId: this.machineId,
          personId: this.form.staffId,
          personName: this.form.staffName,
          personType: '2002',
          communityId
        })
        this.$message.success('添加成功')
        this.$router.back()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
