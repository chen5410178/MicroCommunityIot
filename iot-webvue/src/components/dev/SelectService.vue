<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择服务"
    width="70%"
    append-to-body
  >
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="服务编码">
        <el-input v-model="searchForm.serviceCode" placeholder="请输入服务编码" clearable />
      </el-form-item>
      <el-form-item label="服务名称">
        <el-input v-model="searchForm.name" placeholder="请输入服务名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="serviceCode" label="服务编码" min-width="120" />
      <el-table-column prop="name" label="服务名称" min-width="150" />
      <el-table-column prop="serviceId" label="服务ID" width="120" />
      <el-table-column prop="url" label="调用地址" min-width="180" />
      <el-table-column label="操作" width="80" align="center">
        <template slot-scope="scope">
          <el-button type="text" @click="handleSelect(scope.row)">选择</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="page.current"
      :page-size="page.size"
      :total="page.total"
      :page-sizes="[10, 20, 30, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination-container"
    />
  </el-dialog>
</template>

<script>
import { listServices } from '@/api/dev/devApi'

export default {
  name: 'SelectService',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      tableData: [],
      page: {
        current: 1,
        size: 10,
        total: 0
      },
      searchForm: {
        serviceCode: '',
        name: ''
      }
    }
  },
  methods: {
    openDialog() {
      this.dialogVisible = true
      this.page.current = 1
      this.searchForm = { serviceCode: '', name: '' }
      this._loadApiList()
    },
    async _loadApiList() {
      this.loading = true
      try {
        const params = {
          page: this.page.current,
          row: this.page.size,
          ...this.searchForm
        }
        const res = await listServices(params)
        this.tableData = res.data || []
        this.page.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSelect(row) {
      this.$emit('select', row)
      this.dialogVisible = false
    },
    handleSizeChange(val) {
      this.page.size = val
      this._loadApiList()
    },
    handleCurrentChange(val) {
      this.page.current = val
      this._loadApiList()
    },
    handleSearch() {
      this.page.current = 1
      this._loadApiList()
    }
  }
}
</script>

<style scoped>
.search-form { text-align: right; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
