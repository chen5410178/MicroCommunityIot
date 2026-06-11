<template>
  <el-dialog title="请确认您的操作" :visible.sync="visible" width="500px" :close-on-click-modal="false">
    <div style="text-align: center; padding: 20px 0; font-size: 15px;">
      先用停止充电方式停止，如果设备没有反映，并且设备也是空闲状态，那么用异常停止方式停止
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">点错了</el-button>
      <el-button type="primary" :loading="submitting" @click="handleConfirm">确认停止!</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { throwStopCharge } from '@/api/charge/chargeApi'

export default {
  name: 'ThrowStopChargeMachine',
  data() {
    return {
      visible: false,
      submitting: false,
      orderData: {}
    }
  },
  methods: {
    open(row) {
      this.orderData = row
      this.visible = true
    },
    async handleConfirm() {
      this.submitting = true
      try {
        const res = await throwStopCharge({
          orderId: this.orderData.orderId,
          communityId: this.orderData.communityId
        })
        if (res.code === 0) {
          this.visible = false
          this.$message.success('异常停止成功')
          this.$emit('success')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      } catch (e) {
        this.$message.error('操作失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>
