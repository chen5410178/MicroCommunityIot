<template>
  <div class="admin-charge-detail-acct">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="acctId" label="账户编号" min-width="120" />
      <el-table-column prop="acctName" label="账户" min-width="150" />
      <el-table-column prop="acctTypeName" label="账户类型" min-width="120" />
      <el-table-column prop="amount" label="账户余额" min-width="120" />
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
  </div>
</template>

<script>
import { queryAdminOwnerAccount } from '@/api/charge/chargeApi'

export default {
  name: 'AdminChargeDetailAcct',
  props: {
    acctId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: { page: 1, row: 10 }
    }
  },
  watch: {
    acctId: {
      immediate: true,
      handler() {
        if (this.acctId) {
          this.queryParams.page = 1
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await queryAdminOwnerAccount({
          acctId: this.acctId,
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.records || 0
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
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
