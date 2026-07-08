<template>
  <div class="org-manage">
    <div class="page-container">
      <div class="left-panel">
        <OrgTree ref="orgTreeRef" @switchOrg="handleSwitchOrg" />
        <el-card shadow="never" class="tree-action-card">
          <div class="tree-actions">
            <el-button type="primary" size="small" @click="handleAddOrg">添加</el-button>
            <el-button type="warning" size="small" @click="handleEditOrg" :disabled="!currentOrg">修改</el-button>
            <el-button type="danger" size="small" @click="handleDeleteOrgClick" :disabled="!currentOrg">删除</el-button>
          </div>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.staffName" placeholder="请输入员工名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>员工列表</span>
            <el-button type="primary" size="small" @click="handleRelStaff" :disabled="!currentOrg">关联员工</el-button>
          </div>
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
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDeleteRelStaff(row)">删除</el-button>
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
    <AddOrg :visible.sync="addVisible" :parent-org-data="currentOrg" @success="handleOrgSuccess" />
    <EditOrg :visible.sync="editVisible" :edit-data="currentOrg" @success="handleOrgSuccess" />
    <DeleteOrg :visible.sync="deleteVisible" :del-data="currentOrg" @success="handleOrgSuccess" />
    <OrgRelStaff :visible.sync="relStaffVisible" :org-id="currentOrg ? currentOrg.orgId || currentOrg.id : ''" @success="fetchList" />
    <DeleteOrgRelStaff :visible.sync="deleteRelStaffVisible" :del-data="currentRelStaff" @success="fetchList" />
  </div>
</template>

<script>
import { listStaffs } from '@/api/frame/orgApi'
import OrgTree from '@/components/accessControl/OrgTree.vue'
import AddOrg from '@/components/frame/AddOrg.vue'
import EditOrg from '@/components/frame/EditOrg.vue'
import DeleteOrg from '@/components/frame/DeleteOrg.vue'
import OrgRelStaff from '@/components/frame/OrgRelStaff.vue'
import DeleteOrgRelStaff from '@/components/frame/DeleteOrgRelStaff.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OrgManage',
  components: { OrgTree, AddOrg, EditOrg, DeleteOrg, OrgRelStaff, DeleteOrgRelStaff },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      currentOrg: null,
      currentRelStaff: null,
      addVisible: false,
      editVisible: false,
      deleteVisible: false,
      relStaffVisible: false,
      deleteRelStaffVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        orgId: '',
        staffName: ''
      }
    }
  },
  methods: {
    async fetchList() {
      if (!this.queryParams.orgId) return
      this.loading = true
      try {
        const community = getCurrentCommunity() || {}
        const res = await listStaffs({
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
      this.currentOrg = { orgId: orgId, orgName: orgName, id: orgId }
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
    handleAddOrg() {
      this.addVisible = true
    },
    handleEditOrg() {
      if (!this.currentOrg) {
        this.$message.warning('请先选择组织')
        return
      }
      this.editVisible = true
    },
    handleDeleteOrgClick() {
      if (!this.currentOrg) {
        this.$message.warning('请先选择组织')
        return
      }
      this.deleteVisible = true
    },
    handleRelStaff() {
      if (!this.currentOrg) {
        this.$message.warning('请先选择组织')
        return
      }
      this.relStaffVisible = true
    },
    handleDetail(row) {
      this.$router.push({ path: '/pages/frame/staffDetail', query: { staffId: row.userId || row.staffId || row.id } })
    },
    handleDeleteRelStaff(row) {
      this.currentRelStaff = row
      this.deleteRelStaffVisible = true
    },
    handleOrgSuccess() {
      this.$refs.orgTreeRef && this.$refs.orgTreeRef.refresh()
      this.queryParams.page = 1
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.page-container { display: flex; gap: 16px; }
.left-panel { width: 240px; min-width: 240px; }
.right-panel { flex: 1; min-width: 0; }
.tree-action-card { margin-top: 12px; }
.tree-actions { display: flex; justify-content: space-between; gap: 8px; }
.tree-actions .el-button { flex: 1; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
