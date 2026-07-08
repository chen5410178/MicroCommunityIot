<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="重启确认"
    width="400px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #e6a23c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要重启充电桩 <strong>{{ restartData && restartData.machineName }}</strong> 吗？
      </p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="warning" @click="handleSubmit" :loading="loading">确定重启</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { restartChargeMachine } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RestartChargeMachine',
  props: {
    visible: { type: Boolean, default: false },
    restartData: { type: Object, default: null }
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await restartChargeMachine({ machineId: this.restartData.machineId, communityId })
        this.$message.success('重启成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '重启失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
