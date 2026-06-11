<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除确认"
    width="400px"
    append-to-body
    @close="handleClose"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要删除员工 <strong>{{ delData && delData.name }}</strong> 的关联吗？
      </p>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确定删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteRoleStaff } from '@/api/frame/roleApi'

export default {
  name: 'DeleteRoleStaff',
  props: {
    visible: { type: Boolean, default: false },
    delData: { type: Object, default: null },
    roleId: { type: String, default: '' }
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
    handleClose() {
      this.dialogVisible = false
    },
    async handleSubmit() {
      this.loading = true
      try {
        await deleteRoleStaff({
          roleId: this.roleId,
          staffId: this.delData.userId || this.delData.staffId || this.delData.id
        })
        this.$message.success('删除成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '删除失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
