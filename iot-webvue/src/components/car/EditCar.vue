<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改车辆"
    width="650px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="车牌号" prop="carNum">
            <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="车类型" prop="carType">
            <el-select v-model="form.carType" placeholder="必填，请选择类型" style="width: 100%">
              <el-option v-for="item in carTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="车牌类型" prop="leaseType">
            <el-select v-model="form.leaseType" placeholder="必填，请选择车牌类型" style="width: 100%">
              <el-option label="月租车" value="H" />
              <el-option label="出售车辆" value="S" />
              <el-option label="内部车" value="I" />
              <el-option label="免费车" value="NM" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="停车场" prop="paId">
            <el-select v-model="form.paId" placeholder="请选择停车场" style="width: 100%">
              <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="联系人">
            <el-input v-model="form.contact" placeholder="请填写联系人" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="tel">
            <el-input v-model="form.tel" placeholder="请填写联系电话" maxlength="20" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16" v-if="form.leaseType === 'H'">
        <el-col :span="12">
          <el-form-item label="起租时间" prop="startTime">
            <el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写起租时间" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结租时间" prop="endTime">
            <el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写结租时间" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请填写备注信息" maxlength="300" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateOwnerCar, listParkingAreas } from '@/api/car/carApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditCar',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      carTypes: [],
      parkingAreas: [],
      form: {
        carId: '',
        memberId: '',
        carNum: '',
        carType: '',
        leaseType: '',
        startTime: '',
        endTime: '',
        paId: '',
        contact: '',
        tel: '',
        remark: ''
      },
      rules: {
        carNum: [{ required: true, message: '车牌号不能为空', trigger: 'blur' }],
        carType: [{ required: true, message: '车类型不能为空', trigger: 'change' }],
        leaseType: [{ required: true, message: '车牌类型不能为空', trigger: 'change' }],
        startTime: [{ required: true, message: '起租时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, message: '结租时间不能为空', trigger: 'change' }],
        tel: [{ pattern: /^1[3-9]\d{9}$/, message: '联系电话格式不正确', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadCarTypes()
    this.loadParkingAreas()
  },
  methods: {
    async loadCarTypes() {
      try {
        const res = await listDicts({ tableName: 'owner_car', tableColumns: 'car_type' })
        this.carTypes = res || []
      } catch { /* ignore */ }
    },
    async loadParkingAreas() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
      } catch { /* ignore */ }
    },
    open(car) {
      this.dialogVisible = true
      this.form = {
        carId: car.carId || '',
        memberId: car.memberId || '',
        carNum: car.carNum || '',
        carType: car.carType || '',
        leaseType: car.leaseType || '',
        startTime: car.startTime || '',
        endTime: car.endTime || '',
        paId: car.paId || '',
        contact: car.contact || '',
        tel: car.tel || '',
        remark: car.remark || ''
      }
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
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
        const params = { ...this.form }
        params.communityId = (getCurrentCommunity() || {}).communityId || ''
        if (!params.contact) delete params.contact
        if (!params.tel) delete params.tel
        const res = await updateOwnerCar(params)
        if (res.code === 0) {
          this.$message.success('修改成功')
          this.$emit('success')
          this.handleClose()
        } else {
          this.$message.error(res.msg || '修改失败')
        }
      } catch {
        this.$message.error('修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
