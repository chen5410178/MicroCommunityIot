<template>
  <div class="charge-machine-order-accts">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>扣款明细</span>
        <div>
          <el-button type="primary" size="small" @click="handleRefund">退款</el-button>
          <el-button size="small" @click="handleBack">返回</el-button>
        </div>
      </div>
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
    </el-card>
    <ReturnChargeMoney ref="returnChargeMoneyRef" @success="fetchList" />
  </div>
</template>

<script>
import { listChargeMachineOrderAcct } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'
import ReturnChargeMoney from '@/components/charge/ReturnChargeMoney.vue'

export default {
  name: 'ChargeMachineOrderAccts',
  components: { ReturnChargeMoney },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      orderId: '',
      queryParams: { page: 1, row: 10 }
    }
  },
  mounted() {
    this.orderId = this.$route.query.orderId || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      if (!this.orderId) return
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await listChargeMachineOrderAcct({
          orderId: this.orderId,
          communityId: community ? community.communityId : '',
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleRefund() {
      const community = getCurrentCommunity()
      this.$refs.returnChargeMoneyRef.open({
        orderId: this.orderId,
        communityId: community ? community.communityId : '',
        chargeMachineOrderAcctss: this.list
      })
    },
    handleBack() {
      this.$router.go(-1)
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
