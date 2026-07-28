<template>
  <div class="charge-detail-parking">
    <div style="margin-bottom: 12px;">
      <el-button v-if="list.length === 0" type="primary" size="small" @click="handleAdd">添加停车券</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="shopName" label="商铺" min-width="150" />
      <el-table-column prop="couponName" label="优惠券" min-width="150" />
      <el-table-column prop="paName" label="停车场" min-width="150" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="80" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
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
    <AddParkingCouponCharge :visible.sync="addVisible" :machine-id="machineId" @success="handleSuccess" />
    <DeleteParkingCouponCharge :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listParkingCouponCharge } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddParkingCouponCharge from '@/components/coupon/AddParkingCouponCharge.vue'
import DeleteParkingCouponCharge from '@/components/coupon/DeleteParkingCouponCharge.vue'

export default {
  name: 'ChargeDetailParking',
  components: { AddParkingCouponCharge, DeleteParkingCouponCharge },
  props: {
    machineId: { type: String, default: '' },
    orderId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      delVisible: false,
      currentRow: null,
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingCouponCharge({
          machineId: this.machineId,
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
    handleAdd() { this.addVisible = true },
    handleDelete(row) {
      this.currentRow = row
      this.delVisible = true
    },
    handleSuccess() { this.fetchList() },
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
