<template>
  <div class="lock-detail-log">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="logId" label="日志编号" min-width="140" />
      <el-table-column prop="machineName" label="门锁名称" min-width="150" />
      <el-table-column prop="machineCode" label="门锁编码" min-width="150" />
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
    <ShowLockLog :visible.sync="logVisible" :log-data="currentLog" />
  </div>
</template>

<script>
import { listLockLogs } from '@/api/lock/lockApi'
import ShowLockLog from '@/components/lock/ShowLockLog.vue'
import { getCommunityId } from '@/utils/auth'

export default {
  name: 'LockDetailLog',
  components: { ShowLockLog },
  props: {
    machineId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      logVisible: false,
      currentLog: {},
      queryParams: { page: 1, row: 10 }
    }
  },
  watch: {
    machineId: {
      immediate: true,
      handler() {
        if (!this.machineId) {
          this.list = []
          this.total = 0
          return
        }
        this.queryParams.page = 1
        this.fetchList()
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listLockLogs({
          machineId: this.machineId,
          communityId: getCommunityId(),
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.list = []
        this.total = 0
        this.$message.error(e.message || '获取下发日志失败')
      } finally {
        this.loading = false
      }
    },
    handleShowLog(row) {
      this.currentLog = { reqParam: row.reqParam || '', resParam: row.resParam || '' }
      this.logVisible = true
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.queryParams.page = 1
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
