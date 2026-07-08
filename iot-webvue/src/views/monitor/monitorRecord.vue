<template>
  <div class="monitor-record">
    <el-row :gutter="16" class="content-row">
      <el-col :span="4">
        <el-card shadow="never" class="left-card">
          <div slot="header" class="card-header">
            <span>监控设备</span>
          </div>
          <ul class="device-list">
            <li
              v-for="item in monitorList"
              :key="item.machineId"
              :class="{ active: currentMonitor && currentMonitor.machineId === item.machineId }"
              @click="handleDeviceClick(item)"
            >
              <span>{{ item.machineName }}</span>
            </li>
            <li v-if="monitorList.length === 0" class="empty-tip">暂无设备数据</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请输入人名搜索" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>监控录像</span>
          </div>
          <el-table :data="videoList" v-loading="loading" border stripe>
            <el-table-column prop="fileName" label="文件名" min-width="150" show-overflow-tooltip />
            <el-table-column prop="machineName" label="相机名称" min-width="120" />
            <el-table-column prop="machineCode" label="相机编号" min-width="120" />
            <el-table-column prop="dateHours" label="时间段" min-width="200" show-overflow-tooltip />
            <el-table-column prop="expireTime" label="过期时间" width="170" />
            <el-table-column prop="filePath" label="文件地址" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" label="创建时间" width="170" />
            <el-table-column label="操作" width="80" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handlePreview(row)">预览</el-button>
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
    <PlayRecordMonitorVideo :visible.sync="playVisible" :video-info="playVideoInfo" />
  </div>
</template>

<script>
import { listMonitorMachines, listMonitorRecords } from '@/api/monitor/monitorApi'
import PlayRecordMonitorVideo from '@/components/monitor/PlayRecordMonitorVideo.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MonitorRecord',
  components: { PlayRecordMonitorVideo },
  data() {
    return {
      loading: false,
      monitorList: [],
      videoList: [],
      total: 0,
      currentMonitor: null,
      playVisible: false,
      playVideoInfo: {},
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        personName: '',
        personType: '2002',
        communityId: ''
      }
    }
  },
  mounted() {
    this.fetchMonitorMachines()
  },
  methods: {
    async fetchMonitorMachines() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorMachines({ page: 1, row: 100, communityId })
        this.monitorList = res.data || []
        if (this.monitorList.length > 0) {
          this.handleDeviceClick(this.monitorList[0])
        }
      } catch { /* ignore */ }
    },
    async fetchRecords() {
      if (!this.currentMonitor) return
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const params = { ...this.queryParams, machineId: this.currentMonitor.machineId, communityId }
        const res = await listMonitorRecords(params)
        this.videoList = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleDeviceClick(item) {
      this.currentMonitor = item
      this.queryParams.machineId = item.machineId
      this.queryParams.page = 1
      this.fetchRecords()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchRecords()
    },
    handleReset() {
      this.queryParams.personName = ''
      this.queryParams.page = 1
      this.fetchRecords()
    },
    handlePreview(row) {
      this.playVideoInfo = {
        machineId: this.currentMonitor.machineId,
        machineName: row.machineName || this.currentMonitor.machineName,
        rvId: row.rvId,
        communityId: (getCurrentCommunity() || {}).communityId || ''
      }
      this.playVisible = true
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchRecords()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchRecords()
    }
  }
}
</script>

<style scoped>
.content-row { margin-bottom: 0; }
.left-card { min-height: 300px; }
.device-list { list-style: none; padding: 0; margin: 0; }
.device-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.device-list li:hover { background-color: #f5f7fa; }
.device-list li.active { background-color: #ecf5ff; color: #409eff; font-weight: 600; border-left: 3px solid #409eff; }
.device-list li.empty-tip { cursor: default; color: #c0c4cc; text-align: center; padding: 20px; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
