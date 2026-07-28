<template>
  <div class="meter-machine-charge">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>抄表/充值记录</span>
        <div>
          <el-button size="small" @click="handleRefresh">刷新</el-button>
          <el-button size="small" @click="handleBack">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="chargeId" label="ID" width="100" align="center" />
        <el-table-column prop="roomName" label="房间号" min-width="120" />
        <el-table-column prop="machineName" label="表名称" min-width="120" />
        <el-table-column prop="machineId" label="机器编号" width="100" align="center" />
        <el-table-column prop="chargeMoney" label="金额" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column prop="state" label="状态" width="80" align="center" />
        <el-table-column prop="remark" label="说明" min-width="120">
          <template slot-scope="{ row }">
            {{ row.remark || '-' }}
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
    </el-card>
  </div>
</template>

<script>
import { listMeterMachineCharges } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MeterMachineCharge',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machineId: '',
      queryParams: {
        page: 1,
        row: 10,
        detailType: '2002'
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (!this.machineId) {
      this.$router.go(-1)
      return
    }
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachineCharges({
          ...this.queryParams,
          machineId: this.machineId,
          communityId: community.communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleRefresh() {
      this.fetchList()
    },
    handleBack() {
      this.$router.go(-1)
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
