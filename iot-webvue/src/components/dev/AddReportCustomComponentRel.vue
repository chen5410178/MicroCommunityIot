<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="关联组件"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="组件" prop="componentId">
        <el-select v-model="form.componentId" placeholder="必填，请选择组件" style="width: 100%">
          <el-option
            v-for="item in componentList"
            :key="item.componentId"
            :label="item.name"
            :value="item.componentId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="组件序号" prop="seq">
        <el-input v-model="form.seq" placeholder="必填，请填写组件序号" maxlength="11" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveReportCustomComponentRel, listReportCustomComponents } from '@/api/dev/devApi'

export default {
  name: 'AddReportCustomComponentRel',
  props: {
    visible: { type: Boolean, default: false },
    customId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      componentList: [],
      form: {
        componentId: '',
        customId: '',
        seq: ''
      },
      rules: {
        componentId: [
          { required: true, message: '组件不能为空', trigger: 'change' }
        ],
        seq: [
          { required: true, message: '组件序号不能为空', trigger: 'blur' },
          { max: 11, message: '组件序号不能超过11', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form.customId = this.customId
        this.loadComponentList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadComponentList() {
      try {
        const res = await listReportCustomComponents({ page: 1, row: 50 })
        this.componentList = res.data || []
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
        await saveReportCustomComponentRel(this.form)
        this.$message.success('关联成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '关联失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
