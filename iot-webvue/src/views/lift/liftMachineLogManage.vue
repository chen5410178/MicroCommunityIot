<template>
  <div class="lift-machine-log-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入电梯名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入电梯编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.logAction" placeholder="请输入日志动作" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.state" placeholder="请输入状态" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.createTime"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            clearable
            style="width: 180px"
          />
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
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>电梯日志</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="logId" label="日志ID" min-width="150" />
        <el-table-column prop="machineName" label="电梯名称" min-width="150" />
        <el-table-column prop="machineCode" label="电梯编号" min-width="120" />
        <el-table-column prop="logActionName" label="日志动作" min-width="100" />
        <el-table-column prop="stateName" label="状态" min-width="100" />
        <el-table-column prop="personName" label="操作人" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="100" fixed="right" align="center">
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
    <ShowLiftMachineLog :visible.sync="logVisible" :log-data="currentLog" />
  </div>
</template>

<script>
import { listLiftMachineLogs } from '@/api/lift/liftApi'
import ShowLiftMachineLog from '@/components/lift/ShowLiftMachineLog.vue'

export default {
  name: 'LiftMachineLogManage',
  components: { ShowLiftMachineLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      logVisible: false,
      currentLog: { reqParam: '', resParam: '' },
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineName: '',
        machineCode: '',
        logAction: '',
        state: '',
        createTime: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  computed: {
    currentCommunityId() {
      const community = this.$store.state.app.currentCommunity || {}
      return community.communityId || ''
    }
  },
  watch: {
    currentCommunityId: {
      immediate: true,
      handler(val) {
        this.queryParams.communityId = val
        if (val) {
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listLiftMachineLogs(this.queryParams)
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
      this.queryParams = {
        page: 1,
        row: 10,
        communityId: this.queryParams.communityId,
        machineName: '',
        machineCode: '',
        logAction: '',
        state: '',
        createTime: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleViewLog(row) {
      this.currentLog = {
        reqParam: row.reqParam || '',
        resParam: row.resParam || ''
      }
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
