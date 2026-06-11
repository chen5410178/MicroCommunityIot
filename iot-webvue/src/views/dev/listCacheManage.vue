<template>
  <div class="list-cache-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="缓存编码">
          <el-input v-model="queryParams.cacheCode" placeholder="请输入缓存编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="缓存名称">
          <el-input v-model="queryParams.name" placeholder="请输入缓存名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>缓存信息</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="缓存ID" width="100" />
        <el-table-column prop="cacheCode" label="缓存编码" min-width="150" />
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleRefresh(row)">刷新缓存</el-button>
          </template>
        </el-table-column>
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
    </el-card>
  </div>
</template>

<script>
import { listCache, refreshCache } from '@/api/dev/devApi'

export default {
  name: 'ListCacheManage',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        cacheCode: '',
        name: ''
      }
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listCache(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, cacheCode: '', name: '' }
      this.fetchList()
    },
    handleRefresh(row) {
      this.$confirm('确定要刷新该缓存吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          await refreshCache({ cache: row.url })
          this.$message.success('刷新缓存成功')
        } catch (e) {
          this.$message.error((e && e.message) || '刷新缓存失败')
        }
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
