<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改车辆"
    width="550px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="车牌号" prop="carNum">
        <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="64" />
      </el-form-item>
      <el-form-item label="车类型" prop="carType">
        <el-select v-model="form.carType" placeholder="必填，请选择类型" style="width: 100%">
          <el-option v-for="item in carTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="车牌类型" prop="leaseType">
        <el-select v-model="form.leaseType" placeholder="必填，请选择车牌类型" style="width: 100%">
          <el-option label="月租车" value="H" />
          <el-option label="出售车辆" value="S" />
          <el-option label="内部车" value="I" />
          <el-option label="免费车" value="NM" />
        </el-select>
      </el-form-item>
      <el-form-item label="起租时间" prop="startTime" v-if="form.leaseType === 'H'">
        <el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写起租时间" style="width: 100%" />
      </el-form-item>
      <el-form-item label="结租时间" prop="endTime" v-if="form.leaseType === 'H'">
        <el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="必填，请填写结租时间" style="width: 100%" />
      </el-form-item>
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
import { updateOwnerCar } from '@/api/car/carApi'
import { listDicts } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditCar',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      carTypes: [],
      form: {
        carId: '',
        memberId: '',
        carNum: '',
        carType: '',
        leaseType: '',
        startTime: '',
        endTime: '',
        remark: ''
      },
      rules: {
        carNum: [{ required: true, message: '车牌号不能为空', trigger: 'blur' }],
        carType: [{ required: true, message: '车类型不能为空', trigger: 'change' }],
        leaseType: [{ required: true, message: '车牌类型不能为空', trigger: 'change' }],
        startTime: [{ required: true, message: '起租时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, message: '结租时间不能为空', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadCarTypes()
  },
  methods: {
    async loadCarTypes() {
      try {
        const res = await listDicts({ page: 1, row: 100, tableName: 'owner_car', columnName: 'car_type' })
        this.carTypes = res.data || []
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
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await updateOwnerCar(this.form)
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
