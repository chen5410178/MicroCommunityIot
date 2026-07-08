<template>
  <div class="edit-parking-space-machine">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>修改地磁</span>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="machineName">
              <el-input v-model="form.machineName" placeholder="必填，请填写设备名称" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备编码" prop="machineCode">
              <el-input v-model="form.machineCode" placeholder="必填，请填写设备编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备IP" prop="machineIp">
              <el-input v-model="form.machineIp" placeholder="选填，请填写设备IP" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备MAC" prop="machineMac">
              <el-input v-model="form.machineMac" placeholder="选填，请填写设备MAC" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="工牌厂家" prop="implBean">
              <el-select v-model="form.implBean" placeholder="必填，请选择工牌厂家" style="width: 100%">
                <el-option v-for="item in factorys" :key="item.factoryId" :label="item.factoryName" :value="item.factoryId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="绑定车位">
          <div>
            <el-tag
              v-for="(item, index) in form.parkingSpaces"
              :key="index"
              closable
              style="margin-right: 8px; margin-bottom: 4px;"
              @close="handleRemoveSpace(item)"
            >
              {{ item.psName }}
            </el-tag>
            <el-button type="primary" size="small" @click="openSearchSpace">选择车位</el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <SearchParkingSpace :visible.sync="searchVisible" @choose="handleSpaceChoose" />
  </div>
</template>

<script>
import { listParkingSpaceMachines, updateParkingSpaceMachine, listSpaceMachineFactorys } from '@/api/car/carApi'
import SearchParkingSpace from '@/components/car/SearchParkingSpace.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditParkingSpaceMachine',
  components: { SearchParkingSpace },
  data() {
    return {
      loading: false,
      searchVisible: false,
      factorys: [],
      form: {
        machineId: '',
        machineName: '',
        machineCode: '',
        machineIp: '',
        machineMac: '',
        implBean: '',
        parkingSpaces: []
      },
      rules: {
        machineName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' },
          { max: 200, message: '设备名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '设备编码不能为空', trigger: 'blur' }
        ],
        implBean: [
          { required: true, message: '门禁厂家不能为空', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.fetchFactorys()
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      const machineId = this.$route.query.machineId
      if (!machineId) return
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingSpaceMachines({ page: 1, row: 1, machineId, communityId })
        if (res.data && res.data.length > 0) {
          const detail = res.data[0]
          this.form = {
            machineId: detail.machineId,
            machineName: detail.machineName,
            machineCode: detail.machineCode,
            machineIp: detail.machineIp,
            machineMac: detail.machineMac,
            implBean: detail.implBean,
            parkingSpaces: detail.parkingSpaces || []
          }
        }
      } catch { /* ignore */ }
    },
    async fetchFactorys() {
      try {
        const res = await listSpaceMachineFactorys({ page: 1, row: 100 })
        this.factorys = res.data || []
      } catch { /* ignore */ }
    },
    openSearchSpace() {
      this.searchVisible = true
    },
    handleSpaceChoose(row) {
      const exists = this.form.parkingSpaces.some(item => item.psId === row.psId)
      if (!exists) {
        this.form.parkingSpaces.push({ psId: row.psId, psName: row.paNum + '-' + row.num })
      }
    },
    handleRemoveSpace(item) {
      this.form.parkingSpaces = this.form.parkingSpaces.filter(s => s.psId !== item.psId)
    },
    handleCancel() {
      this.$router.back()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateParkingSpaceMachine({ ...this.form, communityId })
        this.$message.success('修改成功')
        this.$router.back()
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
