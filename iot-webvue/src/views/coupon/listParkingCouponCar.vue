<template>
  <div class="list-parking-coupon-car">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="车牌号">
          <el-input v-model="queryParams.carNumLike" placeholder="请输入车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="商家名称">
          <el-input v-model="queryParams.shopNameLike" placeholder="请输入商家店铺名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="未使用" value="1001" />
            <el-option label="已使用" value="2002" />
            <el-option label="作废" value="3003" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="queryParams.queryStartTime"
            type="datetime"
            placeholder="请选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
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
        <span>车辆停车券</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="carNum" label="车辆" width="120" align="center" />
        <el-table-column prop="couponName" label="优惠券" min-width="120" align="center" />
        <el-table-column prop="shopName" label="商家店铺" min-width="120" align="center" />
        <el-table-column prop="paName" label="停车场" min-width="120" align="center" />
        <el-table-column prop="giveWayName" label="赠送方式" width="100" align="center" />
        <el-table-column prop="typeCdName" label="优惠方式" width="100" align="center" />
        <el-table-column prop="value" label="面值" width="80" align="center" />
        <el-table-column prop="startTime" label="开始时间" width="160" align="center" />
        <el-table-column prop="endTime" label="结束时间" width="160" align="center" />
        <el-table-column prop="createTime" label="赠送时间" width="160" align="center" />
        <el-table-column prop="stateName" label="状态" width="80" align="center" />
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
import { listParkingCouponCars } from '@/api/coupon/couponApi'

export default {
  name: 'ListParkingCouponCar',
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
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  computed: {
    communityId() {
      return this.$store.state.app.currentCommunity.communityId || ''
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
        const res = await listParkingCouponCars({
          ...this.queryParams,
          communityId: this.communityId
        })
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
      this.queryParams.carNumLike = ''
      this.queryParams.shopNameLike = ''
      this.queryParams.state = ''
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
