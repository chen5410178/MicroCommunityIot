<template>
  <div class="sell-parking-space">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>出售车位</span>
        <el-button type="primary" size="small" icon="el-icon-close" @click="goBack">返回</el-button>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车位编号" prop="num">
              <el-input :value="form.num" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="停车场">
              <el-input :value="form.paNum" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车牌号" prop="carId">
              <div class="car-select-wrap">
                <el-input :value="selectedCarNum" disabled placeholder="请选择车辆" />
                <el-button type="primary" class="car-select-btn" @click="openCarDialog">选择车辆</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主" prop="ownerName">
              <el-input :value="form.ownerName" disabled placeholder="请先选择车辆" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div style="text-align: right; margin-top: 20px">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitSell">提交</el-button>
      </div>
    </el-card>
    <SelectNoPsCar :visible.sync="carVisible" @select="onSelectCar" />
  </div>
</template>

<script>
import SelectNoPsCar from '@/components/car/SelectNoPsCar.vue'
import { sellParkingSpace } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'SellParkingSpace',
  components: { SelectNoPsCar },
  data() {
    return {
      saving: false,
      carVisible: false,
      selectedCarNum: '',
      form: {
        psId: '',
        num: '',
        paNum: '',
        carId: '',
        ownerId: '',
        ownerName: ''
      },
      rules: {
        carId: [
          { required: true, message: '请选择车辆', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    const query = this.$route.query
    this.form.psId = query.psId || ''
    this.form.num = query.num || ''
    this.form.paNum = query.paNum || ''
  },
  methods: {
    openCarDialog() {
      this.carVisible = true
    },
    onSelectCar(row) {
      this.form.carId = row.carId || ''
      this.form.ownerId = row.ownerId || ''
      this.form.ownerName = row.ownerName || ''
      this.selectedCarNum = row.carNum || ''
    },
    goBack() {
      this.$router.go(-1)
    },
    submitSell() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.saving = true
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        sellParkingSpace({
          communityId,
          psId: this.form.psId,
          carId: this.form.carId,
          ownerId: this.form.ownerId,
          sellOrHire: 'S'
        }).then(() => {
          this.$message.success('出售车位成功')
          this.goBack()
        }).catch(error => {
          this.$message.error(error.message || '出售车位失败')
        }).finally(() => {
          this.saving = false
        })
      })
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.car-select-wrap { display: flex; }
.car-select-btn { margin-left: 10px; flex-shrink: 0; }
</style>
