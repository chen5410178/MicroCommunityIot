<template>
  <div class="shop-detail-shop">
    <div style="margin-bottom: 12px;">
      <el-input
        v-model="query.shopNum"
        placeholder="商铺编号"
        clearable
        style="width: 200px"
        @keyup.enter.native="handleSearch"
      />
      <el-button type="primary" size="small" style="margin-left: 8px" @click="handleSearch">搜索</el-button>
      <el-button size="small" @click="handleReset">重置</el-button>
    </div>
    <div v-loading="loading">
      <el-table :data="shopList" border stripe>
        <el-table-column label="商铺编号" min-width="120">
          <template slot-scope="{ row }">
            <el-link type="primary" :underline="false" @click="goToShopDetail(row)">{{ row.shopNum }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="locationName" label="所在位置" min-width="140" />
        <el-table-column prop="ownerName" label="业主" min-width="100">
          <template slot-scope="{ row }">{{ row.ownerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="link" label="联系电话" min-width="120">
          <template slot-scope="{ row }">{{ row.link || '-' }}</template>
        </el-table-column>
        <el-table-column label="建筑面积" min-width="100">
          <template slot-scope="{ row }">{{ row.builtUpArea || '-' }}㎡</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="stateMap[row.state] && stateMap[row.state].type" size="small">
              {{ (stateMap[row.state] && stateMap[row.state].name) || row.state }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && shopList.length === 0" description="暂无数据" />
      <el-pagination
        v-if="total > 0"
        :current-page="query.page"
        :page-size="query.row"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        class="pagination-container"
      />
    </div>
  </div>
</template>

<script>
import { listBuildingShops } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ShopDetailShop',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' },
    currentShopId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      shopList: [],
      total: 0,
      stateMap: {
        '2006': { name: '已出租', type: 'warning' },
        '2007': { name: '已出售', type: 'info' },
        '2008': { name: '空置', type: 'success' }
      },
      query: { page: 1, row: 10, shopNum: '' }
    }
  },
  mounted() {
    this.loadShops()
  },
  watch: {
    ownerId: {
      handler() {
        this.shopList = []
        this.total = 0
        this.query.page = 1
        this.query.shopNum = ''
        this.loadShops()
      }
    }
  },
  methods: {
    async loadShops() {
      if (!this.ownerId) return
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listBuildingShops({
          ownerId: this.ownerId,
          shopNum: this.query.shopNum,
          communityId,
          page: this.query.page,
          row: this.query.row
        })
        let list = res.data || []
        if (this.currentShopId) {
          list = list.filter(item => item.shopId !== this.currentShopId)
        }
        this.shopList = list
        this.total = res.total || 0
      } catch (e) {
        this.shopList = []
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.query.page = 1
      this.loadShops()
    },
    handleReset() {
      this.query.shopNum = ''
      this.query.page = 1
      this.loadShops()
    },
    handlePageChange(val) {
      this.query.page = val
      this.loadShops()
    },
    goToShopDetail(row) {
      this.$router.push({
        path: '/pages/shop/shopDetail',
        query: { shopId: row.shopId, shopNum: row.shopNum }
      })
    }
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
