<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改服务注册"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="应用ID" prop="appId">
        <el-input v-model="form.appId" disabled />
      </el-form-item>
      <el-form-item label="服务ID" prop="serviceId">
        <el-input v-model="form.serviceId" disabled />
      </el-form-item>
      <el-form-item label="订单类型" prop="orderTypeCd">
        <el-select v-model="form.orderTypeCd" placeholder="请选择订单类型" style="width: 100%">
          <el-option label="查询" value="Q" />
          <el-option label="业务受理" value="D" />
        </el-select>
      </el-form-item>
      <el-form-item label="调用次数" prop="invokeLimitTimes">
        <el-input v-model="form.invokeLimitTimes" placeholder="必填，请填写调用次数" />
      </el-form-item>
      <el-form-item label="调用方式" prop="invokeModel">
        <el-select v-model="form.invokeModel" placeholder="请选择调用方式" style="width: 100%">
          <el-option label="同步方式" value="S" />
          <el-option label="异步方式" value="A" />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateServiceRegister } from '@/api/dev/devApi'

export default {
  name: 'EditServiceRegister',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        id: '',
        appId: '',
        serviceId: '',
        orderTypeCd: '',
        invokeLimitTimes: 1000,
        invokeModel: ''
      },
      rules: {
        orderTypeCd: [
          { required: true, message: '请选择订单类型', trigger: 'change' }
        ],
        invokeLimitTimes: [
          { required: true, message: '调用次数不能为空', trigger: 'blur' },
          { pattern: /^\d+$/, message: '调用次数必须为数字', trigger: 'blur' }
        ],
        invokeModel: [
          { required: true, message: '请选择调用方式', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          id: this.editData.id || '',
          appId: this.editData.appId || '',
          serviceId: this.editData.serviceId || '',
          orderTypeCd: this.editData.orderTypeCd || '',
          invokeLimitTimes: this.editData.invokeLimitTimes || 1000,
          invokeModel: this.editData.invokeModel || ''
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
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
        await updateServiceRegister(this.form)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
