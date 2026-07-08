<template>
  <div class="add-monitor-machine-model">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加监控设备到模型</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="监控模型" prop="modelId">
          <el-select v-model="form.modelId" placeholder="请选择监控模型" style="width: 400px">
            <el-option
              v-for="item in monitorModelList"
              :key="item.modelId"
              :label="item.modelName"
              :value="item.modelId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择设备">
          <el-input
            v-model="monitorMachineNameOnlyView"
            type="textarea"
            :rows="4"
            readonly
            placeholder="请点击下方按钮选择设备"
            style="width: 400px"
          />
          <el-button type="primary" size="small" style="margin-top: 8px; display: block" @click="chooseVisible = true">选择设备</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <ChooseMonitorMachine :visible.sync="chooseVisible" @selected="handleSelected" />
  </div>
</template>

<script>
import { listMonitorModels, saveMonitorMachineModel } from '@/api/monitor/monitorApi'
import ChooseMonitorMachine from '@/components/monitor/ChooseMonitorMachine.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMonitorMachineModel',
  components: { ChooseMonitorMachine },
  data() {
    return {
      loading: false,
      chooseVisible: false,
      monitorModelList: [],
      selectedMachineList: [],
      form: {
        modelId: '',
        machineIdList: []
      },
      rules: {
        modelId: [
          { required: true, message: '请选择监控模型', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    monitorMachineNameOnlyView() {
      return this.selectedMachineList.map(item => item.machineName).join(', ')
    }
  },
  mounted() {
    this.fetchMonitorModels()
  },
  methods: {
    async fetchMonitorModels() {
      try {
        const res = await listMonitorModels({ page: 1, row: 100 })
        this.monitorModelList = res.data || []
      } catch { /* ignore */ }
    },
    handleSelected(rows) {
      this.selectedMachineList = rows
      this.form.machineIdList = rows.map(item => item.machineId)
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      if (this.form.machineIdList.length === 0) {
        this.$message.warning('请选择设备')
        return
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveMonitorMachineModel({ modelId: this.form.modelId, machineIdList: this.form.machineIdList.join(','), communityId })
        this.$message.success('添加成功')
        this.form = { modelId: '', machineIdList: [] }
        this.selectedMachineList = []
        this.$refs.formRef && this.$refs.formRef.resetFields()
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
