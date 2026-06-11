<template>
  <div class="list-base-privilege-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="权限名称">
          <el-input v-model="queryParams.name" placeholder="请输入权限名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="queryParams.pId" placeholder="请输入权限编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>权限信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加权限</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="pId" label="权限ID" width="180" />
        <el-table-column prop="name" label="权限名称" min-width="120" />
        <el-table-column prop="resource" label="资源路径" min-width="150" />
        <el-table-column label="商户类型" width="120" align="center">
          <template slot-scope="{ row }">{{ storeTypeMap[row.domain] || row.domain }}</template>
        </el-table-column>
        <el-table-column prop="stName" label="商户名称" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
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
    <AddBasePrivilege :visible.sync="addVisible" :m-id="mId" @success="handleSuccess" />
    <EditBasePrivilege :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteBasePrivilege :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listBasePrivileges } from '@/api/dev/devApi'
import AddBasePrivilege from '@/components/dev/AddBasePrivilege.vue'
import EditBasePrivilege from '@/components/dev/EditBasePrivilege.vue'
import DeleteBasePrivilege from '@/components/dev/DeleteBasePrivilege.vue'

export default {
  name: 'ListBasePrivilegeManage',
  components: { AddBasePrivilege, EditBasePrivilege, DeleteBasePrivilege },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      mId: '',
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        name: '',
        pId: ''
      },
      storeTypeMap: {
        '800900000001': '运营团队',
        '800900000003': '物业',
        '800900000000': '开发团队'
      }
    }
  },
  mounted() {
    this.mId = this.$route.query.mId || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = { ...this.queryParams, mId: this.mId }
        const res = await listBasePrivileges(params)
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
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
