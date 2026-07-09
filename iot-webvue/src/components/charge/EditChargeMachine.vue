<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改充电桩"
    width="650px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="充电桩名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="请输入充电桩名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="设备编号" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="请输入设备编号" maxlength="64" />
      </el-form-item>
      <el-form-item label="厂家" prop="implBean">
        <el-select v-model="form.implBean" placeholder="请选择厂家" clearable style="width: 100%" @change="handleFactoryChange">
          <el-option v-for="item in factoryList" :key="item.implBean" :label="item.factoryName" :value="item.implBean" />
        </el-select>
      </el-form-item>
      <el-form-item label="厂家规格" prop="specs">
        <div v-for="(item, index) in specList" :key="index" class="spec-item">
          <span class="spec-name">{{ item.specName }}：</span>
          <el-radio-group v-model="form.specs[item.specCd]" size="small">
            <el-radio-button v-for="val in item.specValues" :key="val" :label="val">{{ val }}</el-radio-button>
          </el-radio-group>
        </div>
      </el-form-item>
      <el-form-item label="插座个数" prop="portCount">
        <el-input-number v-model="form.portCount" :min="0" :max="999" style="width: 100%" />
      </el-form-item>
      <el-form-item label="充电类型" prop="chargeType">
        <el-select v-model="form.chargeType" placeholder="请选择充电类型" clearable style="width: 100%" @change="handleChargeTypeChange">
          <el-option v-for="item in chargeTypeList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="充电规则" prop="ruleId">
        <el-select v-model="form.ruleId" placeholder="请选择充电规则" clearable style="width: 100%">
          <el-option v-for="item in ruleList" :key="item.ruleId" :label="item.ruleName" :value="item.ruleId" />
        </el-select>
      </el-form-item>
      <el-form-item label="监控视频" prop="monitorId">
        <el-select v-model="form.monitorId" placeholder="请选择监控视频" clearable style="width: 100%">
          <el-option v-for="item in monitorList" :key="item.monitorId" :label="item.monitorName" :value="item.monitorId" />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateChargeMachine, listChargeMachineFactory, listChargeMachineFactorySpec } from '@/api/charge/chargeApi'
import { listChargeRule } from '@/api/charge/chargeApi'
import { listDicts } from '@/api/system/systemApi'
import { listMonitorMachine } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditChargeMachine',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      factoryList: [],
      specList: [],
      chargeTypeList: [],
      ruleList: [],
      monitorList: [],
      form: {
        machineId: '',
        machineName: '',
        machineCode: '',
        implBean: '',
        specs: {},
        portCount: 0,
        chargeType: '',
        ruleId: '',
        monitorId: ''
      },
      rules: {
        machineName: [
          { required: true, message: '充电桩名称不能为空', trigger: 'blur' },
          { max: 64, message: '充电桩名称不能超过64', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '设备编号不能为空', trigger: 'blur' },
          { max: 64, message: '设备编号不能超过64', trigger: 'blur' }
        ],
        implBean: [
          { required: true, message: '请选择厂家', trigger: 'change' }
        ],
        chargeType: [
          { required: true, message: '请选择充电类型', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          machineId: this.editData.machineId || '',
          machineName: this.editData.machineName || '',
          machineCode: this.editData.machineCode || '',
          implBean: this.editData.implBean || '',
          specs: this.editData.specs || {},
          portCount: this.editData.portCount || 0,
          chargeType: this.editData.chargeType || '',
          ruleId: this.editData.ruleId || '',
          monitorId: this.editData.monitorId || ''
        }
        this.loadDropdownData()
        if (this.editData.implBean) {
          this.loadSpecs(this.editData.implBean)
        }
        if (this.editData.chargeType) {
          this.loadRules(this.editData.chargeType)
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadDropdownData() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const [factoryRes, dictRes, monitorRes] = await Promise.all([
          listChargeMachineFactory({ page: 1, row: 100 }),
          listDicts({ tableName: 'charge_machine', tableColumns: 'charge_type' }),
        ])
        this.factoryList = factoryRes.data || []
        this.chargeTypeList = dictRes || []
        this.monitorList = monitorRes.data || []
      } catch (e) {
        console.error('加载下拉数据失败', e)
      }
    },
    async loadSpecs(implBean) {
      try {
        const res = await listChargeMachineFactorySpec({ implBean, page: 1, row: 100 })
        const data = res.data || []
        const existingSpecs = this.form.specs || {}
        data.forEach(item => {
          if (!(item.specCd in existingSpecs)) {
            this.$set(this.form.specs, item.specCd, '')
          }
        })
        this.specList = data
      } catch (e) {
        console.error('加载厂家规格失败', e)
      }
    },
    async loadRules(chargeType) {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeRule({ chargeType, communityId, page: 1, row: 100 })
        this.ruleList = res.data || []
      } catch (e) {
        console.error('加载充电规则失败', e)
      }
    },
    handleFactoryChange(val) {
      this.specList = []
      if (!val) return
      this.loadSpecs(val)
    },
    handleChargeTypeChange(val) {
      this.form.ruleId = ''
      this.ruleList = []
      if (!val) return
      this.loadRules(val)
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.specList = []
      this.form.specs = {}
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const specsStr = typeof this.form.specs === 'object' ? JSON.stringify(this.form.specs) : this.form.specs
        await updateChargeMachine({ ...this.form, specs: specsStr, communityId })
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.spec-item { margin-bottom: 10px; display: flex; align-items: center; }
.spec-name { min-width: 80px; font-size: 13px; color: #606266; }
</style>
