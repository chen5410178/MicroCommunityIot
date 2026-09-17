<template>
  <div class="invoice-order-record">
    <div class="table-header">
      <span>开票记录</span>
      <el-button type="primary" size="small" @click="handleApply">开票</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="invoiceOrderId" label="开票单号" min-width="180" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column label="抬头类型" min-width="100" align="center">
        <template slot-scope="{ row }">{{ row.headingType || '-' }}</template>
      </el-table-column>
      <el-table-column prop="buyerName" label="抬头名称" min-width="160" />
      <el-table-column label="税号" min-width="160">
        <template slot-scope="{ row }">{{ row.buyerTaxNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="业务类目" min-width="100" align="center">
        <template slot-scope="{ row }">{{ row.bizCategory || '-' }}</template>
      </el-table-column>
      <el-table-column label="金额" min-width="100" align="right">
        <template slot-scope="{ row }">¥{{ Number(row.amount || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" min-width="110" align="center">
        <template slot-scope="{ row }">
          <span :class="{ 'state-fail': row.state === 'FAIL' }">{{ stateText(row.state) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发票号" min-width="140">
        <template slot-scope="{ row }">{{ row.invoiceNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="失败原因" min-width="160">
        <template slot-scope="{ row }">
          <el-tooltip :content="row.failReason || '-'" :disabled="!row.failReason" placement="top">
            <div class="text-ellipsis">{{ row.failReason || '-' }}</div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="发票PDF" min-width="100" align="center">
        <template slot-scope="{ row }">
          <el-button v-if="row.pdfUrl" size="small" type="text" @click="handleDownload(row)">下载</el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" min-width="160">
        <template slot-scope="{ row }">{{ row.createTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right" align="center">
        <template slot-scope="{ row }">
          <el-button v-if="row.state === 'DONE'" size="small" type="text" @click="handleRed(row)">红冲</el-button>
          <el-button v-if="row.state === 'FAIL'" size="small" type="text" @click="handleReSubmit(row)">重新提交</el-button>
          <el-button
            v-if="row.state !== 'DONE' && row.state !== 'RED'"
            size="small"
            type="text"
            style="color: #f56c6c"
            @click="handleDelete(row)"
          >删除</el-button>
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

    <InvoiceRedDialog :visible.sync="redVisible" @success="handleSuccess" />
  </div>
</template>

<script>
import { listInvoiceOrders, reSubmitInvoice, deleteInvoiceOrder } from '@/api/property/invoiceApi'
import InvoiceRedDialog from '@/components/property/InvoiceRedDialog.vue'

export default {
  name: 'InvoiceOrderRecord',
  components: { InvoiceRedDialog },
  props: {
    // 父页面查询条件（手机号/抬头名称/状态/发票号）
    conditions: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      redVisible: false,
      queryParams: {
        page: 1,
        row: 10
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
        const res = await listInvoiceOrders({
          page: this.queryParams.page,
          row: this.queryParams.row,
          phone: this.conditions.phone || '',
          buyerName: this.conditions.buyerName || '',
          state: this.conditions.state || '',
          invoiceNo: this.conditions.invoiceNo || ''
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.$message.error(e.message || '查询开票记录失败')
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
    handleApply() {
      this.$router.push('/pages/property/invoiceApply')
    },
    handleDownload(row) {
      if (!row.pdfUrl) {
        this.$message.warning('暂无发票PDF')
        return
      }
      window.open(row.pdfUrl)
    },
    handleRed(row) {
      this.$refs.redDialog.open(row)
    },
    async handleReSubmit(row) {
      try {
        const res = await reSubmitInvoice({ invoiceOrderId: row.invoiceOrderId })
        const data = res.data || {}
        if (data.state === 'F') {
          this.$message.error(data.failReason || '重新提交失败')
        } else {
          this.$message.success('重新提交成功')
        }
        this.fetchList()
      } catch (e) {
        this.$message.error(e.message || '重新提交失败')
      }
    },
    handleDelete(row) {
      this.$confirm('确定删除该开票记录吗？此操作不可恢复', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteInvoiceOrder({ invoiceOrderId: row.invoiceOrderId })
          this.$message.success('删除成功')
          this.fetchList()
        } catch (e) {
          this.$message.error(e.message || '删除开票记录失败')
        }
      }).catch(() => {})
    },
    handleSuccess() {
      this.fetchList()
    },
    stateText(state) {
      const map = {
        AUDIT: '待审核',
        WAIT: '待开票',
        DONE: '已开票',
        FAIL: '开票失败',
        RED: '已红冲'
      }
      return map[state] || state || '-'
    }
  }
}
</script>

<style scoped>
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.text-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.state-fail {
  color: #f56c6c;
}

.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
