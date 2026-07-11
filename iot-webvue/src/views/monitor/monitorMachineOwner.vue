<template>
  <div class="monitor-machine-owner">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="left-card">
          <ul class="machine-list">
            <li
              v-for="item in monitors"
              :key="item.machineId"
              :class="{ active: activeMachineId === item.machineId }"
              @click="handleMachineClick(item)"
            >
              <span>{{ item.machineName }}</span>
            </li>
            <li v-if="monitors.length === 0" class="empty-tip">暂无设备数据</li>
          </ul>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请输入人员名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>业主监控</span>
            <el-button type="primary" size="small" @click="handleAdd">授权</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="personName" label="人员名称" min-width="150" />
            <el-table-column prop="personId" label="人员ID" min-width="150" />
            <el-table-column prop="machineName" label="设备名称" min-width="150" />
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
    </div>
    <DeleteMonitorMachinePerson :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMonitorMachines, listMonitorMachinePersons } from '@/api/monitor/monitorApi'
import DeleteMonitorMachinePerson from '@/components/monitor/DeleteMonitorMachinePerson.vue'

export default {
  name: 'MonitorMachineOwner',
  components: { DeleteMonitorMachinePerson },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      monitors: [],
      activeMachineId: '',
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        personName: '',
        personType: '1001'
      }
    }
  },
  mounted() {
    this.fetchMonitors()
    this.fetchList()
  },
  methods: {
    async fetchMonitors() {
      try {
        const res = await listMonitorMachines({ page: 1, row: 100 })
        const data = res.data || []
        this.monitors = [{ machineName: '全部', machineId: '' }, ...data]
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listMonitorMachinePersons(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleMachineClick(item) {
      if (this.activeMachineId === item.machineId) {
        this.activeMachineId = ''
        this.queryParams.machineId = ''
      } else {
        this.activeMachineId = item.machineId
        this.queryParams.machineId = item.machineId
      }
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/monitor/addMonitorMachineOwner?machineId=' + this.activeMachineId)
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
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
.page-container { display: flex; gap: 16px; }
.left-panel { width: 200px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.left-card { height: 100%; }
.machine-list { list-style: none; padding: 0; margin: 0; max-height: calc(100vh - 200px); overflow-y: auto; }
.machine-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  font-size: 13px;
  color: #303133;
  transition: all 0.3s;
}
.machine-list li:hover { background-color: #f5f7fa; }
.machine-list li.active { background-color: #409eff; color: white; font-weight: 600; }
.machine-list li.empty-tip { cursor: default; color: #c0c4cc; text-align: center; padding: 20px; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
