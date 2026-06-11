<template>
  <div class="edit-instrument">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>修改仪表设备</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仪表编码" prop="machineCode">
              <el-input v-model="form.machineCode" placeholder="必填，请填写仪表编码" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仪表名称" prop="machineName">
              <el-input v-model="form.machineName" placeholder="必填，请填写仪表名称" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上报时间" prop="upMin">
              <el-input v-model="form.upMin" type="number" placeholder="必填，请填写上报时间">
                <template slot="append">min</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采样时间" prop="checkSec">
              <el-input v-model="form.checkSec" type="number" placeholder="必填，请填写采样时间">
                <template slot="append">sec</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仪表厂家" prop="implBean">
              <el-select v-model="form.implBean" placeholder="必填，请选择仪表厂家" style="width: 100%" @change="handleFactoryChange">
                <el-option v-for="item in factoryList" :key="item.factoryId" :label="item.factoryName" :value="item.factoryId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仪表类型" prop="typeId">
              <el-select v-model="form.typeId" placeholder="必填，请选择仪表类型" style="width: 100%">
                <el-option v-for="item in instrumentTypeList" :key="item.typeId" :label="item.name" :value="item.typeId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-for="(item, index) in form.specs" :key="'spec' + index">
          <el-col :span="12">
            <el-form-item :label="item.specName">
              <el-input v-model="item.value" :placeholder="item.remark" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">传感器</el-divider>
        <div style="margin-bottom: 12px">
          <el-button type="primary" size="small" @click="addSensor">添加传感器</el-button>
        </div>
        <div v-for="(item, index) in form.sensorList" :key="item.id" style="margin-bottom: 12px">
          <el-row :gutter="10" align="middle">
            <el-col :span="5">
              <el-select v-model="item.sensorType" placeholder="传感器类型" style="width: 100%">
                <el-option v-for="st in sensorTypeList" :key="st.statusCd" :label="st.name" :value="st.statusCd" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-select v-model="item.paramType" placeholder="参数类型" style="width: 100%" @change="handleParamTypeChange(item)">
                <el-option label="数值型" value="1001" />
                <el-option label="非数值型" value="1002" />
              </el-select>
            </el-col>
            <template v-if="item.paramType === '1001'">
              <el-col :span="4">
                <el-input v-model="item.minValue" placeholder="最小值" />
              </el-col>
              <el-col :span="4">
                <el-input v-model="item.maxValue" placeholder="最大值" />
              </el-col>
              <el-col :span="4">
                <el-input v-model="item.units" placeholder="单位" />
              </el-col>
            </template>
            <el-col :span="3">
              <el-button type="danger" size="small" @click="removeSensor(index)">删除</el-button>
            </el-col>
          </el-row>
        </div>
      </el-form>
      <div style="text-align: right; margin-top: 20px">
        <el-button @click="handleBack">返回</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { updateInstrument, listInstruments, listInstrumentTypes, listInstrumentFactory } from '@/api/meter/meterApi'
import { listDicts } from '@/api/dev/devApi'

let sensorId = 0

export default {
  name: 'EditInstrument',
  data() {
    return {
      machineId: '',
      form: {
        machineCode: '',
        machineName: '',
        upMin: '',
        checkSec: '',
        implBean: '',
        typeId: '',
        specs: [],
        sensorList: []
      },
      instrumentTypeList: [],
      factoryList: [],
      sensorTypeList: [],
      submitting: false,
      rules: {
        machineCode: [{ required: true, message: '仪表编码不能为空', trigger: 'blur' }],
        machineName: [{ required: true, message: '仪表名称不能为空', trigger: 'blur' }],
        upMin: [{ required: true, message: '上报时间不能为空', trigger: 'blur' }],
        checkSec: [{ required: true, message: '采样时间不能为空', trigger: 'blur' }],
        implBean: [{ required: true, message: '仪表厂家不能为空', trigger: 'change' }],
        typeId: [{ required: true, message: '仪表类型不能为空', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (this.machineId) {
      this.fetchInstrument()
    }
    this.fetchInstrumentTypes()
    this.fetchFactoryList()
    this.fetchSensorTypes()
  },
  methods: {
    async fetchInstrument() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listInstruments({ page: -1, row: 100, communityId, machineId: this.machineId })
        const data = res.data && res.data[0]
        if (data) {
          this.form.machineCode = data.machineCode || ''
          this.form.machineName = data.machineName || ''
          this.form.upMin = data.upMin || ''
          this.form.checkSec = data.checkSec || ''
          this.form.implBean = data.implBean || ''
          this.form.typeId = data.typeId || ''
          this.form.specs = (data.instrumentParamList || []).map(spec => ({ ...spec, value: spec.value || '' }))
          this.form.sensorList = (data.instrumentSensorList || []).map(sensor => ({
            id: 'sensor_' + (++sensorId),
            sensorType: sensor.sensorType || '',
            paramType: sensor.minValue ? '1001' : '1002',
            minValue: sensor.minValue || null,
            maxValue: sensor.maxValue || null,
            units: sensor.units || null
          }))
        }
      } catch (e) {
        console.log('获取仪表信息失败')
      }
    },
    async fetchInstrumentTypes() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listInstrumentTypes({ page: -1, row: 500, communityId })
        this.instrumentTypeList = res.data || []
      } catch (e) {
        console.log('获取仪表类型失败')
      }
    },
    async fetchFactoryList() {
      try {
        const res = await listInstrumentFactory({ page: -1, row: 500 })
        this.factoryList = res.data || []
      } catch (e) {
        console.log('获取厂家列表失败')
      }
    },
    async fetchSensorTypes() {
      try {
        const res = await listDicts({ tableName: 'instrument_sensor', columnName: 'sensor_type' })
        this.sensorTypeList = res.data || []
      } catch (e) {
        console.log('获取传感器类型失败')
      }
    },
    handleFactoryChange(factoryId) {
      if (!factoryId) {
        return
      }
      const factory = this.factoryList.find(item => item.factoryId === factoryId)
      if (factory && factory.instrumentFactorySpecList) {
        this.form.specs = factory.instrumentFactorySpecList.map(spec => ({ ...spec, value: spec.value || '' }))
      } else {
        this.form.specs = []
      }
    },
    handleParamTypeChange(item) {
      if (item.paramType === '1002') {
        item.minValue = null
        item.maxValue = null
        item.units = null
      }
    },
    addSensor() {
      this.form.sensorList.push({
        id: 'sensor_' + (++sensorId),
        sensorType: '',
        paramType: '1001',
        minValue: null,
        maxValue: null,
        units: null
      })
    },
    removeSensor(index) {
      this.form.sensorList.splice(index, 1)
    },
    handleBack() {
      this.$router.go(-1)
    },
    handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.submitting = true
        try {
          const communityId = this.$store.state.app.currentCommunity.communityId || ''
          await updateInstrument({ ...this.form, machineId: this.machineId, communityId })
          this.$message.success('修改成功')
          this.$router.go(-1)
        } catch (e) {
          this.$message.error(e.msg || '修改失败')
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
