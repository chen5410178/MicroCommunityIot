<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改场景"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="场景名称" prop="sceneName">
        <el-input v-model="form.sceneName" placeholder="必填，请填写场景名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择状态" style="width: 100%">
          <el-option label="启用中" value="Y" />
          <el-option label="停用中" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="场景顺序" prop="seq">
        <el-input v-model="form.seq" placeholder="必填，请填写场景顺序" maxlength="12" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateDtScene } from '@/api/admin/dtSceneApi'

export default {
  name: 'EditDtScene',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        sceneId: '',
        sceneName: '',
        state: '',
        seq: '',
        communityId: ''
      },
      rules: {
        sceneName: [
          { required: true, message: '场景名称不能为空', trigger: 'blur' },
          { max: 64, message: '场景名称不能超过64', trigger: 'blur' }
        ],
        state: [
          { required: true, message: '状态不能为空', trigger: 'change' }
        ],
        seq: [
          { required: true, message: '场景顺序不能为空', trigger: 'blur' },
          { max: 12, message: '场景顺序不能超过12', trigger: 'blur' }
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
        await updateDtScene(this.form)
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
