<template>
  <div class="shop-detail-car">
    <div v-loading="loading">
      <el-table :data="carList" border stripe>
        <el-table-column prop="carNum" label="车牌号" min-width="120" />
        <el-table-column prop="carTypeName" label="类型" min-width="100" />
        <el-table-column prop="stateName" label="状态" min-width="100" />
        <el-table-column prop="createTime" label="登记时间" min-width="160" />
      </el-table>
      <el-empty v-if="!loading && carList.length === 0" description="暂无数据" />
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
import { listOwnerCars } from '@/api/property/ownerCarApi'

export default {
  name: 'ShopDetailCar',
  data() {
    return {
      loading: false,
      carList: [],
      total: 0,
      ownerId: '',
      query: { page: 1, row: 10 }
    }
  },
  methods: {
    async open(ownerId) {
      this.ownerId = ownerId || ''
      this.carList = []
      this.total = 0
      this.query.page = 1
      if (!this.ownerId) return
      await this.loadCars()
    },
    async loadCars() {
      this.loading = true
      try {
        const res = await listOwnerCars({
          ownerId: this.ownerId,
          page: this.query.page,
          row: this.query.row
        })
        this.carList = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.carList = []
      } finally {
        this.loading = false
      }
    },
    handlePageChange(val) {
      this.query.page = val
      this.loadCars()
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
