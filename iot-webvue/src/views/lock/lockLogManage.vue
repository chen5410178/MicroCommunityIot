<template>
  <div class="lock-log-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入门锁编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.userName" placeholder="请选择业主" clearable style="width: 180px" />
          <el-button type="primary" style="margin-left: 8px" @click="handleChooseOwner">选择</el-button>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="请求成功" value="10001" />
            <el-option label="返回成功" value="10002" />
            <el-option label="操作失败" value="10003" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryStartTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryEndTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
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
        <span>下发记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="logId" label="日志编号" min-width="120" />
        <el-table-column prop="machineName" label="门锁名称" min-width="150" />
        <el-table-column prop="machineCode" label="门锁编号" min-width="150" />
        <el-table-column prop="userName" label="操作人名称" min-width="120" />
        <el-table-column prop="lockActionName" label="日志动作" min-width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state === '10001'" type="success" size="small">请求成功</el-tag>
            <el-tag v-else-if="row.state === '10002'" type="success" size="small">返回成功</el-tag>
            <el-tag v-else-if="row.state === '10003'" type="danger" size="small">操作失败</el-tag>
            <span v-else>-</span>
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

    <SearchOwner :visible.sync="ownerVisible" @selected="handleOwnerSelected" />
    <ShowLockLog :visible.sync="logVisible" :log-data="currentLog" />
  </div>
</template>

<script>
import { listLockLogs } from '@/api/lock/lockApi'
import SearchOwner from '@/components/monitor/SearchOwner.vue'
import ShowLockLog from '@/components/lock/ShowLockLog.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LockLogManage',
  components: { SearchOwner, ShowLockLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      ownerVisible: false,
      logVisible: false,
      currentLog: {},
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        userName: '',
        userId: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listLockLogs({ ...this.queryParams, communityId })
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
        machineCode: '',
        userName: '',
        userId: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleChooseOwner() {
      this.ownerVisible = true
    },
    handleOwnerSelected(owner) {
      this.queryParams.userName = owner.name || ''
      this.queryParams.userId = owner.memberId || ''
    },
    handleShowLog(row) {
      this.currentLog = { reqParam: row.reqParam || '', resParam: row.resParam || '' }
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
