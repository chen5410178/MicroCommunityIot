<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加厂家规格"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="规格" prop="specCd">
        <el-input v-model="form.specCd" placeholder="必填，请填写规格" maxlength="12" />
      </el-form-item>
      <el-form-item label="规格值" prop="value">
        <el-input v-model="form.value" placeholder="必填，请填写规格值" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveHardwareManufacturerAttr } from '@/api/accessControl/hardwareManufacturerApi'

export default {
  name: 'AddHardwareManufacturerAttr',
  props: {
    visible: { type: Boolean, default: false },
    hmId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        specCd: '',
        value: ''
      },
      rules: {
        specCd: [
          { required: true, message: '规格不能为空', trigger: 'blur' },
          { max: 12, message: '规格不能超过12', trigger: 'blur' }
        ],
        value: [
          { required: true, message: '规格值不能为空', trigger: 'blur' },
          { max: 200, message: '规格值不能超过200', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form = { specCd: '', value: '' }
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
        await saveHardwareManufacturerAttr({ ...this.form, hmId: this.hmId })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
