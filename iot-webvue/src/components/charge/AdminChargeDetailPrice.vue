<template>
  <div class="admin-charge-detail-price">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="priceId" label="价格ID" min-width="120" />
      <el-table-column prop="startEnergy" label="开始电量" min-width="120" />
      <el-table-column prop="endEnergy" label="结束电量" min-width="120" />
      <el-table-column prop="price" label="电价" min-width="100" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
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
import { listAdminChargeRulePrice } from '@/api/charge/chargeApi'

export default {
  name: 'AdminChargeDetailPrice',
  props: {
    ruleId: { type: String, default: '' }
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
    ruleId: {
      immediate: true,
      handler() {
        if (this.ruleId) {
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
        const res = await listAdminChargeRulePrice({
          ruleId: this.ruleId,
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
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
