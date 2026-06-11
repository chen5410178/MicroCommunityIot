<template>
  <div class="list-mapping-domain-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="域">
          <el-input v-model="queryParams.domain" placeholder="请选择域" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="queryParams.seq" placeholder="请选择排序" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>配置项域</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="domainName" label="名称" min-width="150" />
        <el-table-column prop="domain" label="配置项" min-width="150" />
        <el-table-column prop="seq" label="排序" width="100" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
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
    <AddMappingDomain :visible.sync="addVisible" @success="handleSuccess" />
    <EditMappingDomain :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMappingDomain :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMappingDomains } from '@/api/dev/devApi'
import AddMappingDomain from '@/components/dev/AddMappingDomain.vue'
import EditMappingDomain from '@/components/dev/EditMappingDomain.vue'
import DeleteMappingDomain from '@/components/dev/DeleteMappingDomain.vue'

export default {
  name: 'ListMappingDomainManage',
  components: { AddMappingDomain, EditMappingDomain, DeleteMappingDomain },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        domain: '',
        seq: ''
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
        const res = await listMappingDomains(this.queryParams)
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
