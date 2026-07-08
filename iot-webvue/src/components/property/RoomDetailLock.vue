<template>
  <div class="room-detail-lock">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="machineId" label="门锁ID" width="100" />
      <el-table-column prop="machineName" label="门锁名称" min-width="120" />
      <el-table-column prop="machineCode" label="门锁编号" width="120" />
      <el-table-column prop="roomName" label="房屋名称" min-width="120" />
      <el-table-column label="剩余电量" width="120" align="center">
        <template slot-scope="{ row }">{{ row.charge }}%</template>
      </el-table-column>
      <el-table-column prop="factoryName" label="门禁厂家" width="120" />
      <el-table-column prop="stateName" label="在线状态" width="80" />
      <el-table-column prop="heartbeatTime" label="心跳时间" width="160" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
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
import { listLockMachine } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomDetailLock',
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
        const res = await listLockMachine({ communityId: communityInfo.communityId, roomId: this.roomId, page: this.page, row: this.row })
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) { this.list = [] }
      finally { this.loading = false }
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchData() },
    handlePageChange(val) { this.page = val; this.fetchData() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
