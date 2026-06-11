<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加公众号"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写公众号名称" maxlength="100" />
      </el-form-item>
      <el-form-item label="APPID" prop="appId">
        <el-input v-model="form.appId" placeholder="必填，请填写APPID" maxlength="100" />
      </el-form-item>
      <el-form-item label="应用密钥" prop="appSecret">
        <el-input v-model="form.appSecret" placeholder="必填，请填写应用密钥" maxlength="200" />
      </el-form-item>
      <el-form-item label="描述" prop="remarks">
        <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="选填，请填写描述" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { savePublicWeChat } from '@/api/property/publicWeChatApi'

export default {
  name: 'AddPublicWeChat',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        name: '',
        appId: '',
        appSecret: '',
        remarks: ''
      },
      rules: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 100, message: '名称不能超过100个字符', trigger: 'blur' }
        ],
        appId: [
          { required: true, message: 'APPID不能为空', trigger: 'blur' },
          { max: 100, message: 'APPID不能超过100个字符', trigger: 'blur' }
        ],
        appSecret: [
          { required: true, message: '应用密钥不能为空', trigger: 'blur' },
          { max: 200, message: '应用密钥不能超过200个字符', trigger: 'blur' }
        ],
        remarks: [
          { max: 512, message: '描述不能超过512个字符', trigger: 'blur' }
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
      const communityId = this.$store.state.app.currentCommunity.communityId
      try {
        await savePublicWeChat({
          ...this.form,
          weChatType: '1100',
          objId: communityId,
          objType: '1000'
        })
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
