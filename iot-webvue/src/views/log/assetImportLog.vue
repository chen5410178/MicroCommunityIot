<template>
  <div class="list-asset-import-log">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>批量导入日志</span>
        <el-button type="primary" size="small" @click="fetchList">刷新</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="logId" label="导入编号" min-width="120" />
        <el-table-column prop="logTypeName" label="导入类型" min-width="120" />
        <el-table-column prop="waitCount" label="待导入数" min-width="100" />
        <el-table-column prop="successCount" label="成功数量" min-width="100" />
        <el-table-column prop="errorCount" label="失败数量" min-width="100" />
        <el-table-column prop="state" label="状态" min-width="100">
          <template slot-scope="{ row }">
            {{ stateMap[row.state] || row.state }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="导入时间" min-width="160" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
import { listAssetImportLogs } from '@/api/log/logApi'

export default {
  name: 'AssetImportLog',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10
      },
      stateMap: {
        'W': '待导入',
        'D': '处理中',
        'C': '已完成'
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
        const communityId = this.$store.state.app.currentCommunity.communityId
        const res = await listAssetImportLogs({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleDetail(row) {
      this.$router.push({
        path: '/pages/log/assetImportLogDetail',
        query: { logId: row.logId, logType: row.logType }
      })
    }
  }
}
</script>

<style scoped>
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
