<template>
  <el-dialog :visible.sync="dialogVisible" title="请确认您的操作" width="400px">
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">确定退出小区!</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">点错了</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认退出</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { quitCommunity } from '@/api/admin/propertyCommunityApi'

export default {
  name: 'DeletePropertyCommunity',
  props: {
    delData: { type: Object, default: null },
    storeId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false
    }
  },
  methods: {
    open() {
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.loading = true
      try {
        const data = { ...this.delData, memberId: this.storeId }
        await quitCommunity(data)
        this.$message.success('退出成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '退出失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
