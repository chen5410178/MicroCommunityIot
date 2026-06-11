<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改组织"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="组织名称" prop="orgName">
        <el-input v-model="form.orgName" placeholder="必填，请填写组织名称" />
      </el-form-item>
      <el-form-item label="上级组织">
        <el-input v-model="parentOrgName" disabled placeholder="自动填充" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="可填，请填写描述" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateOrg } from '@/api/frame/orgApi'

export default {
  name: 'EditOrg',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      parentOrgName: '',
      form: {
        orgId: '',
        orgName: '',
        parentOrgId: '',
        description: ''
      },
      rules: {
        orgName: [{ required: true, message: '组织名称不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          orgId: this.editData.orgId || this.editData.id || '',
          orgName: this.editData.orgName || this.editData.label || '',
          parentOrgId: this.editData.parentOrgId || '',
          description: this.editData.description || ''
        }
        this.parentOrgName = this.editData.parentOrgName || ''
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
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const community = this.$store.state.app.currentCommunity || {}
        await updateOrg({ ...this.form, communityId: community.communityId })
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
