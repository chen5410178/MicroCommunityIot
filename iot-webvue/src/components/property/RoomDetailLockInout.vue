<template>
  <div class="room-detail-lock-inout">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="inoutId" label="编号" width="100" />
      <el-table-column prop="machineName" label="设备名称" min-width="120" />
      <el-table-column prop="machineCode" label="设备编码" width="120" />
      <el-table-column prop="name" label="用户名称" width="100" />
      <el-table-column label="开门方式" width="100" align="center">
        <template slot-scope="{ row }">{{ row.openTypeName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="tel" label="手机号" width="120" />
      <el-table-column label="开门状态" width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="row.state === 'C' ? 'success' : 'danger'" size="small">
            {{ row.state === 'C' ? '开门成功' : '开门失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="开门时间" width="160" />
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
import { listLockInout } from '@/api/property/propertyApi'

export default {
  name: 'RoomDetailLockInout',
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
        const communityInfo = this.$store.state.app.currentCommunity || {}
        const res = await listLockInout({ communityId: communityInfo.communityId, roomId: this.roomId, page: this.page, row: this.row })
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
