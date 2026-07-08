<template>
  <div class="list-attendance-machine">
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
          <el-input v-model="queryParams.classesName" placeholder="请输入班组名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>考勤设备</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineCode" label="设备编码" min-width="150" />
        <el-table-column prop="machineName" label="设备名称" min-width="150" />
        <el-table-column prop="classesId" label="班组ID" min-width="120" />
        <el-table-column prop="classesName" label="班组名称" min-width="120" />
        <el-table-column prop="machineIp" label="设备IP" min-width="140" />
        <el-table-column prop="implBean" label="门禁厂家" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column prop="stateName" label="状态" min-width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.stateName === '在线' ? 'success' : 'info'" size="small">{{ row.stateName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
        <el-table-column label="监控视频" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button v-if="row.monitorId" size="small" type="text" @click="handlePlayVideo(row)">观看</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
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
    <AddAttendanceMachine :visible.sync="addVisible" @success="handleSuccess" />
    <EditAttendanceMachine :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteAttendanceMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <PlayMonitorVideo :visible.sync="videoVisible" :video-info="videoInfo" />
  </div>
</template>

<script>
import { listAttendanceMachines } from '@/api/attendance/attendanceApi'
import AddAttendanceMachine from '@/components/attendance/AddAttendanceMachine.vue'
import EditAttendanceMachine from '@/components/attendance/EditAttendanceMachine.vue'
import DeleteAttendanceMachine from '@/components/attendance/DeleteAttendanceMachine.vue'
import PlayMonitorVideo from '@/components/monitor/PlayMonitorVideo.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AttendanceMachine',
  components: { AddAttendanceMachine, EditAttendanceMachine, DeleteAttendanceMachine, PlayMonitorVideo },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      videoVisible: false,
      currentRow: null,
      videoInfo: {},
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        classesName: ''
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
        const res = await listAttendanceMachines(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', classesName: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handlePlayVideo(row) {
      this.videoInfo = {
        machineId: row.monitorId,
        machineName: row.monitorName || '监控视频',
        communityId: (getCurrentCommunity() || {}).communityId
      }
      this.videoVisible = true
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
