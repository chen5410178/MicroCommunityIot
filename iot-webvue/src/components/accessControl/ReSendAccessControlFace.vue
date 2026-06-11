<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="重新下发确认"
    width="400px"
    append-to-body
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #e6a23c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要重新下发人员数据到 <strong>{{ resendData && resendData.machineName }}</strong> 吗？
      </p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定下发</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { reSendAccessControlFace } from '@/api/accessControl/accessControlApi'

export default {
  name: 'ReSendAccessControlFace',
  props: {
    visible: { type: Boolean, default: false },
    resendData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleSubmit() {
      this.loading = true
      try {
        await reSendAccessControlFace({ machineId: this.resendData.machineId })
        this.$message.success('重新下发成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '重新下发失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
