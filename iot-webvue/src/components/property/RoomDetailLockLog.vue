<template>
  <div class="room-detail-lock-log">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="logId" label="编号" width="100" />
      <el-table-column prop="machineName" label="设备名称" min-width="120" />
      <el-table-column prop="machineCode" label="设备编码" width="120" />
      <el-table-column prop="logAction" label="日志动作" width="100" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="userName" label="用户名称" width="100" />
      <el-table-column prop="stateName" label="状态" width="80" />
      <el-table-column prop="createTime" label="下发时间" width="160" />
      <el-table-column label="报文" width="60" align="center">
        <template slot-scope="{ row }">
          <el-button v-if="row.context" type="text" size="small" @click="viewContext(row)">查看</el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="page" :page-size="row" :total="total"
      :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange" @current-change="handlePageChange"
      class="pagination-container"
    />
  </div>
</template>

<script>
import { listLockLog } from '@/api/property/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomDetailLockLog',
  props: { roomId: { type: String, default: '' } },
  data() {
    return { loading: false, list: [], page: 1, row: 10, total: 0 }
  },
  watch: {
    roomId: { immediate: true, handler() { this.fetchData() } }
  },
  methods: {
    async fetchData() {
      if (!this.roomId) { this.list = []; return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await listLockLog({ communityId: communityInfo.communityId, roomId: this.roomId, page: this.page, row: this.row })
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) { this.list = [] }
      finally { this.loading = false }
    },
    viewContext(row) {
      this.$alert(row.context, '报文内容', { confirmButtonText: '关闭' })
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchData() },
    handlePageChange(val) { this.page = val; this.fetchData() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
