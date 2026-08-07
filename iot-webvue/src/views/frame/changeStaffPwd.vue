<template>
  <div class="change-pwd-page">
    <div class="change-pwd-card">
      <h3>修改密码</h3>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="原始密码" prop="oldPwd">
          <el-input
            v-model="form.oldPwd"
            type="password"
            show-password
            placeholder="请输入原始密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input
            v-model="form.newPwd"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="reNewPwd">
          <el-input
            v-model="form.reNewPwd"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        <el-form-item>
          <div class="btn-wrap">
            <el-button type="primary" :loading="loading" @click="handleSubmit">
              确认修改
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { changeStaffPwd } from '@/api/user/userApi'

export default {
  name: 'ChangeStaffPwd',
  data() {
    const validateReNewPwd = (rule, value, callback) => {
      if (value !== this.form.newPwd) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        oldPwd: '',
        newPwd: '',
        reNewPwd: ''
      },
      rules: {
        oldPwd: [
          { required: true, message: '原始密码不能为空', trigger: 'blur' }
        ],
        newPwd: [
          { required: true, message: '新密码不能为空', trigger: 'blur' }
        ],
        reNewPwd: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { validator: validateReNewPwd, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.loading = true
        changeStaffPwd({
          oldPwd: this.form.oldPwd,
          newPwd: this.form.newPwd,
          reNewPwd: this.form.reNewPwd
        }).then(res => {
          if (!res || res.code !== 0) {
            this.loading = false
            this.$message.error((res && res.msg) || '修改失败')
            return
          }
          this.$message.success('修改成功，请重新登录')
          this.form = { oldPwd: '', newPwd: '', reNewPwd: '' }
          sessionStorage.clear()
          this.$router.push('/pages/frame/login')
        }).catch(err => {
          this.loading = false
          const msg = err && err.msg ? err.msg : '修改失败'
          this.$message.error(msg)
        })
      })
    }
  }
}
</script>

<style scoped>
.change-pwd-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}
.change-pwd-card {
  width: 500px;
  padding: 40px 50px 30px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.change-pwd-card h3 {
  text-align: center;
  font-size: 24px;
  color: #303133;
  margin: 0 0 30px;
}
.btn-wrap {
  width: 100%;
  text-align: right;
}
</style>
