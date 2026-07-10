<template>
  <div class="charge-detail-order">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="orderId" label="编号" min-width="100" />
      <el-table-column prop="personName" label="名称" min-width="100" />
      <el-table-column prop="personTel" label="手机号" min-width="120" />
      <el-table-column prop="machineName" label="充电桩" min-width="120" />
      <el-table-column prop="portName" label="插槽" min-width="100" />
      <el-table-column label="充电小时" width="100" align="center">
        <template slot-scope="{ row }">
          {{ row.chargeHours == 999 ? '按量充电' : row.chargeHours + '小时' }}
        </template>
      </el-table-column>
      <el-table-column prop="energy" label="充电量" width="90" align="center" />
      <el-table-column prop="startTime" label="开始时间" min-width="160" />
      <el-table-column prop="endTime" label="结束时间" min-width="160" />
      <el-table-column prop="acctDetailId" label="扣款账户" min-width="120" />
      <el-table-column prop="durationPrice" label="小时电价" width="90" align="center" />
      <el-table-column prop="amount" label="扣款金额" width="100" align="center" />
      <el-table-column prop="stateName" label="插座状态" width="90" align="center" />
      <el-table-column prop="remark" label="说明" min-width="200" />
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
  </div>
</template>

<script>
import { listChargeMachineOrder } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeDetailOrder',
  props: {
    machineId: { type: String, default: '' },
    orderId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: { page: 1, row: 10 }
    }
  },
  watch: {
    machineId: {
      immediate: true,
      handler() {
        if (this.machineId) {
          this.queryParams.page = 1
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeMachineOrder({
          machineId: this.machineId,
          orderId: this.orderId,
          communityId,
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
