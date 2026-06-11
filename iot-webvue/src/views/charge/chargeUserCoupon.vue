<template>
  <div class="charge-user-coupon">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="优惠券编号">
          <el-input v-model="queryParams.couponId" placeholder="请填写优惠券编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="优惠券名称">
          <el-input v-model="queryParams.couponName" placeholder="请填写优惠券名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-input v-model="queryParams.validityDay" placeholder="请填写有效期" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="用户名称">
          <el-input v-model="queryParams.userName" placeholder="请填写用户名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.tel" placeholder="请填写手机号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="未使用" value="1001" />
            <el-option label="已使用" value="2002" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>业主充电券</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="couponId" label="编号" min-width="100" align="center" />
        <el-table-column prop="couponName" label="优惠券名称" min-width="120" align="center" />
        <el-table-column prop="value" label="面值" min-width="100" align="center" />
        <el-table-column prop="validityDay" label="有效期" min-width="100" align="center" />
        <el-table-column prop="userName" label="用户名称" min-width="120" align="center" />
        <el-table-column prop="tel" label="手机号" min-width="120" align="center" />
        <el-table-column prop="toTypeName" label="用途" min-width="100" align="center" />
        <el-table-column label="数量" min-width="80" align="center">
          <template slot-scope="{ row }">{{ row.stock }}张</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90" align="center">
          <template slot-scope="{ row }">{{ row.state === '1001' ? '未使用' : '已使用' }}</template>
        </el-table-column>
        <el-table-column prop="startTime" label="生效时间" min-width="160" align="center" />
        <el-table-column prop="createTime" label="赠送时间" min-width="160" align="center" />
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
import { listCouponPropertyUser } from '@/api/charge/chargeApi'

export default {
  name: 'ChargeUserCoupon',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        couponId: '',
        couponName: '',
        validityDay: '',
        userName: '',
        tel: '',
        state: '',
        toType: '5005'
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
        const res = await listCouponPropertyUser({
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
      this.queryParams.couponId = ''
      this.queryParams.couponName = ''
      this.queryParams.validityDay = ''
      this.queryParams.userName = ''
      this.queryParams.tel = ''
      this.queryParams.state = ''
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
