<template>
  <div class="list-work-license-log">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="tree-card">
          <div slot="header" class="card-header">
            <span>工牌列表</span>
          </div>
          <ul class="machine-tree">
            <li
              :class="{ 'tree-item': true, 'is-active': queryParams.machineId === '' }"
              @click="switchMachine({ machineId: '', machineName: '全部' })"
            >
              全部
            </li>
            <li
              v-for="item in machineList"
              :key="item.machineId"
              :class="{ 'tree-item': true, 'is-active': queryParams.machineId === item.machineId }"
              @click="switchMachine(item)"
            >
              {{ item.machineName }}
            </li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.logAction" placeholder="请选择日志动作" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryStartTime"
                type="datetime"
                placeholder="请输入开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 190px"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryEndTime"
                type="datetime"
                placeholder="请输入结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 190px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>工牌日志</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="logId" label="编号" width="80" />
            <el-table-column prop="machineName" label="设备名称" min-width="120" />
            <el-table-column prop="machineCode" label="设备编码" min-width="120" />
            <el-table-column label="日志动作" width="100">
              <template slot-scope="{ row }">
                {{ row.logAction === 'tts' ? '语音' : '参数' }}
              </template>
            </el-table-column>
            <el-table-column prop="staffId" label="用户ID" width="100" />
            <el-table-column prop="staffName" label="用户名称" min-width="100" />
            <el-table-column label="状态" width="100">
              <template slot-scope="{ row }">
                {{ row.stateName === 'W' ? '请求成功' : '返回成功' }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下发时间" width="170" />
            <el-table-column label="操作" width="80" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleViewLog(row)">报文</el-button>
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
    <ShowWorkLicenseLog :visible.sync="logVisible" :log-data="currentLog" />
  </div>
</template>

<script>
import { listWorkLicenseLogs, listWorkLicenseMachines } from '@/api/charge/chargeApi'
import ShowWorkLicenseLog from '@/components/charge/ShowWorkLicenseLog.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListWorkLicenseLog',
  components: { ShowWorkLicenseLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machineList: [],
      logVisible: false,
      currentLog: null,
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        userName: '',
        logAction: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.loadMachineList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const params = { ...this.queryParams, communityId }
        const res = await listWorkLicenseLogs(params)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    async loadMachineList() {
      try {
        const res = await listWorkLicenseMachines({ page: 1, row: 100 })
        this.machineList = res.data || []
      } catch { /* ignore */ }
    },
    switchMachine(item) {
      this.queryParams.machineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleViewLog(row) {
      this.currentLog = { ...row }
      this.logVisible = true
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
.tree-card { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.machine-tree {
  list-style: none;
  padding: 0;
  margin: 0;
}
.tree-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 2px;
  font-size: 14px;
  color: #606266;
}
.tree-item:hover { background: #f5f7fa; }
.tree-item.is-active { background: #ecf5ff; color: #409eff; font-weight: 500; }
</style>
