<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="重置密码"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="新密码" prop="cardNumber">
        <el-input v-model="form.cardNumber" placeholder="请输入新密码" maxlength="64" show-word-limit />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { resetLockPwd } from '@/api/lock/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ResetLockPwd',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        cardNumber: ''
      },
      rules: {
        cardNumber: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
          { max: 64, message: '密码长度不能超过64', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => {
          this.form.cardNumber = ''
          this.$refs.formRef && this.$refs.formRef.clearValidate()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await resetLockPwd({ ...this.editData, cardNumber: this.form.cardNumber, communityId })
        this.$message.success('密码重置成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '重置密码失败')
      } finally {
        this.loading = false
      }
    },
    handleClose() {
      this.form = { cardNumber: '' }
    }
  }
}
</script>
