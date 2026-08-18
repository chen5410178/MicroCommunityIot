<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除商铺"
    width="420px"
    @close="handleClose"
  >
    <div class="delete-tip">
      <i class="el-icon-warning" style="color: #e6a23c; font-size: 20px; margin-right: 8px" />
      <span>确定删除商铺「{{ (delData || {}).shopNum || '' }}」吗？其下的业主绑定记录将一并删除，且不可恢复。</span>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="danger" @click="handleSubmit" :loading="loading">确定删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteBuildingShop } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'DeleteShop',
  props: {
    visible: { type: Boolean, default: false },
    delData: { type: Object, default: () => null }
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
        await deleteBuildingShop({
          shopId: this.delData.shopId,
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
