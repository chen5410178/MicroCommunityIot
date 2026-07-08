<template>
  <div class="list-menu-group-manage">
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
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
            <el-button type="text" @click="showMore = !showMore">{{ showMore ? '隐藏' : '更多' }}</el-button>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.gId" placeholder="请输入菜单组ID" clearable/>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.name" placeholder="请输入菜单组名称" clearable />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.icon" placeholder="请输入图标" clearable  />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
            <template v-if="showMore">
              <el-form-item>
                <el-select v-model="queryParams.label" placeholder="请选择标签" clearable>
                  <el-option label="HOT" value="HOT" />
                  <el-option label="TOP" value="TOP" />
                </el-select>
              </el-form-item>
            </template>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>菜单组信息</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="gId" label="菜单组Id"  />
            <el-table-column prop="name" label="菜单组名称"  />
            <el-table-column label="菜单组类型" width="120" align="center">
              <template slot-scope="{ row }">{{ row.groupType === 'P_WEB' ? 'web菜单' : 'App菜单' }}</template>
            </el-table-column>
            <el-table-column prop="icon" label="图标" />
            <el-table-column prop="label" label="标签"  />
            <el-table-column prop="seq" label="序列"  />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template slot-scope="{ row }">
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
    <AddMenuGroup :visible.sync="addVisible" :store-type="currentStoreType" @success="handleSuccess" />
    <EditMenuGroup :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMenuGroup :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMenuGroups } from '@/api/dev/devApi'
import AddMenuGroup from '@/components/dev/AddMenuGroup.vue'
import EditMenuGroup from '@/components/dev/EditMenuGroup.vue'
import DeleteMenuGroup from '@/components/dev/DeleteMenuGroup.vue'

export default {
  name: 'ListMenuGroupManage',
  components: { AddMenuGroup, EditMenuGroup, DeleteMenuGroup },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showMore: false,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      currentStoreType: '800900000003',
      storeTypes: [
        { value: '800900000003', label: '物业' },
        { value: '800900000001', label: '运营团队' },
        { value: '800900000000', label: '开发团队' }
      ],
      queryParams: {
        page: 1,
        row: 10,
        gId: '',
        name: '',
        icon: '',
        label: ''
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
        const res = await listMenuGroups({ ...this.queryParams, storeType: this.currentStoreType })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleStoreType(val) {
      if (this.currentStoreType === val) return
      this.currentStoreType = val
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, gId: '', name: '', icon: '', label: '' }
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
.store-card { margin-bottom: 0; }
.store-list { max-height: 200px; overflow-y: auto; }
.store-item { padding: 10px 12px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 13px; transition: all 0.3s; }
.store-item:hover { background-color: #f5f7fa; }
.store-item.active { background-color: #409eff; color: white; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
