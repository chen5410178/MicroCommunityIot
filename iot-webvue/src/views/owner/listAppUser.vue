<template>
  <div class="list-app-user">
    <div class="app-user-container">
      <div class="status-sidebar">
        <div class="sidebar-title">状态筛选</div>
        <div
          class="status-item"
          :class="{ active: activeState === '' }"
          @click="handleStateChange('')"
        >全部</div>
        <div
          class="status-item"
          :class="{ active: activeState === '10000' }"
          @click="handleStateChange('10000')"
        >待审核</div>
        <div
          class="status-item"
          :class="{ active: activeState === '12000' }"
          @click="handleStateChange('12000')"
        >审核成功</div>
        <div
          class="status-item"
          :class="{ active: activeState === '13000' }"
          @click="handleStateChange('13000')"
        >审核失败</div>
      </div>
      <div class="main-content">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.ownerName" placeholder="请选择人员名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.link" placeholder="请选择联系人手机号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.roomName" placeholder="请选择房屋名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>房屋认证</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="auId" label="编号" width="100" />
            <el-table-column prop="ownerName" label="人员名称" min-width="100" />
            <el-table-column prop="link" label="联系人" min-width="120" />
            <el-table-column prop="roomName" label="绑定房屋" min-width="150" />
            <el-table-column prop="openId" label="openId" min-width="120" />
            <el-table-column prop="createTime" label="申请时间" min-width="160" />
            <el-table-column label="状态类型" width="100" align="center">
              <template slot-scope="{ row }">
                <span v-if="row.state == '10000'" style="color: #e6a23c">待审核</span>
                <span v-else-if="row.state == '12000'" style="color: #67c23a">审核成功</span>
                <span v-else-if="row.state == '13000'" style="color: #f56c6c">审核失败</span>
                <span v-else>{{ row.state }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="stateMsg" label="审核说明" min-width="120" />
            <el-table-column prop="remark" label="备注" min-width="120" />
            <el-table-column label="操作" width="150" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button v-if="row.state == '10000'" size="small" type="text" @click="handleAudit(row)">审核</el-button>
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
      </div>
    </div>

    <AuthAppUser :visible.sync="authVisible" :app-user-data="currentRow" @success="handleSuccess" />
    <EditAppUser :visible.sync="editVisible" :app-user-data="currentRow" @success="handleSuccess" />
    <DeleteAppUser :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAppUsers } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'
import AuthAppUser from '@/components/owner/AuthAppUser.vue'
import EditAppUser from '@/components/owner/EditAppUser.vue'
import DeleteAppUser from '@/components/owner/DeleteAppUser.vue'

export default {
  name: 'ListAppUser',
  components: { AuthAppUser, EditAppUser, DeleteAppUser },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      activeState: '',
      authVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: { page: 1, row: 10, ownerName: '', link: '', roomName: '' }
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const params = {
          page: this.queryParams.page,
          row: this.queryParams.row,
          communityId: community ? community.communityId : '',
          ownerName: this.queryParams.ownerName,
          link: this.queryParams.link,
          roomName: this.queryParams.roomName
        }
        if (this.activeState) {
          params.state = this.activeState
        }
        const res = await listAppUsers(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally { this.loading = false }
    },
    handleStateChange(state) {
      this.activeState = state
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() { this.queryParams.page = 1; this.fetchList() },
    handleReset() {
      this.queryParams = { page: 1, row: 10, ownerName: '', link: '', roomName: '' }
      this.fetchList()
    },
    handleAudit(row) { this.currentRow = { ...row }; this.authVisible = true },
    handleDelete(row) { this.currentRow = { ...row }; this.delVisible = true },
    handleSuccess() { this.fetchList() },
    handleSizeChange(val) { this.queryParams.row = val; this.fetchList() },
    handlePageChange(val) { this.queryParams.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.app-user-container {
  display: flex;
  gap: 16px;
}
.status-sidebar {
  width: 160px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 4px;
  padding: 16px 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  align-self: flex-start;
}
.sidebar-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  padding: 0 16px 12px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;
}
.status-item {
  padding: 10px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.status-item:hover {
  background: #ecf5ff;
  color: #409eff;
}
.status-item.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: bold;
  border-right: 3px solid #409eff;
}
.main-content {
  flex: 1;
  min-width: 0;
}
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
