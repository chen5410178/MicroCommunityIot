<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="审核通行码"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称">
        <el-input v-model="form.personName" disabled />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.personTel" disabled />
      </el-form-item>
      <el-form-item label="审核状态" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择" style="width: 100%">
          <el-option label="同意" value="C" />
          <el-option label="拒绝" value="F" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核说明" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="必填，请填写审核说明" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { auditPassQrcode } from '@/api/accessControl/accessControlApi'

export default {
  name: 'AuditPassQrcode',
  props: {
    visible: { type: Boolean, default: false },
    auditData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        pqId: '',
        personName: '',
        personTel: '',
        state: '',
        remark: ''
      },
      rules: {
        state: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
        remark: [{ required: true, message: '审核说明不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.auditData) {
        this.form = {
          pqId: this.auditData.pqId || '',
          personName: this.auditData.personName || '',
          personTel: this.auditData.personTel || '',
          state: '',
          remark: ''
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
        const data = {
          ...this.form,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        await auditPassQrcode(data)
        this.$message.success('审核成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '审核失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
