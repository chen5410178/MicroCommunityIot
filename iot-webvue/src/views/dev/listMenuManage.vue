<template>
  <div class="list-menu-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="商户类型">
          <el-select v-model="queryParams.domain" placeholder="请选择商户类型" clearable style="width: 180px">
            <el-option label="运营团队" value="800900000001" />
            <el-option label="物业" value="800900000003" />
            <el-option label="开发团队" value="800900000000" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单组名称">
          <el-input v-model="queryParams.gName" placeholder="请选择菜单组名称" clearable style="width: 180px">
            <el-button slot="append" @click="openChooseMenuGroup">选择</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.mName" placeholder="请输入菜单名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="权限名称">
          <el-input v-model="queryParams.pName" placeholder="请输入权限名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>菜单信息</span>
        <el-button type="primary" size="small" @click="handleConfigMenu">配置菜单</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="gId" label="菜单组ID" width="180" />
        <el-table-column prop="gName" label="菜单组名称" min-width="120" />
        <el-table-column prop="mId" label="菜单ID" width="150" />
        <el-table-column prop="name" label="菜单名称" min-width="120" />
        <el-table-column label="归属商户" width="120" align="center">
          <template slot-scope="{ row }">{{ row.storeTypeName }}</template>
        </el-table-column>
        <el-table-column prop="url" label="菜单地址" min-width="150" />
        <el-table-column prop="seq" label="序列" width="80" />
        <el-table-column label="菜单显示" width="100" align="center">
          <template slot-scope="{ row }">{{ row.isShowName }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handlePrivilege(row)">权限</el-button>
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
    <ChooseMenuGroup :visible.sync="chooseVisible" @choose="handleChooseMenuGroup" />
    <EditMenu :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMenu :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMenus } from '@/api/dev/devApi'
import ChooseMenuGroup from '@/components/dev/ChooseMenuGroup.vue'
import EditMenu from '@/components/dev/EditMenu.vue'
import DeleteMenu from '@/components/dev/DeleteMenu.vue'

export default {
  name: 'ListMenuManage',
  components: { ChooseMenuGroup, EditMenu, DeleteMenu },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      chooseVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        domain: '',
        gName: '',
        gId: '',
        mName: '',
        pName: ''
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
        const res = await listMenus(this.queryParams)
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
    handleReset() {
      this.queryParams = { page: 1, row: 10, domain: '', gName: '', gId: '', mName: '', pName: '' }
      this.fetchList()
    },
    openChooseMenuGroup() {
      this.chooseVisible = true
    },
    handleChooseMenuGroup(row) {
      this.queryParams.gId = row.gId
      this.queryParams.gName = row.name
    },
    handleConfigMenu() {
      this.$router.push('/pages/dev/listConfigMenuManage')
    },
    handlePrivilege(row) {
      this.$router.push('/pages/dev/listBasePrivilegeManage?mId=' + row.mId)
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
