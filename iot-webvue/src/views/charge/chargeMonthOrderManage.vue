<template>
  <div class="charge-month-order-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.personName" placeholder="请输入充电人姓名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.personTel" placeholder="请输入充电电话" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryStartTime"
            type="datetime"
            placeholder="请选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryEndTime"
            type="datetime"
            placeholder="请选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>充电月卡</span>
        <el-button type="primary" size="small" @click="handleBuy">购买</el-button>
      </div>
      <div class="table-wrapper">
        <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
          <el-table-column prop="orderId" label="编号" min-width="200" />
          <el-table-column prop="cardName" label="月卡" min-width="120" />
          <el-table-column prop="cardMonth" label="月" min-width="80" align="center" />
          <el-table-column prop="cardPrice" label="月价" min-width="100" align="center" />
          <el-table-column prop="dayHours" label="充电小时" min-width="100" align="center" />
          <el-table-column prop="personName" label="充电人" min-width="100" />
          <el-table-column prop="personTel" label="充电电话" min-width="120" />
          <el-table-column prop="primeRateName" label="支付方式" min-width="100" align="center" />
          <el-table-column prop="receivableAmount" label="应收金额" min-width="100" align="center" />
          <el-table-column prop="receivedAmount" label="实收金额" min-width="100" align="center" />
          <el-table-column label="有效期" min-width="200" align="center">
            <template slot-scope="{ row }">
              {{ row.startTime }} ~ {{ row.endTime }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="购买时间" min-width="160" align="center" />
          <el-table-column label="备注" min-width="120" align="center">
            <template slot-scope="{ row }">
              <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
                <el-button slot="reference" size="small" type="text">查看</el-button>
              </el-popover>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
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
    <AddChargeMonthOrder :visible.sync="buyVisible" @success="handleSuccess" />
  </div>
</template>

<script>
import { listChargeMonthOrders } from '@/api/charge/chargeApi'
import AddChargeMonthOrder from '@/components/charge/AddChargeMonthOrder.vue'

export default {
  name: 'ChargeMonthOrderManage',
  components: { AddChargeMonthOrder },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      buyVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        personName: '',
        personTel: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listChargeMonthOrders({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        personName: '',
        personTel: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleBuy() {
      this.buyVisible = true
    },
    handleSuccess() {
      this.fetchList()
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.table-wrapper { overflow-x: auto; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
