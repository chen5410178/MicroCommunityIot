<template>
  <el-dialog :visible.sync="dialogVisible" title="请确认您的操作" width="400px">
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #E6A23C" />
      <p style="margin-top: 20px; font-size: 16px;">确认是否{{ stateName }}!</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">点错了</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updatePropertyState } from '@/api/admin/propertyCompanyApi'

export default {
  name: 'UpdateStoreState',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      storeId: '',
      state: '',
      stateName: ''
    }
  },
  methods: {
    open(row, state) {
      this.storeId = row.storeId
      this.state = state
      this.stateName = state === '48002' ? '限制登录' : '恢复登录'
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.loading = true
      try {
        await updatePropertyState({ storeId: this.storeId, state: this.state, stateName: this.stateName })
        this.$message.success('操作成功')
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
