<template>
  <div class="work-license-log">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="device-card">
          <div class="device-list">
            <div
              v-for="item in deviceList"
              :key="item.machineId"
              class="device-item"
              :class="{ active: currentMachineId === item.machineId }"
              @click="switchDevice(item)"
            >
              {{ item.machineName }}
            </div>
          </div>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.logAction" placeholder="请输入日志动作" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.state" placeholder="请输入状态" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryStartTime" type="date" placeholder="请选择开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryEndTime" type="date" placeholder="请选择结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>工牌日志</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="logId" label="编号" width="80" />
            <el-table-column prop="machineName" label="设备名称" width="120" />
            <el-table-column prop="machineCode" label="设备编码" width="120" />
            <el-table-column label="日志动作" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.logAction === 'tts' ? '语音' : '参数' }}
              </template>
            </el-table-column>
            <el-table-column prop="staffId" label="用户ID" width="120" />
            <el-table-column prop="staffName" label="用户名称" width="100" />
            <el-table-column label="状态" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.state === 'W' ? '请求成功' : '返回成功' }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下发时间" width="160" />
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
      </div>
    </div>
    <ShowWorkLicenseLog ref="showLog" />
  </div>
</template>

<script>
import { listWorkLicenseLogs, listWorkLicenseMachines } from '@/api/charge/chargeApi'
import ShowWorkLicenseLog from '@/components/charge/ShowWorkLicenseLog.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'WorkLicenseLog',
  components: { ShowWorkLicenseLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      deviceList: [{ machineId: '', machineName: '全部' }],
      currentMachineId: '',
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
    this.fetchDevices()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        const res = await listWorkLicenseLogs(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchDevices() {
      try {
        const res = await listWorkLicenseMachines({ page: 1, row: 100 })
        this.deviceList = [{ machineId: '', machineName: '全部' }, ...(res.data || [])]
      } catch (e) {
        // ignore
      }
    },
    switchDevice(item) {
      this.currentMachineId = item.machineId
      this.queryParams.machineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        machineId: this.currentMachineId,
        userName: '',
        logAction: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleShowLog(row) {
      this.$refs.showLog.open(row)
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
.page-container { display: flex; gap: 16px; }
.left-panel { width: 160px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.device-card { height: 100%; }
.device-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.device-list { max-height: calc(100vh - 200px); overflow-y: auto; }
.device-item { padding: 10px 12px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 13px; transition: all 0.3s; }
.device-item:hover { background-color: #f5f7fa; }
.device-item.active { background-color: #409eff; color: white; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
