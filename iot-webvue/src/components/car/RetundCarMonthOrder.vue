<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="退款确认"
    width="400px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定退款订单 <strong>{{ orderData && orderData.orderId }}</strong> 吗？
      </p>
      <p style="color: #909399; font-size: 14px; margin-top: 8px;">此操作不可恢复</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">点错了</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认退款!</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { refundCarMonthOrder } from '@/api/car/carApi'

export default {
  name: 'RetundCarMonthOrder',
  props: {
    visible: { type: Boolean, default: false },
    orderData: { type: Object, default: null }
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
        const params = {
          orderId: this.orderData.orderId,
          communityId: this.orderData.communityId || this.$store.state.app.currentCommunity.communityId || ''
        }
        await refundCarMonthOrder(params)
        this.$message.success('退款成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '退款失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
