<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="orderId" label="编号" min-width="120" />
      <el-table-column prop="personName" label="名称" min-width="100" />
      <el-table-column prop="personTel" label="手机号" min-width="120" />
      <el-table-column prop="machineName" label="充电桩" min-width="120" />
      <el-table-column prop="portName" label="插槽" min-width="80" />
      <el-table-column label="充电小时" width="100">
        <template slot-scope="{ row }">
          <span v-if="row.chargeHours == 999">按量充电</span>
          <span v-else>{{ row.chargeHours }}小时</span>
        </template>
      </el-table-column>
      <el-table-column prop="energy" label="充电量" width="80" />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column label="扣款账户" min-width="120">
        <template slot-scope="{ row }">{{ row.acctDetailId || '-' }}</template>
      </el-table-column>
      <el-table-column prop="durationPrice" label="小时电价" width="90" />
      <el-table-column label="扣款金额" width="100">
        <template slot-scope="{ row }">{{ row.amount || '-' }}</template>
      </el-table-column>
      <el-table-column prop="stateName" label="插座状态" min-width="100" />
      <el-table-column label="说明" min-width="180">
        <template slot-scope="{ row }">{{ row.remark || '-' }}</template>
      </el-table-column>
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { listChargeMachineOrder } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailChargeOrder',
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
      row: 10
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
        const res = await listChargeMachineOrder({
          personTel: this.link,
          communityId: community ? community.communityId : '',
          page: this.page,
          row: this.row
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchList() },
    handlePageChange(val) { this.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
