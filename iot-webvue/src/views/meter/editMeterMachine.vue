<template>
  <div class="edit-meter-machine">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>修改水电表</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="machineName">
              <el-input v-model="form.machineName" placeholder="必填，请填写名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表号" prop="address">
              <el-input v-model="form.address" placeholder="必填，请填写表号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="表类型" prop="meterType">
              <el-select v-model="form.meterType" placeholder="必填，请选择表类型" style="width: 100%">
                <el-option :value="item.typeId" :label="item.typeName" v-for="(item, index) in meterTypes" :key="index" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模式" prop="machineModel">
              <el-select v-model="form.machineModel" placeholder="必填，请选择模式" style="width: 100%">
                <el-option value="1001" label="充值模式" />
                <el-option value="2002" label="抄表模式" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.machineModel == '2002'">
          <el-col :span="12">
            <el-form-item label="抄表日" prop="readDay" :rules="[{ required: form.machineModel == '2002', message: '请选择抄表日', trigger: 'change' }]">
              <el-select v-model="form.readDay" placeholder="必填，请选择抄表日" style="width: 100%" :disabled="form.machineModel != '2002'">
                <el-option :value="day" :label="day + '日'" v-for="day in 31" :key="day" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="抄表时" prop="readHours" :rules="[{ required: form.machineModel == '2002', message: '请选择抄表时', trigger: 'change' }]">
              <el-select v-model="form.readHours" placeholder="必填，请选择抄表时" style="width: 100%" :disabled="form.machineModel != '2002'">
                <el-option :value="h" :label="h + '时'" v-for="h in 24" :key="h" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房屋" prop="roomId">
              <el-input v-model="form.roomName" disabled placeholder="必填，请选择房屋">
                <el-button slot="append" @click="roomVisible = true">选择</el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="厂家设备" prop="implBean">
              <el-select v-model="form.implBean" placeholder="必填，请选择厂家设备" style="width: 100%" @change="handleFactoryChange">
                <el-option :value="item.factoryId" :label="item.factoryName" v-for="(item, index) in factorys" :key="index" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-for="(item, index) in form.specs" :key="index">
          <el-col :span="12">
            <el-form-item :label="item.specName">
              <el-input v-model.trim="item.specValue" :placeholder="item.remark" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" style="text-align: right; margin-top: 20px;">
            <el-button @click="handleBack">返回</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <RoomSelect :visible.sync="roomVisible" @confirm="handleRoomConfirm" />
  </div>
</template>

<script>
import { updateMeterMachine, listMeterMachines, listMeterTypes, listMeterMachineFactory } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'
import RoomSelect from '@/components/accessControl/RoomSelect.vue'

export default {
  name: 'EditMeterMachine',
  components: { RoomSelect },
  data() {
    return {
      loading: false,
      roomVisible: false,
      meterTypes: [],
      factorys: [],
      form: {
        machineId: '',
        machineName: '',
        address: '',
        meterType: '',
        machineModel: '',
        readDay: '',
        readHours: '',
        roomId: '',
        roomName: '',
        implBean: '',
        specs: []
      },
      rules: {
        machineName: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
        address: [{ required: true, message: '表号不能为空', trigger: 'blur' }],
        meterType: [{ required: true, message: '请选择表类型', trigger: 'change' }],
        machineModel: [{ required: true, message: '请选择模式', trigger: 'change' }],
        implBean: [{ required: true, message: '请选择厂家设备', trigger: 'change' }],
        roomId: [{ required: true, message: '请选择房屋', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (!this.machineId) {
      this.$router.go(-1)
      return
    }
    this.loadMeterTypes()
    this.loadFactorys()
    this.loadMachineInfo()
  },
  methods: {
    async loadMeterTypes() {
      try {
        const community = getCurrentCommunity()
        const res = await listMeterTypes({ page: 1, row: 100, communityId: community.communityId })
        this.meterTypes = res.data || []
      } catch (e) {
        console.error('加载类型列表失败', e)
      }
    },
    async loadFactorys() {
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachineFactory({ page: 1, row: 500, communityId: community.communityId })
        this.factorys = res.data || []
      } catch (e) {
        console.error('加载厂家列表失败', e)
      }
    },
    async loadMachineInfo() {
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachines({
          machineId: this.machineId,
          communityId: community.communityId,
          page: 1,
          row: 10
        })
        if (res.data && res.data.length > 0) {
          const info = res.data[0]
          this.form = {
            machineId: info.machineId || '',
            machineName: info.machineName || '',
            address: info.address || '',
            meterType: info.meterType || '',
            machineModel: info.machineModel || '',
            readDay: info.readDay || '',
            readHours: info.readHours || '',
            roomId: info.roomId || '',
            roomName: info.roomName || '',
            implBean: info.implBean || '',
            specs: info.specs ? info.specs.map(s => ({ ...s })) : []
          }
        }
      } catch (e) {
        console.error('加载水电表信息失败', e)
      }
    },
    handleFactoryChange(factoryId) {
      const factory = this.factorys.find(f => f.factoryId === factoryId)
      if (factory && factory.specs) {
        const existingSpecs = [...this.form.specs]
        this.form.specs = factory.specs.map(s => {
          const old = existingSpecs.find(os => os.specName === s.specName)
          return { ...s, specValue: old ? old.specValue : '' }
        })
      } else {
        this.form.specs = []
      }
    },
    handleRoomConfirm(data) {
      this.form.roomId = data.roomId
      this.form.roomName = data.roomName
    },
    handleBack() {
      this.$router.go(-1)
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        await updateMeterMachine({
          ...this.form,
          communityId: community.communityId
        })
        this.$message.success('修改成功')
        this.$router.go(-1)
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
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
