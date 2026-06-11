<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除确认"
    width="400px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要移除员工 <strong>{{ delData && delData.name }}</strong> 吗？
      </p>
      <p style="color: #909399; font-size: 14px; margin-top: 8px;">移除后该员工将不再属于此组织</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确定移除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteOrgRelStaff } from '@/api/frame/orgApi'

export default {
  name: 'DeleteOrgRelStaff',
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
        const community = this.$store.state.app.currentCommunity || {}
        await deleteOrgRelStaff({
          osrId: this.delData.osrId || this.delData.id,
          communityId: community.communityId
        })
        this.$message.success('移除成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '移除失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
