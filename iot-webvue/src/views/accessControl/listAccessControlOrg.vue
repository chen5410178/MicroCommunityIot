<template>
  <div class="list-access-control-org">
    <div class="page-container">
      <div class="left-panel">
        <OrgTree
          ref="orgTreeRef"
          @switchOrg="handleSwitchOrg"
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
            <span>部门授权</span>
            <el-button type="primary" size="small" @click="handleAuthorize">授权门禁</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="orgName" label="组织" min-width="120" />
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
    <AuthorizeAccessControlOrg
      ref="authorizeRef"
      :visible.sync="authorizeVisible"
      @success="fetchList"
    />
    <DeleteAccessControlOrg
      ref="deleteRef"
      :visible.sync="deleteVisible"
      @success="fetchList"
    />
  </div>
</template>

<script>
import { listAccessControlOrg } from '@/api/accessControl/accessControlApi'
import OrgTree from '@/components/accessControl/OrgTree.vue'
import AuthorizeAccessControlOrg from '@/components/accessControl/AuthorizeAccessControlOrg.vue'
import DeleteAccessControlOrg from '@/components/accessControl/DeleteAccessControlOrg.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListAccessControlOrg',
  components: { OrgTree, AuthorizeAccessControlOrg, DeleteAccessControlOrg },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      authorizeVisible: false,
      deleteVisible: false,
      currentRow: null,
      hasSelectedOrg: false,
      queryParams: {
        page: 1,
        row: 10,
        orgId: '',
        machineName: ''
      }
    }
  },
  methods: {
    async fetchList() {
      if (!this.queryParams.orgId) return
      this.loading = true
      try {
        const community = getCurrentCommunity() || {}
        const res = await listAccessControlOrg({
          ...this.queryParams,
          communityId: community.communityId
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSwitchOrg({ orgId, orgName }) {
      this.hasSelectedOrg = true
      this.queryParams.orgId = orgId
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
      if (!this.queryParams.orgId) {
        this.$message.warning('请先选择组织')
        return
      }
      this.$refs.authorizeRef.open(this.queryParams.orgId)
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
