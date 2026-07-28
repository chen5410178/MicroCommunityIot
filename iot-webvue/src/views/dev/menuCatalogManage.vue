<template>
  <div class="list-menu-catalog-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入菜单组名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.storeType" placeholder="请选择商户类型" clearable style="width: 180px">
            <el-option label="运营团队" value="800900000001" />
            <el-option label="物业" value="800900000003" />
            <el-option label="商家" value="800900000005" />
            <el-option label="开发团队" value="800900000000" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.isShow" placeholder="请选择是否显示" clearable style="width: 180px">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>菜单目录</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="caId" label="编号" width="150" />
        <el-table-column prop="name" label="菜单组名称" min-width="150" />
        <el-table-column prop="icon" label="图标" min-width="120" />
        <el-table-column prop="seq" label="顺序" width="80" />
        <el-table-column label="商户类型" width="120" align="center">
          <template slot-scope="{ row }">{{ storeTypeMap[row.storeType] || row.storeType }}</template>
        </el-table-column>
        <el-table-column prop="url" label="页面" min-width="150" />
        <el-table-column label="是否显示" width="100" align="center">
          <template slot-scope="{ row }">{{ row.isShow === 'Y' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" @click="handleMenuGroup(row)">菜单组</el-button>
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
    <AddMenuCatalog :visible.sync="addVisible" @success="handleSuccess" />
    <EditMenuCatalog :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMenuCatalog :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMenuCatalogs } from '@/api/dev/devApi'
import AddMenuCatalog from '@/components/dev/AddMenuCatalog.vue'
import EditMenuCatalog from '@/components/dev/EditMenuCatalog.vue'
import DeleteMenuCatalog from '@/components/dev/DeleteMenuCatalog.vue'

export default {
  name: 'ListMenuCatalogManage',
  components: { AddMenuCatalog, EditMenuCatalog, DeleteMenuCatalog },
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
        name: '',
        storeType: '',
        isShow: ''
      },
      storeTypeMap: {
        '800900000001': '运营团队',
        '800900000003': '物业',
        '800900000005': '商家',
        '800900000000': '开发团队'
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
        const res = await listMenuCatalogs(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, name: '', storeType: '', isShow: '' }
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
    handleMenuGroup(row) {
      this.$router.push(`/pages/dev/menuGroupCatalogManage?caId=${row.caId}&storeType=${row.storeType}&catalogName=${row.name}`)
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
