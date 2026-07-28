<template>
  <div class="tab-component">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item>
          <el-input v-model="queryParams.carNum" placeholder="必填，请填写车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择车辆状态" clearable style="width: 180px">
            <el-option label="进场状态" value="100300" />
            <el-option label="支付完成" value="100400" />
            <el-option label="离场状态" value="100500" />
            <el-option label="支付超时重新支付" value="100600" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.startTime" type="date" placeholder="开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.endTime" type="date" placeholder="结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" border stripe size="small">
        <el-table-column prop="inoutId" label="进出场编号" min-width="120" align="center" />
        <el-table-column label="进出方向" min-width="100" align="center">
          <template slot-scope="{ row }">{{ row.carInout === '3306' ? '进场' : '出场' }}</template>
        </el-table-column>
        <el-table-column prop="carNum" label="车牌号" min-width="120" align="center" />
        <el-table-column prop="inTime" label="进场时间" min-width="160" align="center" />
        <el-table-column prop="payTypeName" label="收费类型" min-width="100" align="center" />
        <el-table-column prop="payCharge" label="应收金额" min-width="100" align="center" />
        <el-table-column prop="realCharge" label="实收金额" min-width="100" align="center" />
        <el-table-column prop="createTime" label="支付时间" min-width="160" align="center" />
      </el-table>
      <div v-if="list.length > 0" class="summary-info">
        <span>应收合计：{{ list[0].payChargeTotal || 0 }}元</span>
        <span style="margin-left: 16px">实收合计：{{ list[0].realChargeTotal || 0 }}元</span>
      </div>
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
import { listCarInoutPayments } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlPayment',
  props: {
    paId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1, row: 10, carNum: '', state: '', startTime: '', endTime: ''
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarInoutPayments({
          ...this.queryParams,
          paId: this.paId,
          communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        console.error('获取收费明细失败', e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() { this.queryParams.page = 1; this.loadData() },
    handleReset() {
      this.queryParams = { page: 1, row: 10, carNum: '', state: '', startTime: '', endTime: '' }
      this.loadData()
    },
    handleSizeChange(val) { this.queryParams.row = val; this.loadData() },
    handlePageChange(val) { this.queryParams.page = val; this.loadData() }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.pagination-container { text-align: right; margin-top: 16px; }
.summary-info { padding: 8px 0; font-size: 13px; color: #606266; }
</style>
