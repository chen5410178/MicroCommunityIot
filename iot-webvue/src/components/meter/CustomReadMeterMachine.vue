<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="手工抄表"
    width="480px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #e6a23c" />
      <p style="margin-top: 20px; font-size: 14px; color: #606266;">
        此操作为手工触发抄表方式，您也可以等到抄表日，系统自动抄表！
      </p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定抄表</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { customMeterMachineRead } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'CustomReadMeterMachine',
  props: {
    visible: { type: Boolean, default: false }
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
        const community = getCurrentCommunity()
        await customMeterMachineRead({ communityId: community.communityId })
        this.$message.success('抄表请求已发送，表反馈速度可能有延时，请您刷新查看！')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
