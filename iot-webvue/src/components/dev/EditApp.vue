<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改应用"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="应用名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写应用名称" />
      </el-form-item>
      <el-form-item label="秘钥" prop="securityCode">
        <el-input v-model="form.securityCode" placeholder="选填，请填写秘钥" />
      </el-form-item>
      <el-form-item label="白名单" prop="whileListIp">
        <el-input v-model="form.whileListIp" placeholder="选填，请填写白名单" />
      </el-form-item>
      <el-form-item label="黑名单" prop="blackListIp">
        <el-input v-model="form.blackListIp" placeholder="选填，请填写黑名单" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateApp } from '@/api/dev/devApi'

export default {
  name: 'EditApp',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        appId: '',
        name: '',
        securityCode: '',
        whileListIp: '',
        blackListIp: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '应用名称不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '应用名称必须在2至50字符之间', trigger: 'blur' }
        ],
        securityCode: [{ max: 64, message: '秘钥太长超过64位', trigger: 'blur' }],
        whileListIp: [{ max: 200, message: '白名单内容不能超过200', trigger: 'blur' }],
        blackListIp: [{ max: 200, message: '黑名单内容不能超过200', trigger: 'blur' }],
        remark: [{ max: 200, message: '备注内容不能超过200', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
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
        await updateApp(this.form)
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
