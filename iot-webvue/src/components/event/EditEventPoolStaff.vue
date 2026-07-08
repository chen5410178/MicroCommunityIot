<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="处理事件"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="事件ID">
        <el-input :value="epsData.eventId" disabled />
      </el-form-item>
      <el-form-item label="员工">
        <el-input :value="epsData.staffName" disabled />
      </el-form-item>
      <el-form-item label="事故原因" prop="eventReason">
        <el-input v-model="form.eventReason" type="textarea" :rows="3" placeholder="必填" maxlength="512" />
      </el-form-item>
      <el-form-item label="处理方案" prop="eventResult">
        <el-input v-model="form.eventResult" type="textarea" :rows="3" placeholder="必填" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateEventPoolStaff } from '@/api/event/eventApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditEventPoolStaff',
  props: {
    visible: { type: Boolean, default: false },
    epsData: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        eventReason: '',
        eventResult: ''
      },
      rules: {
        eventReason: [{ required: true, message: '事故原因不能为空', trigger: 'blur' }],
        eventResult: [{ required: true, message: '处理方案不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form = { eventReason: '', eventResult: '' }
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateEventPoolStaff({
          epsId: this.epsData.epsId,
          eventReason: this.form.eventReason,
          eventResult: this.form.eventResult,
          state: 'D',
          communityId
        })
        this.$message.success('处理成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '处理失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
