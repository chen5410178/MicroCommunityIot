<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改任务"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="任务名称" prop="taskName">
        <el-input v-model="form.taskName" placeholder="必填，2-50字符" maxlength="50" />
      </el-form-item>
      <el-form-item label="选择模板" prop="templateId">
        <el-select v-model="form.templateId" placeholder="必填，请选择模板" style="width: 100%" @change="handleTemplateChange">
          <el-option
            v-for="tpl in templates"
            :key="tpl.templateId"
            :label="tpl.templateName"
            :value="tpl.templateId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="定时时间" prop="taskCron">
        <el-input v-model="form.taskCron" placeholder="必填，Cron表达式" maxlength="50" />
      </el-form-item>
      <el-form-item
        v-for="spec in form.templateSpecs"
        :key="spec.specCd"
        :label="spec.specName"
        :prop="'templateSpec_' + spec.specCd"
      >
        <el-input v-model="spec.value" :placeholder="'请输入' + spec.specName" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateTask, listTaskTemplate, listTaskTemplateSpec } from '@/api/dev/devApi'

export default {
  name: 'EditJob',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      templates: [],
      form: {
        taskId: '',
        taskName: '',
        templateId: '',
        taskCron: '',
        taskAttr: [],
        templateSpecs: []
      },
      rules: {
        taskName: [
          { required: true, message: '任务名称不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '任务名称必须在2-50字符之间', trigger: 'blur' }
        ],
        templateId: [
          { required: true, message: '模板不能为空', trigger: 'change' }
        ],
        taskCron: [
          { required: true, message: '定时不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form.taskId = this.editData.taskId || ''
        this.form.taskName = this.editData.taskName || ''
        this.form.templateId = this.editData.templateId || ''
        this.form.taskCron = this.editData.taskCron || ''
        this.form.taskAttr = this.editData.taskAttr || []
        this.loadTemplates().then(() => {
          this.loadSpecs(this.form.templateId)
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    resetForm() {
      this.form = {
        taskId: '',
        taskName: '',
        templateId: '',
        taskCron: '',
        taskAttr: [],
        templateSpecs: []
      }
    },
    async loadTemplates() {
      try {
        const res = await listTaskTemplate({ page: 1, row: 30 })
        this.templates = res.data || []
      } catch { /* ignore */ }
    },
    async loadSpecs(templateId) {
      if (!templateId) {
        this.form.templateSpecs = []
        return
      }
      try {
        const res = await listTaskTemplateSpec({ page: 1, row: 30, templateId })
        const data = res.data || []
        data.forEach(item => {
          item.value = ''
          const taskAttr = this.form.taskAttr || []
          taskAttr.forEach(attr => {
            if (item.specCd === attr.specCd && item.templateId === templateId) {
              item.value = attr.value
              item.attrId = attr.attrId
            }
          })
        })
        this.form.templateSpecs = data
      } catch { /* ignore */ }
    },
    handleTemplateChange(templateId) {
      this.loadSpecs(templateId)
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.resetForm()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        await updateTask(this.form)
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
