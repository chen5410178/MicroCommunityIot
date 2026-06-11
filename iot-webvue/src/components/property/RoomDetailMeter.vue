<template>
  <div class="room-detail-meter">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="machineId" label="编号" width="100" />
      <el-table-column prop="machineName" label="名称" min-width="120" />
      <el-table-column prop="address" label="表号" width="120" />
      <el-table-column prop="meterTypeName" label="表类型" width="80" />
      <el-table-column label="模式" width="100" align="center">
        <template slot-scope="{ row }">{{ row.machineModel === '1001' ? '充值模式' : '抄表模式' }}</template>
      </el-table-column>
      <el-table-column prop="roomName" label="房屋" min-width="120" />
      <el-table-column prop="implBeanName" label="设备厂家" width="120" />
      <el-table-column label="剩余度数" width="100" align="center">
        <template slot-scope="{ row }">
          {{ row.machineModel === '1001' ? (row.degree || row.prestoreDegrees) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="当前度数" width="150">
        <template slot-scope="{ row }">
          <template v-if="row.machineModel === '1001'">-</template>
          <template v-else>{{ row.curDegrees }}(每月{{ row.readDay }}日{{ row.readHours }}时抄表)</template>
        </template>
      </el-table-column>
      <el-table-column prop="curReadingTime" label="读表时间" width="160" />
      <el-table-column prop="stateName" label="状态" width="80" />
      <el-table-column prop="heartbeatTime" label="心跳时间" width="160" />
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
import { listMeterMachine } from '@/api/property/propertyApi'

export default {
  name: 'RoomDetailMeter',
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
        const res = await listMeterMachine({ communityId: communityInfo.communityId, roomId: this.roomId, page: this.page, row: this.row })
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
