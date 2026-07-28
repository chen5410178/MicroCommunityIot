<template>
  <div class="list-admin-charge-machine">
    <el-row :gutter="16">
      <el-col :span="4">
        <CommunitySelector @change="handleCommunityChange" />
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.machineName" placeholder="请输入名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.machineCode" placeholder="请输入设备编号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>充电桩</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="communityName" label="小区名称" min-width="120" />
            <el-table-column prop="machineName" label="名称" min-width="120" />
            <el-table-column prop="machineCode" label="设备编号" min-width="120" />
            <el-table-column prop="factoryName" label="厂家" min-width="100" />
            <el-table-column prop="portCount" label="插槽/桩个数" width="110" align="center" />
            <el-table-column prop="ruleName" label="充电规则" min-width="120" />
            <el-table-column prop="chargeTypeName" label="充电类型" min-width="100" />
            <el-table-column prop="stateName" label="状态" width="90" align="center" />
            <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
            <el-table-column prop="monitorName" label="监控" min-width="100" />
            <el-table-column label="操作" width="100" fixed="right" align="center">
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
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listAdminChargeMachine } from '@/api/charge/chargeApi'
import CommunitySelector from '@/components/common/CommunitySelector.vue'

export default {
  name: 'ListAdminChargeMachine',
  components: { CommunitySelector },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineName: '',
        machineCode: ''
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAdminChargeMachine(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleCommunityChange(community) {
      this.queryParams.communityId = community.communityId || ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.machineName = ''
      this.queryParams.machineCode = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleDetail(row) {
      this.$router.push('/pages/charge/adminChargeDetail?machineId=' + row.machineId)
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
