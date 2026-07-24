<template>
  <el-dialog :visible.sync="dialogVisible" title="密码确认" width="500px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="密码" prop="curPasswd">
        <el-input v-model="form.curPasswd" type="password" placeholder="必填，请输入当前账号密码" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">登录</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listStoreStaff, adminLoginProperty } from '@/api/admin/propertyCompanyApi'

export default {
  name: 'AdminLoginProperty',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        username: '',
        userId: '',
        curPasswd: '',
        curUserName: ''
      },
      rules: {
        curPasswd: [{ required: true, message: '当前用户密码不能为空', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async open(row) {
      this.dialogVisible = true
      this.form.curPasswd = ''
      try {
        const res = await listStoreStaff({ page: 1, row: 1, adminFlag: 'Y', storeId: row.storeId })
        if (res.data && res.data.length > 0) {
          this.form.username = res.data[0].staffName
          this.form.userId = res.data[0].staffId
          const userStr = sessionStorage.getItem('iot_webvue_user')
          const userInfo = userStr ? JSON.parse(userStr) : {}
          this.form.curUserName = userInfo.name || ''
        }
      } catch { /* ignore */ }
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
        const res = await adminLoginProperty(this.form)
        this.$message.success('登录成功')
        this.$emit('success', res)
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '登录失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
