<template>
  <div class="rent-parking-space">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>出租车位</span>
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
            <el-form-item label="业主" prop="ownerId">
              <div class="owner-select-wrap">
                <el-input :value="form.ownerName" disabled placeholder="请选择业主" />
                <el-button type="primary" class="owner-select-btn" @click="openOwnerDialog">选择业主</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号" prop="carId">
              <el-select
                v-model="form.carId"
                filterable
                remote
                reserve-keyword
                :remote-method="loadCars"
                :disabled="!form.ownerId"
                placeholder="请先选择业主"
                style="width: 100%"
                clearable
                @change="onCarChange"
              >
                <el-option v-for="item in carOptions" :key="item.carId" :label="item.carNum" :value="item.carId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
                placeholder="请选择开始时间"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
                placeholder="请选择结束时间"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div style="text-align: right; margin-top: 20px">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitRent">提交</el-button>
      </div>
    </el-card>
    <SearchOwner :visible.sync="ownerVisible" @selected="onSelectOwner" />
  </div>
</template>

<script>
import SearchOwner from '@/components/monitor/SearchOwner.vue'
import { sellParkingSpace, queryCarsWithoutParkingSpace } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RentParkingSpace',
  components: { SearchOwner },
  data() {
    return {
      saving: false,
      ownerVisible: false,
      carOptions: [],
      form: {
        psId: '',
        num: '',
        paNum: '',
        carId: '',
        carNum: '',
        ownerId: '',
        ownerName: '',
        startTime: '',
        endTime: ''
      },
      rules: {
        ownerId: [
          { required: true, message: '请选择业主', trigger: 'change' }
        ],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' }
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
    openOwnerDialog() {
      this.ownerVisible = true
    },
    onSelectOwner(row) {
      this.form.ownerId = row.memberId || ''
      this.form.ownerName = row.name || ''
      this.form.carId = ''
      this.form.carNum = ''
      this.carOptions = []
      this.loadCars('')
    },
    loadCars(keyword) {
      if (!this.form.ownerId) return
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      queryCarsWithoutParkingSpace({
        page: 1,
        row: 20,
        communityId,
        ownerId: this.form.ownerId,
        carNumLike: keyword || ''
      }).then(res => {
        this.carOptions = res.data || []
      }).catch(() => {
        this.carOptions = []
      })
    },
    onCarChange(carId) {
      const found = this.carOptions.find(item => item.carId === carId)
      this.form.carNum = found ? found.carNum : ''
    },
    goBack() {
      this.$router.go(-1)
    },
    submitRent() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.endTime <= this.form.startTime) {
          this.$message.error('结束时间必须晚于开始时间')
          return
        }
        this.saving = true
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        sellParkingSpace({
          communityId,
          psId: this.form.psId,
          carId: this.form.carId || '',
          ownerId: this.form.ownerId,
          sellOrHire: 'H',
          startTime: this.form.startTime,
          endTime: this.form.endTime
        }).then(() => {
          this.$message.success('出租车位成功')
          this.goBack()
        }).catch(error => {
          this.$message.error(error.message || '出租车位失败')
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
.owner-select-wrap { display: flex; }
.owner-select-btn { margin-left: 10px; flex-shrink: 0; }
</style>
