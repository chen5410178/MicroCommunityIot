<template>
  <div class="list-parking-coupon-car-order">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.carNumLike" placeholder="请输入车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.shopNameLike" placeholder="请输入商家店铺名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryStartTime"
            type="datetime"
            placeholder="请选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryEndTime"
            type="datetime"
            placeholder="请选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>停车券核销</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="orderId" label="核销订单" min-width="220" align="center" />
        <el-table-column prop="carNum" label="核销车辆" width="120" align="center" />
        <el-table-column prop="couponName" label="优惠券" min-width="120" align="center" />
        <el-table-column prop="shopName" label="商家店铺" min-width="120" align="center" />
        <el-table-column prop="paName" label="停车场" min-width="120" align="center" />
        <el-table-column prop="machineName" label="道闸" min-width="120" align="center" />
        <el-table-column prop="createTime" label="核销时间" width="160" align="center" />
        <el-table-column prop="remark" label="说明" min-width="120" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
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
import { listParkingCouponCarOrders } from '@/api/coupon/couponApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListParkingCouponCarOrder',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        carNumLike: '',
        shopNameLike: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  computed: {
    communityId() {
      return (getCurrentCommunity() || {}).communityId || ''
    }
  },
  watch: {
    communityId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listParkingCouponCarOrders({
          ...this.queryParams,
          communityId: this.communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.carNumLike = ''
      this.queryParams.shopNameLike = ''
      this.queryParams.queryStartTime = ''
      this.queryParams.queryEndTime = ''
      this.queryParams.page = 1
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
