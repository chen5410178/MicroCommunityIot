<template>
  <div class="tab-component">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item>
          <el-input v-model="queryParams.carNum" placeholder="必填，请填写车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择审核状态" clearable style="width: 160px">
            <el-option label="待审核" value="W" />
            <el-option label="允许通行" value="C" />
            <el-option label="拒绝通行" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.startTime" type="date" placeholder="开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.endTime" type="date" placeholder="结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" border stripe size="small">
        <el-table-column label="进场图" width="90" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.photoJpg" :src="row.photoJpg" fit="cover" style="width:60px;height:60px" :preview-src-list="[row.photoJpg]" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="carNum" label="车牌号" min-width="120" align="center" />
        <el-table-column prop="paNum" label="停车场" min-width="120" align="center" />
        <el-table-column prop="machineName" label="入口" min-width="140" align="center" />
        <el-table-column prop="inTime" label="进场时间" min-width="160" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state === 'W'" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.state === 'C'" type="success" size="small">允许通行</el-tag>
            <el-tag v-else-if="row.state === 'F'" type="danger" size="small">拒绝通行</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="审核说明" min-width="120" align="center">
          <template slot-scope="{ row }">{{ row.remark || row.msg || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="{ row }">
            <el-button v-if="row.state === 'W'" type="text" size="small" @click="doOpenTempCarAuth(row)">审核</el-button>
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
import { listCarInoutTempAuth } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlTempCarAuths',
  props: {
    boxId: { type: String, default: '' },
    paId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1, row: 10, carNum: '', state: '', startTime: '', endTime: ''
      }
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
        const res = await listCarInoutTempAuth({
          ...this.queryParams,
          paId: this.paId,
          communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        console.error('获取临时车审核列表失败', e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() { this.queryParams.page = 1; this.loadData() },
    handleReset() {
      this.queryParams = { page: 1, row: 10, carNum: '', state: '', startTime: '', endTime: '' }
      this.loadData()
    },
    handleSizeChange(val) { this.queryParams.row = val; this.loadData() },
    handlePageChange(val) { this.queryParams.page = val; this.loadData() },
    doOpenTempCarAuth(car) {
      this.$emit('show-temp-car-auth', car)
    }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.pagination-container { text-align: right; margin-top: 16px; }
</style>
