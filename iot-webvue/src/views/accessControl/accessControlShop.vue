<template>
  <div class="list-access-control-shop">
    <div class="page-container">
      <div class="left-panel">
        <ShopLocationTree
          ref="shopLocationTreeRef"
          :show-all="false"
          :show-actions="false"
          @select="handleSelectLocation"
        />
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.machineName" placeholder="请输入门禁名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>商铺授权</span>
            <el-button type="primary" size="small" @click="handleAuthorize">授权门禁</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="locationName" label="位置名称" min-width="100" />
            <el-table-column prop="machineName" label="门禁名称" min-width="120" />
            <el-table-column prop="machineCode" label="门禁编号" min-width="120" />
            <el-table-column prop="personCount" label="人员数" width="80" align="center" />
            <el-table-column prop="createTime" label="授权时间" min-width="150" />
            <el-table-column label="操作" width="100" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">取消授权</el-button>
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
      </div>
    </div>
    <AuthorizeAccessControlShop
      ref="authorizeRef"
      :visible.sync="authorizeVisible"
      @success="fetchList"
    />
    <DeleteAccessControlShop
      ref="deleteRef"
      :visible.sync="deleteVisible"
      @success="fetchList"
    />
  </div>
</template>

<script>
import { listAccessControlShop } from '@/api/accessControl/accessControlApi'
import ShopLocationTree from '@/components/shop/ShopLocationTree.vue'
import AuthorizeAccessControlShop from '@/components/accessControl/AuthorizeAccessControlShop.vue'
import DeleteAccessControlShop from '@/components/accessControl/DeleteAccessControlShop.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListAccessControlShop',
  components: { ShopLocationTree, AuthorizeAccessControlShop, DeleteAccessControlShop },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      authorizeVisible: false,
      deleteVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        locationId: '',
        locationName: '',
        machineName: ''
      }
    }
  },
  mounted() {
    // 未选择位置时不查询，选择位置后再加载
  },
  methods: {
    async fetchList() {
      if (!this.queryParams.locationId) {
        this.list = []
        this.total = 0
        return
      }
      this.loading = true
      try {
        const community = getCurrentCommunity() || {}
        const res = await listAccessControlShop({
          ...this.queryParams,
          communityId: community.communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    handleSelectLocation({ locationId, locationName }) {
      this.queryParams.locationId = locationId || ''
      this.queryParams.locationName = locationName || ''
      this.queryParams.page = 1
      this.fetchList()
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
    handleAuthorize() {
      const locationId = this.queryParams.locationId
      if (!locationId) {
        this.$message.warning('请先选择位置')
        return
      }
      this.$refs.authorizeRef.open(locationId, this.queryParams.locationName)
    },
    handleDelete(row) {
      this.$refs.deleteRef.open(row)
    }
  }
}
</script>

<style scoped>
.page-container { display: flex; gap: 16px; }
.left-panel { width: 260px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
