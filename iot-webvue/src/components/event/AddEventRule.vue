<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加事件规则"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="form.ruleName" placeholder="必填，请填写规则名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="超时时间(分钟)" prop="limitTime">
        <el-input-number v-model="form.limitTime" :min="1" placeholder="必填，请填写超时时间" style="width: 100%" />
      </el-form-item>
      <el-form-item label="通知模板" prop="templateId">
        <el-select v-model="form.templateId" placeholder="必填，请选择通知模板" style="width: 100%">
          <el-option v-for="item in templateList" :key="item.templateId" :label="item.templateName" :value="item.templateId" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写备注" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveEventRule } from '@/api/event/eventApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddEventRule',
  props: {
    visible: { type: Boolean, default: false },
    templateList: { type: Array, default: () => [] }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        ruleName: '',
        limitTime: null,
        templateId: '',
        remark: ''
      },
      rules: {
        ruleName: [
          { required: true, message: '规则名称不能为空', trigger: 'blur' },
          { max: 64, message: '规则名称不能超过64', trigger: 'blur' }
        ],
        limitTime: [
          { required: true, message: '超时时间不能为空', trigger: 'blur' },
          { type: 'number', message: '超时时间必须为正整数', trigger: 'blur' }
        ],
        templateId: [
          { required: true, message: '请选择通知模板', trigger: 'change' }
        ],
        remark: [
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
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
        const communityId = (getCurrentCommunity() || {}).communityId
        await saveEventRule({ ...this.form, communityId })
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
