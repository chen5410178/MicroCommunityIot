<template>
  <div class="list-attendance-machine-log">
    <div class="content-wrapper">
      <div class="sidebar">
        <ul class="device-list">
          <li
            :class="{ active: queryParams.machineId === '' }"
            @click="switchDevice('')"
          >
            全部
          </li>
          <li
            v-for="item in machines"
            :key="item.machineId"
            :class="{ active: queryParams.machineId === item.machineId }"
            @click="switchDevice(item.machineId)"
          >
            {{ item.machineName }}
          </li>
        </ul>
      </div>
      <div class="main-content">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.logAction" placeholder="请输入日志动作" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.state" placeholder="请输入状态" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryStartTime"
                type="datetime"
                placeholder="选择开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryEndTime"
                type="datetime"
                placeholder="选择结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>下发日志</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="logId" label="编号" min-width="120" />
            <el-table-column prop="machineName" label="设备名称" min-width="120" />
            <el-table-column prop="machineCode" label="设备编码" min-width="120" />
            <el-table-column prop="logAction" label="日志动作" min-width="120" />
            <el-table-column prop="staffId" label="用户ID" width="100" />
            <el-table-column prop="staffName" label="用户名称" min-width="100" />
            <el-table-column prop="stateName" label="状态" width="80" align="center" />
            <el-table-column prop="createTime" label="下发时间" min-width="150" />
            <el-table-column label="操作" width="80" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleShowMsg(row)">报文</el-button>
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
    <ShowAttendanceMachineLog ref="showLogRef" />
  </div>
</template>

<script>
import { listAttendanceMachineLog, listAttendanceMachine } from '@/api/attendance/attendanceApi'
import ShowAttendanceMachineLog from '@/components/attendance/ShowAttendanceMachineLog.vue'

export default {
  name: 'AttendanceMachineLog',
  components: { ShowAttendanceMachineLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machines: [],
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineId: '',
        userName: '',
        logAction: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  computed: {
    currentCommunityId() {
      return this.$store.state.app.currentCommunity.communityId || ''
    }
  },
  watch: {
    currentCommunityId: {
      immediate: true,
      handler(val) {
        this.queryParams.communityId = val
        if (val) {
          this.loadMachines()
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAttendanceMachineLog(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async loadMachines() {
      try {
        const res = await listAttendanceMachine({ page: 1, row: 100, communityId: this.queryParams.communityId })
        this.machines = res.data || []
      } catch (e) {
        this.machines = []
      }
    },
    switchDevice(machineId) {
      this.queryParams.machineId = machineId
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
        communityId: this.queryParams.communityId,
        machineId: '',
        userName: '',
        logAction: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleShowMsg(row) {
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
.list-attendance-machine-log { height: 100%; }
.content-wrapper { display: flex; height: 100%; }
.sidebar { width: 200px; min-width: 200px; background: #fff; border-right: 1px solid #ebeef5; overflow-y: auto; }
.device-list { list-style: none; padding: 0; margin: 0; }
.device-list li { padding: 12px 16px; cursor: pointer; border-bottom: 1px solid #ebeef5; font-size: 14px; color: #303133; transition: all 0.2s; }
.device-list li:hover { background: #f5f7fa; }
.device-list li.active { background: #ecf5ff; color: #409eff; border-left: 3px solid #409eff; }
.main-content { flex: 1; overflow-y: auto; padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
