<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="关联小区"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item>
        <el-input v-model="queryParams.name" placeholder="请输入小区名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="tableRef"
      :data="list"
      v-loading="loading"
      border
      stripe
      @selection-change="handleSelectionChange"
      max-height="400"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="communityId" label="小区ID" min-width="120" />
      <el-table-column prop="communityName" label="小区名称" min-width="150" />
    </el-table>
    <el-pagination
      :current-page="queryParams.page"
      :page-size="queryParams.row"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      class="pagination-container"
    />
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading" :disabled="selectedRows.length === 0">确定关联</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listOrgNoCommunitys, saveRoleCommunity } from '@/api/frame/roleApi'

export default {
  name: 'AddRoleCommunity',
  props: {
    visible: { type: Boolean, default: false },
    roleId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      submitLoading: false,
      list: [],
      total: 0,
      selectedRows: [],
      queryParams: {
        page: 1,
        row: 10,
        name: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.queryParams = { page: 1, row: 10, name: '' }
        this.selectedRows = []
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listOrgNoCommunitys({
          ...this.queryParams,
          roleId: this.roleId
        })
        this.list = res.data || []
        this.total = res.total || 0
        this.$nextTick(() => {
          if (this.$refs.tableRef) this.$refs.tableRef.clearSelection()
        })
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.tableRef && this.$refs.tableRef.clearSelection()
    },
    async handleSubmit() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择小区')
        return
      }
      this.submitLoading = true
      try {
        const communitys = this.selectedRows.map(item => ({
          communityId: item.communityId,
          communityName: item.communityName
        }))
        await saveRoleCommunity({ roleId: this.roleId, communitys })
        this.$message.success('关联成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '关联失败')
      } finally {
        this.submitLoading = false
      }
    }
  }
}
</script>

<style scoped>
.search-form { padding-bottom: 8px; }
.pagination-container { margin-top: 12px; text-align: right; }
</style>
