<template>
  <div class="role-community">
    <div class="toolbar">
      <el-button type="primary" size="small" @click="handleAdd">关联小区</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item>
        <el-input v-model="queryParams.communityName" placeholder="请输入小区名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="communityId" label="小区编号" min-width="120" />
      <el-table-column prop="communityName" label="小区名称" min-width="150" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="{ row }">
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
    <AddRoleCommunity :visible.sync="addVisible" :role-id="pgId" @success="handleSuccess" />
    <DeleteRoleCommunity :visible.sync="deleteVisible" :del-data="currentItem" :role-id="pgId" @success="handleSuccess" />
  </div>
</template>

<script>
import { listRoleCommunity } from '@/api/frame/roleApi'
import AddRoleCommunity from './addRoleCommunity.vue'
import DeleteRoleCommunity from './deleteRoleCommunity.vue'

export default {
  name: 'RoleCommunity',
  components: { AddRoleCommunity, DeleteRoleCommunity },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      pgId: '',
      addVisible: false,
      deleteVisible: false,
      currentItem: null,
      queryParams: {
        page: 1,
        row: 10,
        communityName: ''
      }
    }
  },
  methods: {
    open(pgId) {
      this.pgId = pgId
      this.queryParams = { page: 1, row: 10, communityName: '' }
      this.fetchList()
    },
    async fetchList() {
      if (!this.pgId) return
      this.loading = true
      try {
        const res = await listRoleCommunity({
          ...this.queryParams,
          roleId: this.pgId
        })
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
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleDelete(row) {
      this.currentItem = row
      this.deleteVisible = true
    },
    handleSuccess() {
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.search-form { margin-bottom: 0; }
.pagination-container { margin-top: 12px; text-align: right; }
</style>
