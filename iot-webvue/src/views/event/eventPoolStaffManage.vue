<template>
  <div class="event-pool-staff-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="处理状态">
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="待处理" value="W" />
            <el-option label="处理中" value="D" />
            <el-option label="正常处理完成" value="NC" />
            <el-option label="超时处理完成" value="TC" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>事件处理信息</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="epsId" label="epsId" min-width="100" />
        <el-table-column prop="eventId" label="事件ID" min-width="100" />
        <el-table-column label="事故原因" width="80" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewReason(row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="处理方案" width="80" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewResult(row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="eventHours" label="处理时长" width="100" />
        <el-table-column prop="createTime" label="分配时间" width="170" />
        <el-table-column label="状态" width="110" align="center">
          <template slot-scope="{ row }">
            {{ stateMap[row.state] || row.state }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.state === 'W'"
              size="small"
              type="text"
              @click="handleAccept(row)"
            >
              接受分配
            </el-button>
            <el-button
              v-if="row.state !== 'W'"
              size="small"
              type="text"
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
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
    <ViewData :visible.sync="viewVisible" :title="viewTitle" :content="viewContent" />
    <EditEventPoolStaff :visible.sync="editStaffVisible" :eps-data="currentEpsData" @success="handleSuccess" />
    <ViewEventPoolStaffInfo :visible.sync="staffInfoVisible" :staff-data="currentStaffInfo" @success="handleSuccess" />
  </div>
</template>

<script>
import { listEventPoolStaffs } from '@/api/event/eventApi'
import ViewData from '@/components/event/ViewData.vue'
import EditEventPoolStaff from '@/components/event/EditEventPoolStaff.vue'
import ViewEventPoolStaffInfo from '@/components/event/ViewEventPoolStaffInfo.vue'

export default {
  name: 'EventPoolStaffManage',
  components: { ViewData, EditEventPoolStaff, ViewEventPoolStaffInfo },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      stateMap: { W: '待处理', D: '处理中', NC: '正常处理完成', TC: '超时处理完成' },
      dateRange: [],
      viewVisible: false,
      viewTitle: '',
      viewContent: '',
      editStaffVisible: false,
      staffInfoVisible: false,
      currentEpsData: {},
      currentStaffInfo: {},
      queryParams: {
        page: 1,
        row: 10,
        state: '',
        startTime: '',
        endTime: ''
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const params = { ...this.queryParams, communityId }
        if (this.dateRange && this.dateRange.length === 2) {
          params.startTime = this.dateRange[0]
          params.endTime = this.dateRange[1]
        } else {
          params.startTime = ''
          params.endTime = ''
        }
        const res = await listEventPoolStaffs(params)
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
      this.queryParams = { page: 1, row: 10, state: '', startTime: '', endTime: '' }
      this.dateRange = []
      this.fetchList()
    },
    handleViewReason(row) {
      this.viewTitle = '事故原因'
      this.viewContent = row.eventReason || ''
      this.viewVisible = true
    },
    handleViewResult(row) {
      this.viewTitle = '处理方案'
      this.viewContent = row.eventResult || ''
      this.viewVisible = true
    },
    handleAccept(row) {
      this.currentEpsData = { ...row }
      this.editStaffVisible = true
    },
    handleViewDetail(row) {
      this.currentStaffInfo = {
        epsId: row.epsId,
        staffName: row.staffName,
        createTime: row.createTime,
        state: row.state,
        stateName: this.stateMap[row.state] || row.state,
        eventReason: row.eventReason,
        eventResult: row.eventResult,
        eventHours: row.eventHours
      }
      this.staffInfoVisible = true
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
