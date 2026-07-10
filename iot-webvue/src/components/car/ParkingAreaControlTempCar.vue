<template>
  <div class="tab-component">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item>
          <el-input v-model="queryParams.carNum" placeholder="必填，请填写车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" border stripe size="small">
        <el-table-column prop="tiId" label="编号" min-width="100" align="center" />
        <el-table-column prop="carNum" label="车牌号" min-width="120" align="center" />
        <el-table-column prop="areaNum" label="停车场" min-width="120" align="center" />
        <el-table-column prop="startTime" label="开始时间" min-width="160" align="center" />
        <el-table-column prop="endTime" label="结束时间" min-width="160" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center" />
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
import { listTempInnerCars } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlTempCar',
  props: {
    paId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: { page: 1, row: 10, carNum: '' }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listTempInnerCars({
          ...this.queryParams,
          paId: this.paId,
          communityId
        })
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) {
        console.error('获取临时车失败', e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() { this.queryParams.page = 1; this.loadData() },
    handleReset() { this.queryParams = { page: 1, row: 10, carNum: '' }; this.loadData() },
    handleSizeChange(val) { this.queryParams.row = val; this.loadData() },
    handlePageChange(val) { this.queryParams.page = val; this.loadData() }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.pagination-container { text-align: right; margin-top: 16px; }
</style>
