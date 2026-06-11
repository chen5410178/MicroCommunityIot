<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加监控模型"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="模型名称" prop="modelName">
        <el-input v-model="form.modelName" placeholder="必填，请填写模型名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="模型处理类" prop="modelAdapt">
        <el-input v-model="form.modelAdapt" placeholder="必填，请填写模型处理类" maxlength="64" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveMonitorModel } from '@/api/monitor/monitorApi'

export default {
  name: 'AddMonitorModel',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        modelName: '',
        modelAdapt: ''
      },
      rules: {
        modelName: [
          { required: true, message: '模型名称不能为空', trigger: 'blur' },
          { max: 200, message: '模型名称不能超过200', trigger: 'blur' }
        ],
        modelAdapt: [
          { required: true, message: '模型处理类不能为空', trigger: 'blur' },
          { max: 64, message: '模型处理类不能超过64', trigger: 'blur' }
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveMonitorModel({ ...this.form, communityId })
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
