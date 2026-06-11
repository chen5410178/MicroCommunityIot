<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="格式化小区数据"
    width="500px"
    @close="handleClose"
  >
    <div class="format-warning">
      <p style="color: #f56c6c; margin-bottom: 16px;">
        请慎重操作，此操作将清空所有【{{ community.name }}】小区数据，小区编码为【{{ community.communityId }}】。谨慎期间，请再次跟相关人员核实确认！
      </p>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
      <el-form-item prop="devPassword">
        <el-input v-model="form.devPassword" type="password" placeholder="请输入开发者密码" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认格式化</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteBuildingUnit } from '@/api/dev/devApi'

export default {
  name: 'ChooseInitializeCommunity',
  props: {
    visible: { type: Boolean, default: false },
    community: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        devPassword: ''
      },
      rules: {
        devPassword: [
          { required: true, message: '请输入开发者密码', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
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
        await deleteBuildingUnit({
          communityId: this.community.communityId,
          devPassword: this.form.devPassword
        })
        this.$message.success('格式化成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '格式化失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
