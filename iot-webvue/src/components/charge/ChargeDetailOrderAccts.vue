<template>
  <div class="charge-detail-order-accts">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="acctDetailId" label="明细ID" min-width="120" />
      <el-table-column label="时间段" min-width="220">
        <template slot-scope="{ row }">
          <div>{{ row.startTime }}</div>
          <div>~{{ row.endTime }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="durationPrice" label="小时电价" min-width="100" />
      <el-table-column prop="amount" label="扣款金额" min-width="100" />
      <el-table-column prop="createTime" label="扣款时间" min-width="160" />
      <el-table-column prop="remark" label="说明" min-width="200" />
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
import { listChargeMachineOrderAcct } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeDetailOrderAccts',
  props: {
    orderId: { type: String, default: '' }
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
    orderId: {
      immediate: true,
      handler() {
        if (this.orderId) {
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeMachineOrderAcct({
          orderId: this.orderId,
          communityId,
          page: this.queryParams.page,
          row: this.queryParams.row
        })
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
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
