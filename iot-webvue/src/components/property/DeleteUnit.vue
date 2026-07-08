<template>
  <el-dialog :visible.sync="dialogVisible" title="删除单元" width="450px" @close="handleClose">
    <p v-if="delData">确认删除单元 <strong>{{ delData.unitNum }}单元</strong> 吗？此操作不可恢复。</p>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="danger" @click="handleConfirm" :loading="loading">确认删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteUnitItem } from '@/api/property/unitApi'

export default {
  name: 'DeleteUnit',
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
        await deleteUnitItem({ unitId: this.delData.unitId, floorId: this.delData.floorId })
        this.$message.success('删除成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '删除失败') }
      finally { this.loading = false }
    }
  }
}
</script>
