<template>
  <div class="tab-component">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item>
          <el-input v-model="queryParams.staffName" placeholder="请填写员工" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" border stripe size="small">
        <el-table-column label="抓拍图" width="80" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.photoJpg" :src="row.photoJpg" fit="cover" style="width:60px;height:60px" :preview-src-list="[row.photoJpg]" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开闸时间" min-width="160" align="center" />
        <el-table-column prop="staffName" label="员工" min-width="120" align="center" />
        <el-table-column prop="machineName" label="摄像头名称" min-width="160" align="center" />
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
import { listManualOpenDoorLogs } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlManualOpenDoorLog',
  props: { paId: { type: String, default: '' } },
  data() {
    return { loading: false, list: [], total: 0, queryParams: { page: 1, row: 10, staffName: '' } }
  },
  mounted() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listManualOpenDoorLogs({
          ...this.queryParams,
          paId: this.paId,
          communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) { console.error('获取开闸日志失败', e) } finally { this.loading = false }
    },
    handleSearch() { this.queryParams.page = 1; this.loadData() },
    handleSizeChange(val) { this.queryParams.row = val; this.loadData() },
    handlePageChange(val) { this.queryParams.page = val; this.loadData() }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.pagination-container { text-align: right; margin-top: 16px; }
</style>
