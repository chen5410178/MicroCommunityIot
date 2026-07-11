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
import { updateOrg, listOrgs } from '@/api/frame/orgApi'
import { getCurrentCommunity } from '@/utils/auth'

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
        orgLevel: '',
        parentOrgId: '',
        parentOrgName: '',
        description: '',
        belongCommunityId: ''
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
        this.loadOrgDetail()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadOrgDetail() {
      const orgId = this.editData.orgId || this.editData.id || ''
      if (!orgId) return
      try {
        const community = getCurrentCommunity() || {}
        const res = await listOrgs({ page: 1, row: 1, orgId, communityId: community.communityId })
        const orgData = res.data && res.data.length > 0 ? res.data[0] : null
        if (orgData) {
          this.form = {
            orgId: orgData.orgId || '',
            orgName: orgData.orgName || '',
            orgLevel: orgData.orgLevel || '',
            parentOrgId: orgData.parentOrgId || '',
            parentOrgName: orgData.parentOrgName || '',
            description: orgData.description || '',
            belongCommunityId: orgData.belongCommunityId || ''
          }
          this.parentOrgName = orgData.parentOrgName || ''
        }
      } catch (e) {
        this.$message.error(e.msg || '查询组织详情失败')
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity() || {}
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
