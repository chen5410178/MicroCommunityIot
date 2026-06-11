<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加商家"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="店铺名称" prop="shopName">
        <el-input v-model="form.shopName" placeholder="必填，请填写店铺名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="联系电话" prop="link">
        <el-input v-model="form.link" placeholder="必填，请填写联系电话" maxlength="11" />
      </el-form-item>
      <el-form-item label="登录密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="必填，请填写登录密码" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveCommunityShop } from '@/api/coupon/couponApi'

export default {
  name: 'AddCommunityShop',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        shopName: '',
        link: '',
        password: ''
      },
      rules: {
        shopName: [
          { required: true, message: '店铺名称不能为空', trigger: 'blur' },
          { max: 64, message: '不超过64字符', trigger: 'blur' }
        ],
        link: [
          { required: true, message: '联系电话不能为空', trigger: 'blur' },
          { max: 11, message: '不超过11位', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '登录密码不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
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
        const communityId = this.$store.state.app.currentCommunity.communityId
        await saveCommunityShop({ ...this.form, communityId })
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
