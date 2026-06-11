<template>
  <div class="a-access-control-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.locationName" placeholder="请输入位置名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>门禁设备</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="communityName" label="小区名称" min-width="120" />
        <el-table-column prop="machineCode" label="门禁编码" min-width="120" />
        <el-table-column prop="machineName" label="门禁名称" min-width="120" />
        <el-table-column prop="machineIp" label="IP" min-width="130" />
        <el-table-column prop="machineMac" label="Mac" min-width="140" />
        <el-table-column label="开门提示" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.promptSwitch == 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="方向" width="70" align="center">
          <template slot-scope="{ row }">
            {{ row.direction == '3306' ? '进' : '出' }}
          </template>
        </el-table-column>
        <el-table-column prop="implBean" label="厂家" min-width="120" />
        <el-table-column prop="locationName" label="位置" min-width="120" />
        <el-table-column prop="stateName" label="在线" width="80" align="center" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="150" />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
import { listAdminAccessControl } from '@/api/accessControl/accessControlApi'

export default {
  name: 'AAccessControl',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        locationName: ''
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
        const res = await listAdminAccessControl(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', locationName: '' }
      this.fetchList()
    },
    handleDetail(row) {
      this.$router.push(`/pages/accessControl/aAccessControlDetail?machineId=${row.machineId}`)
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
