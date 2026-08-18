<template>
  <div class="shop-detail-member">
    <div v-loading="loading">
      <el-table :data="memberList" border stripe>
        <el-table-column prop="name" label="名称" min-width="100" />
        <el-table-column prop="ownerTypeName" label="类型" min-width="100" />
        <el-table-column label="性别" width="70" align="center">
          <template slot-scope="{ row }">{{ row.sex === '0' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="link" label="联系方式" min-width="120" />
        <el-table-column prop="idCard" label="身份证" min-width="180" />
      </el-table>
      <el-empty v-if="!loading && memberList.length === 0" description="暂无数据" />
      <el-pagination
        v-if="total > 0"
        :current-page="query.page"
        :page-size="query.row"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        class="pagination-container"
      />
    </div>
  </div>
</template>

<script>
import { queryOwners } from '@/api/owner/ownerApi'

export default {
  name: 'ShopDetailMember',
  data() {
    return {
      loading: false,
      memberList: [],
      total: 0,
      ownerId: '',
      query: { page: 1, row: 10 }
    }
  },
  methods: {
    async open(ownerId) {
      this.ownerId = ownerId || ''
      this.memberList = []
      this.total = 0
      this.query.page = 1
      if (!this.ownerId) return
      await this.loadMembers()
    },
    async loadMembers() {
      this.loading = true
      try {
        const res = await queryOwners({
          ownerTypeCd: '2002',
          ownerId: this.ownerId,
          page: this.query.page,
          row: this.query.row
        })
        this.memberList = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.memberList = []
      } finally {
        this.loading = false
      }
    },
    handlePageChange(val) {
      this.query.page = val
      this.loadMembers()
    }
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
