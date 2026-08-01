<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加成员车辆"
    width="550px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="车牌号" prop="carNum">
        <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="12" />
      </el-form-item>
      <el-form-item label="车类型" prop="carType">
        <el-select v-model="form.carType" placeholder="必填，请选择车辆类型" style="width: 100%">
          <el-option v-for="item in carTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写备注" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveMemberCar } from '@/api/car/carApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMemberCar',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      carTypes: [],
      carId: '',
      form: {
        carNum: '',
        carType: '',
        remark: ''
      },
      rules: {
        carNum: [{ required: true, message: '车牌号不能为空', trigger: 'blur' }],
        carType: [{ required: true, message: '车类型不能为空', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  mounted() {
    this.loadCarTypes()
  },
  methods: {
    async loadCarTypes() {
      try {
        const res = await listDicts({ tableName: 'owner_car', tableColumns: 'car_type' })
        this.carTypes = res || []
      } catch { /* ignore */ }
    },
    open(carData) {
      this.carId = carData.carId || ''
      this.form = { carNum: '', carType: '', remark: '' }
      this.dialogVisible = true
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
        const data = {
          ...this.form,
          carId: this.carId,
          communityId: (getCurrentCommunity() || {}).communityId || ''
        }
        const res = await saveMemberCar(data)
        if (res.code === 0) {
          this.$message.success('添加成功')
          this.$emit('success')
          this.handleClose()
        } else {
          this.$message.error(res.msg || '添加失败')
        }
      } catch {
        this.$message.error('添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
