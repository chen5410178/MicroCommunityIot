<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除确认"
    width="400px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要删除规格 <strong>{{ delData && delData.specName }}</strong> 吗？
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
import { deleteChargeMachineFactorySpec } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'DeleteChargeMachineFactorySpec',
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await deleteChargeMachineFactorySpec({ specId: this.delData.specId, communityId })
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
