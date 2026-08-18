<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除位置"
    width="420px"
    @close="handleClose"
  >
    <div class="delete-tip">
      <i class="el-icon-warning" style="color: #e6a23c; font-size: 20px; margin-right: 8px" />
      <span>确定删除位置「{{ (editData || {}).locationName || '' }}」吗？请先确认该位置下没有子位置或商铺，删除后不可恢复。</span>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确定删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteShopLocation } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'DeleteLocation',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: () => null }
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await deleteShopLocation({
          locationId: this.editData.locationId,
          communityId
        })
        this.$message.success('删除成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '删除失败') }
      finally { this.loading = false }
    }
  }
}
</script>

<style scoped>
.delete-tip {
  display: flex;
  align-items: flex-start;
  line-height: 22px;
}
</style>
