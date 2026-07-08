<template>
  <div class="hire-parking-space">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加车辆</span>
        <el-button size="small" @click="goBack">返回</el-button>
      </div>
      <el-form :model="form" ref="formRef" label-width="120px" style="max-width: 800px;">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车牌号" prop="carNum" :rules="[{ required: true, message: '车牌号不能为空', trigger: 'blur' }, { min: 2, max: 12, message: '车牌号不正确', trigger: 'blur' }]">
              <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车类型" prop="carType" :rules="[{ required: true, message: '车类型不能为空', trigger: 'change' }]">
              <el-select v-model="form.carType" placeholder="必填，请选择车辆类型" style="width: 100%">
                <el-option v-for="item in carTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车牌类型" prop="leaseType" :rules="[{ required: true, message: '车牌类型不能为空', trigger: 'change' }]">
              <el-select v-model="form.leaseType" placeholder="必填，请选择车牌类型" style="width: 100%" @change="onLeaseTypeChange">
                <el-option label="月租车" value="H" />
                <el-option label="出售车辆" value="S" />
                <el-option label="内部车" value="I" />
                <el-option label="免费车" value="NM" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车颜色">
              <el-input v-model="form.carColor" placeholder="请填写车颜色" maxlength="12" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.leaseType === 'H'">
          <el-col :span="12">
            <el-form-item label="起租时间" prop="startTime" :rules="[{ required: true, message: '起租时间不能为空', trigger: 'change' }]">
              <el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写起租时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结租时间" prop="endTime" :rules="[{ required: true, message: '结租时间不能为空', trigger: 'change' }]">
              <el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写结租时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业主" prop="ownerName" :rules="[{ required: true, message: '请选择业主', trigger: 'change' }]">
              <el-input v-model="form.ownerName" disabled placeholder="必填，请选择业主">
                <el-button slot="append" @click="openChooseOwner">选择业主</el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车位" prop="psName">
              <el-input v-model="form.psName" disabled placeholder="请选择车位">
                <el-button slot="append" @click="openChooseParkingSpace">选择车位</el-button>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写备注" maxlength="300" />
        </el-form-item>
        <el-form-item>
          <el-button @click="goBack">返回</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { saveOwnerCar } from '@/api/car/carApi'
import { listDicts } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'HireParkingSpace',
  data() {
    return {
      loading: false,
      carTypes: [],
      form: {
        carNum: '',
        carBrand: '',
        carType: '',
        carColor: '',
        leaseType: 'H',
        startTime: '',
        endTime: '',
        ownerId: '',
        ownerName: '',
        psId: '',
        psName: '',
        remark: ''
      }
    }
  },
  mounted() {
    const ownerId = this.$route.query.ownerId
    if (ownerId) {
      this.form.ownerId = ownerId
      this.form.ownerName = this.$route.query.ownerName || ''
    }
    this.loadCarTypes()
  },
  methods: {
    async loadCarTypes() {
      try {
        const res = await listDicts({ page: 1, row: 100, tableName: 'owner_car', columnName: 'car_type' })
        this.carTypes = res.data || []
      } catch { /* ignore */ }
    },
    onLeaseTypeChange() {
      this.form.startTime = ''
      this.form.endTime = ''
    },
    goBack() {
      this.$router.go(-1)
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await saveOwnerCar(this.form)
        if (res.code === 0) {
          this.$message.success('请记得收费哦！')
          this.$router.go(-1)
        } else {
          this.$message.error(res.msg || '提交失败')
        }
      } catch {
        this.$message.error('提交失败')
      } finally {
        this.loading = false
      }
    },
    openChooseOwner() {
      // TODO: 打开选择业主弹窗 - 需要searchOwner组件
      this.$message.info('请先通过业主列表进入，或后续开发选择业主弹窗')
    },
    openChooseParkingSpace() {
      // TODO: 打开选择车位弹窗 - 需要searchParkingSpace组件
      this.$message.info('请后续开发选择车位弹窗')
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
