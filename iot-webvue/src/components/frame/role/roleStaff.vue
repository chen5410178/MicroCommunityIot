<template>
  <div class="role-staff">
    <div class="toolbar">
      <el-button type="primary" size="small" @click="handleAdd">关联员工</el-button>
    </div>
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item label="员工名称">
        <el-input v-model="queryParams.userName" placeholder="请输入员工名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="name" label="名称" min-width="100" />
      <el-table-column prop="tel" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="150" />
      <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
      <el-table-column prop="sex" label="性别" width="60" align="center">
        <template slot-scope="{ row }">
          {{ row.sex === '0' ? '男' : row.sex === '1' ? '女' : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
    <AddRoleStaff :visible.sync="addVisible" :role-id="pgId" @success="handleSuccess" />
    <DeleteRoleStaff :visible.sync="deleteVisible" :del-data="currentItem" :role-id="pgId" @success="handleSuccess" />
  </div>
</template>

<script>
import { listRoleStaff } from '@/api/frame/roleApi'
import AddRoleStaff from './addRoleStaff.vue'
import DeleteRoleStaff from './deleteRoleStaff.vue'

export default {
  name: 'RoleStaff',
  components: { AddRoleStaff, DeleteRoleStaff },
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
        userName: ''
      }
    }
  },
  methods: {
    open(pgId) {
      this.pgId = pgId
      this.queryParams = { page: 1, row: 10, userName: '' }
      this.fetchList()
    },
    async fetchList() {
      if (!this.pgId) return
      this.loading = true
      try {
        const res = await listRoleStaff({
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
    handleDetail(row) {
      this.$router.push({ path: '/pages/frame/staffDetail', query: { staffId: row.userId || row.staffId || row.id } })
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
