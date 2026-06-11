<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改权限"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="权限ID" prop="pId">
        <el-input v-model="form.pId" placeholder="必填，最大30字符" maxlength="30" disabled />
      </el-form-item>
      <el-form-item label="权限名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，最大50字符" maxlength="50" />
      </el-form-item>
      <el-form-item label="资源路径" prop="resource">
        <el-input v-model="form.resource" placeholder="必填，最大200字符" maxlength="200" />
      </el-form-item>
      <el-form-item label="权限类型" prop="domain">
        <el-select v-model="form.domain" placeholder="必填，请选择权限类型" style="width: 100%">
          <el-option label="运营团队" value="800900000001" />
          <el-option label="物业" value="800900000003" />
          <el-option label="开发团队" value="800900000000" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="选填，最大200字符" maxlength="200" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateBasePrivilege } from '@/api/dev/devApi'

export default {
  name: 'EditBasePrivilege',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        pId: '',
        name: '',
        resource: '',
        domain: '',
        description: '',
        mId: ''
      },
      rules: {
        pId: [
          { required: true, message: '权限ID不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '权限名称不能为空', trigger: 'blur' },
          { max: 50, message: '权限名称不能超过50', trigger: 'blur' }
        ],
        resource: [
          { required: true, message: '资源路径不能为空', trigger: 'blur' },
          { max: 200, message: '资源路径不能超过200', trigger: 'blur' }
        ],
        domain: [
          { required: true, message: '权限类型不能为空', trigger: 'change' }
        ],
        description: [
          { max: 200, message: '备注内容不能超过200', trigger: 'blur' }
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
        await updateBasePrivilege(this.form)
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
