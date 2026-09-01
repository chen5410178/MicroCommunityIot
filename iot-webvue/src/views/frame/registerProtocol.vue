<template>
  <div class="register-protocol">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>注册协议</span>
      </div>
      <div v-loading="loading">
        <el-form ref="formRef" :model="form" label-width="100px">
          <el-form-item label="用户协议">
            <p class="input-tip">请直接输入 HTML 内容</p>
            <el-input v-model="form.userProtocol" type="textarea" :rows="15" placeholder="请填写用户协议" />
          </el-form-item>
          <el-form-item label="隐私保护指引">
            <p class="input-tip">请直接输入 HTML 内容</p>
            <el-input v-model="form.merchantProtocol" type="textarea" :rows="15" placeholder="请填写隐私保护指引" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listSystemInfo, updateSystemInfo } from '@/api/login/systemApi'

export default {
  name: 'RegisterProtocol',
  data() {
    return {
      loading: false,
      submitting: false,
      form: {
        systemId: '',
        userProtocol: '',
        merchantProtocol: ''
      }
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await listSystemInfo({ page: 1, row: 1 })
        if (res.data && res.data.length > 0) {
          this.form = { ...res.data[0] }
        }
      } finally {
        this.loading = false
      }
    },
    async handleSubmit() {
      this.submitting = true
      try {
        const res = await updateSystemInfo({
          systemId: this.form.systemId,
          userProtocol: this.form.userProtocol,
          merchantProtocol: this.form.merchantProtocol
        })
        if (res.code != 0) {
          throw new Error(res.msg || '修改失败')
        }
        this.$message.success('修改成功')
        this.fetchData()
      } catch (e) {
        this.$message.error(e.msg || e.message || '修改失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.input-tip { color: #909399; font-size: 12px; margin: 0 0 4px 0; }
</style>
