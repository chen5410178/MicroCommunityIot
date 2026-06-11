<template>
  <div class="list-access-control-floor">
    <div class="page-container">
      <div class="left-panel">
        <FloorUnitTree
          ref="floorUnitTreeRef"
          @switchFloor="handleSwitchFloor"
          @switchUnit="handleSwitchUnit"
        />
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="门禁名称">
              <el-input v-model="queryParams.machineName" placeholder="请输入门禁名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>楼栋授权</span>
            <el-button type="primary" size="small" @click="handleAuthorize">授权门禁</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="floorNum" label="楼栋" min-width="100" />
            <el-table-column prop="unitNum" label="单元" min-width="100" />
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
    <AuthorizeAccessControlFloor
      ref="authorizeRef"
      :visible.sync="authorizeVisible"
      @success="fetchList"
    />
    <DeleteAccessControlFloor
      ref="deleteRef"
      :visible.sync="deleteVisible"
      @success="fetchList"
    />
  </div>
</template>

<script>
import { listAdminAccessControlFloor } from '@/api/accessControl/accessControlApi'
import FloorUnitTree from '@/components/property/FloorUnitTree.vue'
import AuthorizeAccessControlFloor from '@/components/accessControl/AuthorizeAccessControlFloor.vue'
import DeleteAccessControlFloor from '@/components/accessControl/DeleteAccessControlFloor.vue'

export default {
  name: 'ListAccessControlFloor',
  components: { FloorUnitTree, AuthorizeAccessControlFloor, DeleteAccessControlFloor },
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
        floorId: '',
        unitId: '',
        machineName: ''
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
        const community = this.$store.state.app.currentCommunity || {}
        const res = await listAdminAccessControlFloor({
          ...this.queryParams,
          communityId: community.communityId
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSwitchFloor({ floorId }) {
      this.queryParams.floorId = floorId
      this.queryParams.unitId = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSwitchUnit({ unitId }) {
      this.queryParams.unitId = unitId
      this.queryParams.floorId = ''
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
      this.$refs.authorizeRef.open(this.queryParams.floorId, this.queryParams.unitId)
    },
    handleDelete(row) {
      this.$refs.deleteRef.open(row)
    }
  }
}
</script>

<style scoped>
.page-container { display: flex; gap: 16px; }
.right-panel { flex: 1; min-width: 0; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
