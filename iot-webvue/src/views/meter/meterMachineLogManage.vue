<template>
  <div class="tab-component">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.personName" placeholder="请输入用户名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>开合闸记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="logId" label="日志ID" width="100" align="center" />
        <el-table-column prop="machineId" label="设备ID" min-width="120" />
        <el-table-column label="动作" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.logAction == '1001' ? '开闸' : '关闸' }}
          </template>
        </el-table-column>
        <el-table-column prop="personName" label="用户名称" min-width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.state == '10001' ? '请求成功' : '返回异常' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleShowLog(row)">报文</el-button>
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

    <ShowMeterMachineLog :visible.sync="showLogVisible" ref="showLogRef" />
  </div>
</template>

<script>
import { listMeterMachineLog } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'
import ShowMeterMachineLog from '@/components/meter/ShowMeterMachineLog.vue'

export default {
  name: 'MeterMachineLogManage',
  components: { ShowMeterMachineLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showLogVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        personName: ''
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
        const community = getCurrentCommunity()
        const res = await listMeterMachineLog({
          ...this.queryParams,
          communityId: community.communityId
        })
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
      this.queryParams.personName = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleShowLog(row) {
      this.$refs.showLogRef.open(row)
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
.table-card { margin-top: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
