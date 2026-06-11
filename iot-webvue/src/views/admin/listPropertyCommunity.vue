<template>
  <div class="list-property-community">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="小区编号">
          <el-input v-model="queryParams.communityId" placeholder="请输入小区编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="小区名称">
          <el-input v-model="queryParams.name" placeholder="请输入小区名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>小区信息</span>
        <div>
          <el-button type="primary" size="small" @click="handleAdd">加入小区</el-button>
          <el-button size="small" @click="$router.back()">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="communityId" label="小区ID" width="200" />
        <el-table-column prop="name" label="小区名称" min-width="200" />
        <el-table-column prop="cityName" label="城市编码" width="150" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">退出小区</el-button>
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
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

    <AddPropertyCommunity ref="addRef" :store-id="storeId" @success="fetchList" />
    <EditPropertyCommunity ref="editRef" :edit-data="currentRow" :store-id="storeId" @success="fetchList" />
    <DeletePropertyCommunity ref="delRef" :del-data="currentRow" :store-id="storeId" @success="fetchList" />
  </div>
</template>

<script>
import { listCommunitys } from '@/api/admin/propertyCommunityApi'
import AddPropertyCommunity from '@/components/admin/AddPropertyCommunity.vue'
import EditPropertyCommunity from '@/components/admin/EditPropertyCommunity.vue'
import DeletePropertyCommunity from '@/components/admin/DeletePropertyCommunity.vue'

export default {
  name: 'ListPropertyCommunity',
  components: { AddPropertyCommunity, EditPropertyCommunity, DeletePropertyCommunity },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      currentRow: null,
      storeId: '',
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        name: '',
        memberId: ''
      }
    }
  },
  mounted() {
    this.storeId = this.$route.query.storeId || ''
    this.queryParams.memberId = this.storeId
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listCommunitys(this.queryParams)
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
      this.$refs.addRef.open()
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.$refs.editRef.open()
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.$refs.delRef.open()
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
