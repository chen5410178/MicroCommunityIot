<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除确认"
    width="400px"
    append-to-body
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">确定取消楼栋授权</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">点错了</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确认删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteAccessControlFloor } from '@/api/accessControl/accessControlApi'

export default {
  name: 'DeleteAccessControlFloor',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      rowData: null
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    open(row) {
      this.rowData = row
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.loading = true
      try {
        const community = this.$store.state.app.currentCommunity || {}
        await deleteAccessControlFloor({
          acfId: this.rowData.acfId,
          communityId: community.communityId
        })
        this.$message.success('取消授权成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '取消授权失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
