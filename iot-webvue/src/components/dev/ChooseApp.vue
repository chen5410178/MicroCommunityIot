<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择应用"
    width="700px"
    @close="handleClose"
  >
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="应用名称">
        <el-input v-model="searchForm.name" placeholder="请输入应用名称" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="appList" v-loading="loading" border stripe max-height="400">
      <el-table-column prop="appId" label="应用ID" width="120" />
      <el-table-column prop="name" label="应用名称" min-width="200" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="handleChoose(row)">选择</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="searchForm.page"
      :page-size="searchForm.row"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, prev, pager, next"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      class="pagination-container"
    />
  </el-dialog>
</template>

<script>
import { listApps } from '@/api/dev/devApi'

export default {
  name: 'ChooseApp',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      appList: [],
      total: 0,
      searchForm: {
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
        this.searchForm = { page: 1, row: 10, name: '' }
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listApps(this.searchForm)
        this.appList = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.searchForm.page = 1
      this.fetchList()
    },
    handleChoose(row) {
      this.$emit('chosen', { appId: row.appId, appName: row.name })
      this.dialogVisible = false
    },
    handleClose() {
      this.dialogVisible = false
    },
    handleSizeChange(val) {
      this.searchForm.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.searchForm.page = val
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
