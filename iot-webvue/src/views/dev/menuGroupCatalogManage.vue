<template>
  <div class="list-menu-group-catalog-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>{{ catalogName }}菜单组</span>
        <div>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          <el-button size="small" @click="handleBack">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="gcId" label="编号" min-width="150" />
        <el-table-column prop="mgName" label="菜单组名称" min-width="150" />
        <el-table-column prop="gId" label="菜单组Id" min-width="150" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
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
    </el-card>
    <AddMenuGroupCatalog :visible.sync="addVisible" :ca-id="caId" :store-type="storeType" @success="handleSuccess" />
    <DeleteMenuGroupCatalog :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMenuGroupCatalogs } from '@/api/dev/devApi'
import AddMenuGroupCatalog from '@/components/dev/AddMenuGroupCatalog.vue'
import DeleteMenuGroupCatalog from '@/components/dev/DeleteMenuGroupCatalog.vue'

export default {
  name: 'ListMenuGroupCatalogManage',
  components: { AddMenuGroupCatalog, DeleteMenuGroupCatalog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      caId: '',
      storeType: '',
      catalogName: '',
      addVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  created() {
    const { caId, storeType, catalogName } = this.$route.query
    this.caId = caId || ''
    this.storeType = storeType || ''
    this.catalogName = catalogName || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listMenuGroupCatalogs({
          ...this.queryParams,
          caId: this.caId,
          storeType: this.storeType
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleAdd() {
      this.addVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleSuccess() {
      this.fetchList()
    },
    handleBack() {
      this.$router.go(-1)
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
