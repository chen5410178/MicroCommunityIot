<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加电梯"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="名称" prop="machineName">
            <el-input v-model="form.machineName" placeholder="必填，请填写名称" maxlength="200" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备编号" prop="machineCode">
            <el-input v-model="form.machineCode" placeholder="必填，请填写设备编号" maxlength="30" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="form.brand" placeholder="必填，请填写品牌" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="位置" prop="locationName">
            <el-input v-model="form.locationName" placeholder="必填，请填写位置" maxlength="64" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="登记单位" prop="registrationUnit">
            <el-input v-model="form.registrationUnit" placeholder="选填，请填写登记单位" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="使用单位" prop="useUnit">
            <el-input v-model="form.useUnit" placeholder="选填，请填写使用单位" maxlength="64" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="维保单位" prop="maintenanceUnit">
            <el-input v-model="form.maintenanceUnit" placeholder="选填，请填写维保单位" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="救援电话" prop="rescueLink">
            <el-input v-model="form.rescueLink" placeholder="选填，请填写救援电话" maxlength="11" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="厂家" prop="factoryId">
            <el-select v-model="form.factoryId" placeholder="请选择厂家" clearable style="width: 100%">
              <el-option
                v-for="item in factoryList"
                :key="item.factoryId"
                :label="item.factoryName"
                :value="item.factoryId"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="监控视频">
            <el-select v-model="form.monitorId" placeholder="选填，请选择监控视频" clearable style="width: 100%">
              <el-option label="没有监控" :value="-1" />
              <el-option
                v-for="item in monitorList"
                :key="item.machineId"
                :label="item.machineName"
                :value="item.machineId"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveLiftMachine, listLiftMachineFactorys } from '@/api/lift/liftApi'
import { listMonitorMachines } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddLiftMachine',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      factoryList: [],
      monitorList: [],
      form: {
        machineName: '',
        machineCode: '',
        brand: '',
        locationName: '',
        registrationUnit: '',
        useUnit: '',
        maintenanceUnit: '',
        rescueLink: '',
        factoryId: '',
        monitorId: ''
      },
      rules: {
        machineName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 200, message: '名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '设备编号不能为空', trigger: 'blur' },
          { max: 30, message: '设备编号不能超过30', trigger: 'blur' }
        ],
        brand: [
          { required: true, message: '品牌不能为空', trigger: 'blur' },
          { max: 64, message: '品牌不能超过64', trigger: 'blur' }
        ],
        locationName: [
          { required: true, message: '位置不能为空', trigger: 'blur' },
          { max: 64, message: '位置不能超过64', trigger: 'blur' }
        ],
        registrationUnit: [
          { max: 64, message: '登记单位不能超过64', trigger: 'blur' }
        ],
        useUnit: [
          { max: 64, message: '使用单位不能超过64', trigger: 'blur' }
        ],
        maintenanceUnit: [
          { max: 64, message: '维保单位不能超过64', trigger: 'blur' }
        ],
        rescueLink: [
          { max: 11, message: '救援电话不能超过11', trigger: 'blur' }
        ],
        factoryId: [
          { required: true, message: '厂家不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadFactorys()
        this.loadMonitors()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadFactorys() {
      try {
        const res = await listLiftMachineFactorys({ page: -1, row: 100 })
        this.factoryList = res.data || []
      } catch (e) {
        console.error('获取厂家列表失败', e)
      }
    },
    async loadMonitors() {
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        const res = await listMonitorMachines({ page: 1, row: 50, communityId })
        this.monitorList = res.data || []
      } catch (e) {
        console.error('获取监控列表失败', e)
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        await saveLiftMachine({ ...this.form, communityId })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
