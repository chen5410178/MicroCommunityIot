<template>
  <div class="car-month-order-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.cardId" placeholder="请选择月卡" clearable style="width: 180px">
            <el-option v-for="item in cards" :key="item.cardId" :label="item.cardName" :value="item.cardId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.carNum" placeholder="请输入车牌号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.primeRate" placeholder="请选择支付方式" clearable style="width: 160px">
            <el-option
              v-for="item in primeRates"
              :key="item.statusCd"
              v-if="item.statusCd !== '5' && item.statusCd !== '6'"
              :label="item.name"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="正常" value="1001" />
            <el-option label="退费" value="2002" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.orderId" placeholder="请输入订单编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-form :inline="true" :model="queryParams">
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
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>月卡订单</span>
        <div>
          <el-button type="text" size="small" @click="handleQrCode">二维码购买</el-button>
          <el-button type="primary" size="small" @click="handleBuy">购买月卡</el-button>
        </div>
      </div>
      <div class="table-wrapper">
      <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="orderId" label="订单编号" min-width="220" />
        <el-table-column prop="cardName" label="月卡" min-width="100" />
        <el-table-column prop="carNum" label="车牌号" min-width="100" />
        <el-table-column prop="psNum" label="车位" min-width="100" align="center" />
        <el-table-column prop="primeRateName" label="支付方式" min-width="100" align="center" />
        <el-table-column prop="receivableAmount" label="应收金额" min-width="100" align="center" />
        <el-table-column prop="receivedAmount" label="实收金额" min-width="100" align="center" />
        <el-table-column label="有效期" min-width="200" align="center">
          <template slot-scope="{ row }">
            {{ row.startTime }} ~ {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state === '1001'" size="small">正常</el-tag>
            <el-tag v-else-if="row.state === '2002'" type="danger" size="small">已退费</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="cashierName" label="收银员" min-width="100" align="center" />
        <el-table-column prop="createTime" label="购买时间" min-width="160" align="center" />
        <el-table-column label="备注" min-width="120" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.state === '1001'"
              size="small"
              type="text"
              style="color: #f56c6c"
              @click="handleRefund(row)"
            >退款</el-button>
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
    <BuyCarMonthCard :visible.sync="buyVisible" @success="handleSuccess" />
    <RetundCarMonthOrder :visible.sync="refundVisible" :order-data="currentRow" @success="handleSuccess" />
    <BuyMonthCardQrCode :visible.sync="qrCodeVisible" />
  </div>
</template>

<script>
import { listCarMonthOrders, listCarMonthCards } from '@/api/car/carApi'
import { listDicts } from '@/api/property/propertyApi'
import BuyCarMonthCard from '@/components/car/BuyCarMonthCard.vue'
import RetundCarMonthOrder from '@/components/car/RetundCarMonthOrder.vue'
import BuyMonthCardQrCode from '@/components/car/BuyMonthCardQrCode.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'CarMonthOrder',
  components: { BuyCarMonthCard, RetundCarMonthOrder, BuyMonthCardQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      cards: [],
      primeRates: [],
      buyVisible: false,
      refundVisible: false,
      qrCodeVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        cardId: '',
        carNum: '',
        primeRate: '',
        state: '',
        orderId: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  mounted() {
    this.fetchDicts()
    this.fetchCards()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarMonthOrders({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchCards() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarMonthCards({ page: 1, row: 100, communityId })
        this.cards = res.data || []
      } catch (e) { /* ignore */ }
    },
    async fetchDicts() {
      try {
        const res = await listDicts({ page: 1, row: 100, tableName: 'pay_fee_detail', columnName: 'prime_rate' })
        this.primeRates = res.data || []
      } catch (e) { /* ignore */ }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1, row: 10,
        cardId: '', carNum: '', primeRate: '', state: '', orderId: '',
        queryStartTime: '', queryEndTime: ''
      }
      this.fetchList()
    },
    handleBuy() {
      this.buyVisible = true
    },
    handleQrCode() {
      this.qrCodeVisible = true
    },
    handleRefund(row) {
      this.currentRow = { ...row }
      this.refundVisible = true
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
