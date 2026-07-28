<template>
  <div class="evasion-list-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.carNum" placeholder="请输入车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="逃费开始日期"
            end-placeholder="逃费结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待处理" value="W" />
            <el-option label="已处理" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>逃费记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="carNum" label="车牌号" min-width="120" />
        <el-table-column prop="paNum" label="停车场" min-width="120" />
        <el-table-column prop="boxId" label="岗亭编号" min-width="120" />
        <el-table-column prop="inTime" label="进场时间" min-width="160" align="center" />
        <el-table-column prop="evasionTime" label="逃费时间" min-width="160" align="center" />
        <el-table-column label="应收金额" min-width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.amount ? '¥' + row.amount : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.state === 'W' ? 'warning' : 'success'" size="small">
              {{ row.state === 'W' ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="抓拍图片" width="100" align="center">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.photoJpg"
              style="width: 60px; height: 40px"
              :src="row.photoJpg"
              :preview-src-list="[row.photoJpg]"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="160" show-overflow-tooltip>
          <template slot-scope="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template slot-scope="{ row }">
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
  </div>
</template>

<script>
import { listCarInoutEvasion, deleteCarInoutEvasion } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListCarInoutEvasion',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      dateRange: [],
      queryParams: {
        page: 1,
        row: 10,
        carNum: '',
        evasionTimeStart: '',
        evasionTimeEnd: '',
        state: ''
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
        const params = { ...this.queryParams, communityId }
        if (this.dateRange && this.dateRange.length === 2) {
          params.evasionTimeStart = this.dateRange[0]
          params.evasionTimeEnd = this.dateRange[1]
        }
        const res = await listCarInoutEvasion(params)
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
      this.queryParams = { page: 1, row: 10, carNum: '', evasionTimeStart: '', evasionTimeEnd: '', state: '' }
      this.dateRange = []
      this.fetchList()
    },
    handleDelete(row) {
      this.$confirm('确定删除该条逃费记录吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          await deleteCarInoutEvasion({ evasionId: row.evasionId, communityId: row.communityId })
          this.$message.success('删除成功')
          this.fetchList()
        } catch (e) {
          this.$message.error(e.message || '删除失败')
        }
      }).catch(() => {})
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
