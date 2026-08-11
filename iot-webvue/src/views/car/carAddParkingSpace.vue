<template>
  <div class="car-add-parking-space">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>续租车位</span>
      </div>
      <el-form :model="form" ref="formRef" label-width="120px" style="max-width: 600px;">
        <el-form-item label="车牌号">
          <el-input v-model="form.carNum" disabled />
        </el-form-item>
        <el-form-item label="停车场" prop="paId" :rules="[{ required: true, message: '请选择停车场', trigger: 'change' }]">
          <el-select v-model="form.paId" placeholder="请选择停车场" style="width: 100%">
            <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
          </el-select>
        </el-form-item>
        <el-form-item label="起租时间" prop="startTime" :rules="[{ required: true, message: '起租时间不能为空', trigger: 'change' }]">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写起租时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结租时间" prop="endTime" :rules="[{ required: true, message: '结租时间不能为空', trigger: 'change' }]">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写结租时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="车位" prop="psId" :rules="[{ required: true, message: '车位不能为空', trigger: 'change' }]">
          <el-input v-model="form.psName" disabled placeholder="请选择车位">
            <el-button slot="append" @click="openChooseParkingSpace">选择车位</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请填写备注信息" maxlength="300" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <SearchParkingSpace :visible.sync="searchParkingSpaceVisible" :pa-id="form.paId" parkingSpaceFlag="F" @choose="handleParkingSpaceChoose" />
  </div>
</template>

<script>
import { queryOwnerCars, carAddParkingSpace, listParkingAreas } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'
import SearchParkingSpace from '@/components/car/SearchParkingSpace.vue'

export default {
  name: 'CarAddParkingSpace',
  components: { SearchParkingSpace },
  data() {
    return {
      loading: false,
      searchParkingSpaceVisible: false,
      parkingAreas: [],
      form: {
        carNum: '',
        startTime: '',
        endTime: '',
        carId: '',
        communityId: '',
        paId: '',
        psId: '',
        psName: '',
        memberId: '',
        remark: ''
      }
    }
  },
  mounted() {
    this.form.carId = this.$route.query.carId || ''
    if (this.form.carId) {
      this.loadCarInfo()
    }
    this.loadParkingAreas()
  },
  methods: {
    async loadParkingAreas() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
      } catch { /* ignore */ }
    },
    async loadCarInfo() {
      try {
        const res = await queryOwnerCars({
          page: 1, row: 1,
          carId: this.form.carId,
          communityId: (getCurrentCommunity() || {}).communityId || ''
        })
        const data = res.data
        if (data && data.length > 0) {
          const car = data[0]
          this.form.carNum = car.carNum || ''
          this.form.communityId = car.communityId || (getCurrentCommunity() || {}).communityId || ''
          this.form.memberId = car.memberId || ''
          this.form.paId = car.paId || ''
        }
      } catch { /* ignore */ }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await carAddParkingSpace(this.form)
        if (res.code === 0) {
          this.$message.success('续租成功')
          this.$router.go(-1)
        } else {
          this.$message.error(res.msg || '续租失败')
        }
      } catch {
        this.$message.error('续租失败')
      } finally {
        this.loading = false
      }
    },
    openChooseParkingSpace() {
      if (!this.form.paId) {
        this.$message.warning('请先选择停车场')
        return
      }
      this.searchParkingSpaceVisible = true
    },
    handleParkingSpaceChoose(row) {
      this.form.psId = row.psId
      this.form.psName = row.paNum + '-' + row.num
      this.searchParkingSpaceVisible = false
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
