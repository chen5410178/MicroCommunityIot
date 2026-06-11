<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改通行码设置"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="qrcodeName">
        <el-input v-model="form.qrcodeName" placeholder="必填，请填写名称" maxlength="128" />
      </el-form-item>
      <el-form-item label="验证" prop="smsValidate">
        <el-select v-model="form.smsValidate" placeholder="必填，请选择验证" style="width: 100%">
          <el-option label="是" value="ON" />
          <el-option label="否" value="OFF" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核" prop="audit">
        <el-select v-model="form.audit" placeholder="必填，请选择审核" style="width: 100%">
          <el-option label="是" value="ON" />
          <el-option label="否" value="OFF" />
        </el-select>
      </el-form-item>
      <el-form-item label="失效时间" prop="expired">
        <el-input v-model="form.expired" type="number" placeholder="必填，请填写失效时间(分钟)" />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择状态" style="width: 100%">
          <el-option label="启用" value="ON" />
          <el-option label="停用" value="OFF" />
        </el-select>
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写说明" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updatePassQrcodeSetting } from '@/api/accessControl/accessControlApi'

export default {
  name: 'EditPassQrcodeSetting',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        pqsId: '',
        qrcodeName: '',
        smsValidate: '',
        audit: '',
        expired: '',
        state: '',
        remark: ''
      },
      rules: {
        qrcodeName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 128, message: '名称不能超过128', trigger: 'blur' }
        ],
        smsValidate: [{ required: true, message: '请选择验证', trigger: 'change' }],
        audit: [{ required: true, message: '请选择审核', trigger: 'change' }],
        expired: [{ required: true, message: '失效时间不能为空', trigger: 'blur' }],
        state: [{ required: true, message: '请选择状态', trigger: 'change' }],
        remark: [{ max: 512, message: '说明不能超过512', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          pqsId: this.editData.pqsId || '',
          qrcodeName: this.editData.qrcodeName || '',
          smsValidate: this.editData.smsValidate || '',
          audit: this.editData.audit || '',
          expired: this.editData.expired != null ? String(this.editData.expired) : '',
          state: this.editData.state || '',
          remark: this.editData.remark || ''
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
        await updatePassQrcodeSetting(data)
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
