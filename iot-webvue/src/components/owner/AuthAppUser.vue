<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="审核"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" ref="formRef" label-width="110px">
      <el-form-item label="人员名称">
        <el-input v-model="form.ownerName" disabled />
      </el-form-item>
      <el-form-item label="联系人手机号">
        <el-input v-model="form.link" disabled />
      </el-form-item>
      <el-form-item label="房屋名称">
        <el-input v-model="form.roomName" disabled />
      </el-form-item>
      <el-form-item label="状态类型" prop="state" :rules="[{ required: true, message: '请选择状态类型', trigger: 'change' }]">
        <el-select v-model="form.state" placeholder="必填，请选择状态类型" style="width: 100%">
          <el-option label="审核成功" value="12000" />
          <el-option label="审核失败" value="13000" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核说明">
        <el-input v-model="form.stateMsg" type="textarea" :rows="3" placeholder="选填，请填写审核说明" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateAppUser } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AuthAppUser',
  props: {
    visible: { type: Boolean, default: false },
    appUserData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { ownerName: '', link: '', roomName: '', state: '', stateMsg: '', auId: '' }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.appUserData) {
        this.form = {
          auId: this.appUserData.auId,
          ownerName: this.appUserData.ownerName || '',
          link: this.appUserData.link || '',
          roomName: this.appUserData.roomName || '',
          state: '',
          stateMsg: ''
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
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        await updateAppUser({
          auId: this.form.auId,
          state: this.form.state,
          stateMsg: this.form.stateMsg,
          communityId: community ? community.communityId : ''
        })
        this.$message.success('审核成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '审核失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
