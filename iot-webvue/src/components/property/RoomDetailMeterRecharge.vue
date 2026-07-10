<template>
  <div class="room-detail-meter-recharge">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="detailId" label="编号" />
      <el-table-column prop="machineName" label="设备名称" />
      <el-table-column prop="machineCode" label="设备编码" />
      <el-table-column prop="detailTypeName" label="类型" />
      <el-table-column prop="amount" label="金额" align="center" />
      <el-table-column prop="beforeDegrees" label="充值前度数" align="center" />
      <el-table-column prop="afterDegrees" label="充值后度数" align="center" />
      <el-table-column prop="stateName" label="状态" />
      <el-table-column prop="createTime" label="充值时间" />
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
import { listMeterMachineReadOrRechargeDetail } from '@/api/property/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomDetailMeterRecharge',
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
        const res = await listMeterMachineReadOrRechargeDetail({ communityId: communityInfo.communityId, roomId: this.roomId, detailType: '1001', page: this.page, row: this.row })
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
