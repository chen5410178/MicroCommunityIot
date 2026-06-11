<template>
  <div class="list-community-shop">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.shopId" placeholder="请输入商家编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.shopName" placeholder="请输入商家名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.returnLink" placeholder="请输入联系电话" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>小区商家</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="shopName" label="商家店铺" min-width="150" />
        <el-table-column prop="shopId" label="商家编号" width="180" />
        <el-table-column prop="returnLink" label="联系电话" width="140" />
        <el-table-column label="停车劵" width="120" align="center">
          <template slot-scope="{ row }">
            {{ row.parkingCouponCount || 0 }}
            <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleBuy(row)">购买停车劵</el-button>
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
    </el-card>

    <AddCommunityShop :visible.sync="addVisible" @success="handleSuccess" />
    <BuyParkingCoupon :visible.sync="buyVisible" :shop-data="currentRow" @success="handleSuccess" />
    <DeleteCommunityShop :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewShopCoupons :visible.sync="viewVisible" :shop-data="currentRow" />
  </div>
</template>

<script>
import { listCommunityShops } from '@/api/coupon/couponApi'
import AddCommunityShop from '@/components/coupon/AddCommunityShop.vue'
import BuyParkingCoupon from '@/components/coupon/BuyParkingCoupon.vue'
import DeleteCommunityShop from '@/components/coupon/DeleteCommunityShop.vue'
import ViewShopCoupons from '@/components/coupon/ViewShopCoupons.vue'

export default {
  name: 'ListCommunityShop',
  components: { AddCommunityShop, BuyParkingCoupon, DeleteCommunityShop, ViewShopCoupons },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        shopId: '',
        shopName: '',
        returnLink: ''
      },
      addVisible: false,
      buyVisible: false,
      delVisible: false,
      viewVisible: false,
      currentRow: null
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId
        const res = await listCommunityShops({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, shopId: '', shopName: '', returnLink: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleBuy(row) {
      this.currentRow = { ...row }
      this.buyVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleView(row) {
      this.currentRow = { ...row }
      this.viewVisible = true
    },
    handleSuccess() {
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
