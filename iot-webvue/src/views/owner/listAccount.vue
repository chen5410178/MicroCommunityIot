<template>
  <div class="list-owner-account">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="账户名称">
          <el-input v-model="queryParams.acctName" placeholder="请输入账户名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="queryParams.link" placeholder="请输入联系方式" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>业主账户</span>
        <el-button type="primary" size="small" @click="handlePrestore">预存/维护</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="acctId" label="账户编号" min-width="200" />
        <el-table-column prop="acctName" label="账户名称" min-width="120" />
        <el-table-column prop="link" label="手机号" min-width="120" />
        <el-table-column prop="acctTypeName" label="账户类型" min-width="100" />
        <el-table-column prop="amount" label="账户金额" min-width="100" />
        <el-table-column prop="swipeCardNo" label="卡号" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">账户明细</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
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
    <PrestoreAccount :visible.sync="prestoreVisible" @success="fetchList" />
    <DeleteAccount :visible.sync="deleteVisible" :del-data="currentRow" @success="fetchList" />
  </div>
</template>

<script>
import { queryCommunityAccount } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'
import PrestoreAccount from '@/components/owner/PrestoreAccount.vue'
import DeleteAccount from '@/components/owner/DeleteAccount.vue'

export default {
  name: 'ListOwnerAccount',
  components: { PrestoreAccount, DeleteAccount },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        acctName: '',
        link: ''
      },
      prestoreVisible: false,
      deleteVisible: false,
      currentRow: null
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
        const res = await queryCommunityAccount({
          ...this.queryParams,
          communityId: community ? community.communityId : ''
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, acctName: '', link: '' }
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handlePrestore() {
      this.prestoreVisible = true
    },
    handleDetail(row) {
      this.$router.push({ path: '/pages/owner/accountDetail', query: { acctId: row.acctId } })
    },
    handleDelete(row) {
      this.currentRow = row
      this.deleteVisible = true
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
