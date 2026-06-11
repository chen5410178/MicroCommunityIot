<template>
  <el-dialog :visible.sync="dialogVisible" title="删除楼栋" width="450px" @close="handleClose">
    <p>确认删除楼栋 <strong>{{ delData.floorNum || delData.floorName }}栋</strong> 吗？此操作不可恢复。</p>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="danger" @click="handleConfirm" :loading="loading">确认删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteFloorItem } from '@/api/property/propertyApi'

export default {
  name: 'DeleteFloor',
  props: { visible: { type: Boolean, default: false }, delData: { type: Object, default: () => ({}) } },
  data() {
    return { dialogVisible: false, loading: false }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() { this.dialogVisible = false },
    async handleConfirm() {
      this.loading = true
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        await deleteFloorItem({ floorId: this.delData.floorId, communityId: communityInfo.communityId })
        this.$message.success('删除成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '删除失败') }
      finally { this.loading = false }
    }
  }
}
</script>
