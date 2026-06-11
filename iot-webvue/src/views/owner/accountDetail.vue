<template>
  <div class="owner-account-detail">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>账户明细</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="detailId" label="明细编号" min-width="200" />
        <el-table-column prop="orderId" label="交易编号" min-width="200" />
        <el-table-column label="明细类型" min-width="120" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="detailTypeTag(row.detailType)" size="small">
              {{ detailTypeLabel(row.detailType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" min-width="100" />
        <el-table-column prop="createTime" label="交易时间" min-width="160" />
        <el-table-column prop="remark" label="说明" min-width="160" />
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
import { queryOwnerAccountDetail } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerAccountDetail',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10
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
        const community = getCurrentCommunity()
        const res = await queryOwnerAccountDetail({
          ...this.queryParams,
          acctId: this.$route.query.acctId,
          communityId: community ? community.communityId : ''
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
    },
    handleBack() {
      this.$router.back()
    },
    detailTypeLabel(type) {
      if (type === '1001') return '转入'
      if (type === '3003') return '转入已撤销'
      return '转出'
    },
    detailTypeTag(type) {
      if (type === '1001') return 'success'
      if (type === '3003') return 'warning'
      return 'danger'
    }
  }
}
</script>

<style scoped>
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
