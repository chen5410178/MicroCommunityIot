<template>
  <div class="list-transaction-log-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.transactionId" placeholder="请输入流水号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.appId" placeholder="请输入应用编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.serviceCode" placeholder="请输入服务编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>交互日志</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="transactionId" label="流水号" min-width="180" />
        <el-table-column prop="srcIp" label="源IP" min-width="140" />
        <el-table-column prop="appId" label="应用编码" min-width="120" />
        <el-table-column prop="serviceCode" label="服务编码" min-width="180" />
        <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.state === 'F' ? 'danger' : 'success'" size="small">
              {{ row.state === 'F' ? '失败' : '成功' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="请求时间" min-width="160" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewMessage(row)">查看报文</el-button>
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
    <TransactionLogMessage ref="transactionLogMessageRef" />
  </div>
</template>

<script>
import { queryTransactionLogs } from '@/api/dev/devApi'
import TransactionLogMessage from '@/components/dev/TransactionLogMessage.vue'

export default {
  name: 'ListTransactionLogManage',
  components: { TransactionLogMessage },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        transactionId: '',
        appId: '',
        serviceCode: ''
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
        const res = await queryTransactionLogs(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, transactionId: '', appId: '', serviceCode: '' }
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
    handleViewMessage(row) {
      this.$refs.transactionLogMessageRef.open(row)
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
