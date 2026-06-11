<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="取消授权确认"
    width="400px"
    append-to-body
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要取消门禁 <strong>{{ delData && delData.machineName }}</strong> 的授权吗？
      </p>
      <p style="color: #909399; font-size: 14px; margin-top: 8px;">此操作不可恢复</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteAccessControlAdRel } from '@/api/accessControl/accessControlApi'

export default {
  name: 'DeleteAccessControlAdRel',
  props: {
    visible: { type: Boolean, default: false },
    delData: { type: Object, default: null }
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
        await deleteAccessControlAdRel({
          relId: this.delData.relId,
          communityId: this.$store.state.app.currentCommunity.communityId
        })
        this.$message.success('取消授权成功')
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
