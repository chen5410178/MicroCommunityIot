<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除确认"
    width="400px"
    append-to-body
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要删除门禁 <strong>{{ delData && delData.machineName }}</strong> 吗？
      </p>
      <p style="color: #909399; font-size: 14px; margin-top: 8px;">此操作不可恢复</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确定删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteAccessControl } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'DeleteAccessControl',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      delData: {}
    }
  },
  methods: {
    open(row) {
      this.delData = row
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        await deleteAccessControl({ machineId: this.delData.machineId, communityId })
        this.$message.success('删除成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '删除失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
