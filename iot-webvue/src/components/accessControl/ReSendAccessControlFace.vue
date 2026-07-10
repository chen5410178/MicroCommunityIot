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
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ReSendAccessControlFace',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      resendData: {}
    }
  },
  methods: {
    open(row) {
      this.resendData = row
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        await reSendAccessControlFace({ ...this.resendData, communityId })
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
