<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="logId" label="编号" min-width="120" />
      <el-table-column prop="machineName" label="设备名称" min-width="120" />
      <el-table-column prop="machineCode" label="设备编码" min-width="120" />
      <el-table-column prop="logAction" label="日志动作" min-width="100" />
      <el-table-column prop="userId" label="用户ID" min-width="100" />
      <el-table-column prop="userName" label="用户名称" min-width="100" />
      <el-table-column prop="stateName" label="状态" width="90" />
      <el-table-column prop="createTime" label="下发时间" width="160" />
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
import { listAccessControlLogs } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailAccessControlLog',
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
        const res = await listAccessControlLogs({
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
