<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改报表组"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="组名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写组名称" maxlength="128" />
      </el-form-item>
      <el-form-item label="组url" prop="url">
        <el-input v-model="form.url" placeholder="必填，请填写组url" maxlength="512" />
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="必填，请填写描述" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateReportCustomGroup } from '@/api/dev/devApi'

export default {
  name: 'EditReportCustomGroup',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        groupId: '',
        name: '',
        url: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '组名称不能为空', trigger: 'blur' },
          { max: 128, message: '组名称不能超过128', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '组url不能为空', trigger: 'blur' },
          { max: 512, message: '组url不能超过512', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '描述不能为空', trigger: 'blur' },
          { max: 512, message: '描述不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
      }
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
        await updateReportCustomGroup(this.form)
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
