<template>
  <div class="parking-coupon-order-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
        <el-button type="text" size="small" @click="handleReset">重置</el-button>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.couponId" placeholder="请输入优惠券ID" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.shopName" placeholder="请输入商家店铺名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.queryStartTime" type="datetime" placeholder="选择开始时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.queryEndTime" type="datetime" placeholder="选择结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>停车券订单</span>
      </div>
      <div class="table-wrapper">
        <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
          <el-table-column prop="orderId" label="订单" min-width="120" />
          <el-table-column prop="couponName" label="优惠券" min-width="120" />
          <el-table-column prop="shopName" label="商家店铺" min-width="130" />
          <el-table-column prop="paName" label="停车场" min-width="120" />
          <el-table-column prop="price" label="单价" min-width="80" />
          <el-table-column prop="quantity" label="数量" min-width="80" />
          <el-table-column prop="payTypeName" label="支付方式" min-width="100" />
          <el-table-column prop="receivableAmount" label="应收" min-width="80" />
          <el-table-column prop="receivedAmount" label="实收" min-width="80" />
          <el-table-column prop="remark" label="备注" min-width="120" />
          <el-table-column prop="createTime" label="创建时间" min-width="160" />
        </el-table>
      </div>
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
import { listParkingCouponOrders } from '@/api/coupon/couponApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListParkingCouponOrder',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        couponId: '',
        shopName: '',
        queryStartTime: '',
        queryEndTime: ''
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingCouponOrders({ ...this.queryParams, communityId })
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
      this.queryParams = {
        page: 1,
        row: 10,
        couponId: '',
        shopName: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
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
.table-card { margin-top: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.table-wrapper { overflow-x: auto; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
