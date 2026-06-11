<template>
  <div class="list-initialize-community-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="小区ID">
          <el-input v-model="queryParams.communityId" placeholder="请输入小区ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="小区名称">
          <el-input v-model="queryParams.name" placeholder="请输入小区名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>小区信息</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="communityId" label="小区ID" width="180" />
        <el-table-column prop="name" label="小区名称" min-width="150" />
        <el-table-column prop="nearbyLandmarks" label="附近地标" min-width="150" />
        <el-table-column prop="cityName" label="城市编码" width="120" />
        <el-table-column prop="stateName" label="状态" width="100" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleFormat(row)">格式化</el-button>
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
    <ChooseInitializeCommunity :visible.sync="formatVisible" :community="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listCommunitys } from '@/api/dev/devApi'
import ChooseInitializeCommunity from '@/components/dev/ChooseInitializeCommunity.vue'

export default {
  name: 'ListInitializeCommunityManage',
  components: { ChooseInitializeCommunity },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      formatVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        name: ''
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
        const res = await listCommunitys({ ...this.queryParams })
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
      this.queryParams.communityId = ''
      this.queryParams.name = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleFormat(row) {
      this.currentRow = { ...row }
      this.formatVisible = true
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
