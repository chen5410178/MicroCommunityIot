<template>
  <div class="admin-charge-detail-parking">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="couponName" label="优惠券名称" min-width="150" />
      <el-table-column prop="couponTypeName" label="优惠券类型" min-width="120" />
      <el-table-column prop="faceValue" label="优惠券面值" min-width="120" />
      <el-table-column prop="validityPeriod" label="有效时间" min-width="120" />
      <el-table-column prop="statusName" label="状态" width="90" align="center" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
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
import { listAdminParkingCouponCharge } from '@/api/charge/chargeApi'

export default {
  name: 'AdminChargeDetailParking',
  props: {
    machineId: { type: String, default: '' },
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
    machineId: {
      immediate: true,
      handler() {
        if (this.machineId) {
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
        const res = await listAdminParkingCouponCharge({
          machineId: this.machineId,
          orderId: this.orderId,
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
