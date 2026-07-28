<template>
  <div class="list-admin-barrier-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.communityId" placeholder="请选择小区" clearable style="width: 180px" @change="handleSearch">
            <el-option
              v-for="item in communityList"
              :key="item.communityId"
              :label="item.name"
              :value="item.communityId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>道闸摄像头</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="communityName" label="小区名称" min-width="120" />
        <el-table-column prop="machineCode" label="设备编码" min-width="120" />
        <el-table-column prop="machineName" label="设备名称" min-width="120" />
        <el-table-column prop="machineIp" label="设备IP" min-width="130" />
        <el-table-column label="设备方向" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.direction == '3306' ? '进' : '出' }}
          </template>
        </el-table-column>
        <el-table-column prop="implBeanName" label="道闸厂家" min-width="120" />
        <el-table-column prop="boxName" label="岗亭" min-width="100" />
        <el-table-column prop="stateName" label="状态" width="80" align="center" />
        <el-table-column prop="monitorName" label="监控" min-width="120" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
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
import { listAdminBarriers } from '@/api/car/carApi'
import { getCommunityList } from '@/utils/auth'

export default {
  name: 'ListAdminBarrier',
  computed: {
    communityList() { return getCommunityList() || [] }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineCode: '',
        machineName: ''
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
        const res = await listAdminBarriers(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, communityId: '', machineCode: '', machineName: '' }
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
