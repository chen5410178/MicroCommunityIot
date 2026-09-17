<template>
  <div class="invoice-red-record">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="applyId" label="申请单号" min-width="180" />
      <el-table-column prop="redId" label="冲红单号" min-width="180" />
      <el-table-column label="发票号码" min-width="140">
        <template slot-scope="{ row }">{{ row.invoiceNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="抬头" min-width="160">
        <template slot-scope="{ row }">{{ row.buyerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="手机号" min-width="120">
        <template slot-scope="{ row }">{{ row.phone || '-' }}</template>
      </el-table-column>
      <el-table-column label="金额" min-width="100" align="right">
        <template slot-scope="{ row }">¥{{ Number(row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="冲红原因" min-width="110">
        <template slot-scope="{ row }">{{ row.reason || '-' }}</template>
      </el-table-column>
      <el-table-column label="备注" min-width="160">
        <template slot-scope="{ row }">
          <el-tooltip :content="row.remark || '-'" :disabled="!row.remark" placement="top">
            <div class="text-ellipsis">{{ row.remark || '-' }}</div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="100" align="center">
        <template slot-scope="{ row }">{{ stateText(row.state) }}</template>
      </el-table-column>
      <el-table-column label="红字发票号" min-width="140">
        <template slot-scope="{ row }">{{ row.redNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="时间" min-width="160">
        <template slot-scope="{ row }">{{ row.createTime || '-' }}</template>
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
  </div>
</template>

<script>
import { listInvoiceReds } from '@/api/property/invoiceApi'

export default {
  name: 'InvoiceRedRecord',
  props: {
    // 父页面查询条件（手机号/状态）
    conditions: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        phone: '',
        state: ''
      }
    }
  },
  created() {
    this.reload()
  },
  methods: {
    // 由父页面在查询/重置/切换菜单后调用
    reload() {
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listInvoiceReds({
          page: this.queryParams.page,
          row: this.queryParams.row,
          phone: this.conditions.phone || '',
          state: this.conditions.state || ''
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        // 商城未开启或调用异常时后端返回空分页，此处兜底为空列表
        this.list = []
        this.total = 0
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
    },
    stateText(state) {
      if (state === 'DONE') {
        return '已冲红'
      }
      if (state === 'WAIT') {
        return '待冲红'
      }
      return state || '-'
    }
  }
}
</script>

<style scoped>
.text-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
