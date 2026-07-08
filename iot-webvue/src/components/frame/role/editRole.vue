<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改角色"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，2-10位" maxlength="10" />
      </el-form-item>
      <el-form-item label="角色描述" prop="description">
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
import { updatePrivilegeGroup } from '@/api/frame/roleApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditRole',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        pgId: '',
        name: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' },
          { min: 2, max: 10, message: '角色名称长度必须在2位至10位', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '角色描述长度不能超过200位', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          pgId: this.editData.pgId || '',
          name: this.editData.name || '',
          description: this.editData.description || ''
        }
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
        const community = getCurrentCommunity() || {}
        await updatePrivilegeGroup({ ...this.form, communityId: community.communityId })
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
