<template>
  <div class="list-menu-manage">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="store-card">
          <div class="store-list">
            <div
              v-for="item in storeTypes"
              :key="item.value"
              class="store-item"
              :class="{ active: currentStoreType === item.value }"
              @click="handleStoreType(item.value)"
            >
              {{ item.label }}
            </div>
          </div>
        </el-card>
        <el-card shadow="never" class="group-card">
          <div class="group-list">
            <div
              v-for="item in menuGroups"
              :key="item.gId"
              class="group-item"
              :class="{ active: currentGId === item.gId }"
              @click="handleGroup(item.gId)"
            >
              {{ item.name }}
            </div>
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
              <el-input v-model="queryParams.name" placeholder="请输入菜单名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.isShow" placeholder="请选择显示状态" clearable style="width: 160px">
                <el-option label="显示" value="Y" />
                <el-option label="不显示" value="N" />
              </el-select>
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
            <el-button type="primary" size="small" @click="handleAdd">新增菜单</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
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
            <el-table-column label="描述" min-width="120">
              <template slot-scope="{ row }">{{ row.description || '-' }}</template>
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
      </div>
    </div>
    <AddMenu :visible.sync="addVisible" :g-id="currentGId" :store-type="currentStoreType" @success="handleSuccess" />
    <EditMenu :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMenu :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMenus, listMenuGroups } from '@/api/dev/devApi'
import AddMenu from '@/components/dev/AddMenu.vue'
import EditMenu from '@/components/dev/EditMenu.vue'
import DeleteMenu from '@/components/dev/DeleteMenu.vue'

export default {
  name: 'ListMenuManage',
  components: { AddMenu, EditMenu, DeleteMenu },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      currentStoreType: '800900000003',
      currentGId: '',
      menuGroups: [],
      storeTypes: [
        { value: '800900000003', label: '物业' },
        { value: '800900000001', label: '运营团队' },
        { value: '800900000000', label: '开发团队' }
      ],
      queryParams: {
        page: 1,
        row: 10,
        name: '',
        isShow: ''
      }
    }
  },
  mounted() {
    this.fetchMenuGroups()
  },
  methods: {
    async fetchMenuGroups() {
      try {
        const res = await listMenuGroups({ page: 1, row: 100, storeType: this.currentStoreType })
        this.menuGroups = res.data || []
        if (this.menuGroups.length > 0) {
          this.currentGId = this.menuGroups[0].gId
        } else {
          this.currentGId = ''
        }
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '获取菜单组失败')
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          gId: this.currentGId
        }
        const res = await listMenus(params)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleStoreType(val) {
      if (this.currentStoreType === val) return
      this.currentStoreType = val
      this.currentGId = ''
      this.fetchMenuGroups()
    },
    handleGroup(gId) {
      if (this.currentGId === gId) return
      this.currentGId = gId
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, name: '', isShow: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handlePrivilege(row) {
      this.$router.push('/pages/dev/basePrivilegeManage?mId=' + row.mId)
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
.page-container { display: flex; gap: 16px; }
.left-panel { width: 200px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.store-card { margin-bottom: 16px; }
.store-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.store-list { max-height: 200px; overflow-y: auto; }
.store-item { padding: 10px 12px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 13px; transition: all 0.3s; }
.store-item:hover { background-color: #f5f7fa; }
.store-item.active { background-color: #409eff; color: white; font-weight: 600; }
.group-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.group-list { max-height: calc(100vh - 420px); overflow-y: auto; }
.group-item { padding: 10px 12px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 13px; transition: all 0.3s; }
.group-item:hover { background-color: #f5f7fa; }
.group-item.active { background-color: #409eff; color: white; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
