<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="logId" label="日志编号" min-width="120" />
      <el-table-column prop="machineName" label="门锁名称" min-width="120" />
      <el-table-column prop="machineCode" label="门锁编号" min-width="120" />
      <el-table-column prop="userName" label="业主名称" min-width="100" />
      <el-table-column label="日志动作" width="100">
        <template slot-scope="{ row }">
          <span v-if="row.logAction === 'unlock'">开锁</span>
          <span v-else>{{ row.logAction || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <span v-if="row.state === '10001'">请求成功</span>
          <span v-else-if="row.state === '10002'">返回成功</span>
          <span v-else-if="row.state === '10003'">操作失败</span>
          <span v-else>{{ row.stateName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="报文" width="80" align="center" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="showMsg(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
    <el-dialog title="报文详情" :visible.sync="dialogVisible" width="600px">
      <pre style="max-height: 400px; overflow: auto;">{{ currentMsg }}</pre>
    </el-dialog>
  </div>
</template>

<script>
import { listLockLogs } from '@/api/lock/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailLockLog',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      page: 1,
      row: 10,
      dialogVisible: false,
      currentMsg: ''
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await listLockLogs({
          personId: this.ownerId,
          communityId: community ? community.communityId : '',
          page: this.page,
          row: this.row
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    showMsg(row) {
      this.currentMsg = JSON.stringify(row, null, 2)
      this.dialogVisible = true
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchList() },
    handlePageChange(val) { this.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
