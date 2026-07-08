<template>
  <div class="list-dt-modal-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="tree-card">
          <ul class="type-list">
            <li
              v-for="item in modalTypes"
              :key="item.statusCd"
              :class="{ active: queryParams.modalType === item.statusCd }"
              @click="switchModalType(item.statusCd)"
            >
              {{ item.name }}
            </li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.modalName" placeholder="请输入模型名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>3D模型</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="modalId" label="编号" width="150" />
            <el-table-column prop="modalName" label="模型名称" min-width="150" />
            <el-table-column prop="modalCode" label="模型编码" min-width="120" />
            <el-table-column prop="modalTypeName" label="模型类型" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="150" fixed="right" align="center">
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
      </el-col>
    </el-row>
    <AddDtModal :visible.sync="addVisible" :default-modal-type="queryParams.modalType" @success="handleSuccess" />
    <EditDtModal :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteDtModal :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listDtModals } from '@/api/3d/dtModalApi'
import { listDicts } from '@/api/dev/devApi'
import AddDtModal from '@/components/3d/AddDtModal.vue'
import EditDtModal from '@/components/3d/EditDtModal.vue'
import DeleteDtModal from '@/components/3d/DeleteDtModal.vue'

export default {
  name: 'ListDtModalManage',
  components: { AddDtModal, EditDtModal, DeleteDtModal },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      modalTypes: [{ name: '全部类型', statusCd: '' }],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        modalName: '',
        modalType: ''
      }
    }
  },
  mounted() {
    this.loadModalTypes()
    this.fetchList()
  },
  methods: {
    async loadModalTypes() {
      try {
        const res = await listDicts({ tableName: 'dt_modal', columnName: 'modal_type' })
        const dicts = res.data || []
        this.modalTypes = [{ name: '全部类型', statusCd: '' }, ...dicts]
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listDtModals(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    switchModalType(type) {
      this.queryParams.modalType = type
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, modalName: '', modalType: '' }
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.tree-card { min-height: 400px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.type-list { list-style: none; padding: 0; margin: 0; }
.type-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 4px;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.type-list li:hover { background-color: #f5f7fa; }
.type-list li.active { background-color: #409eff; color: white; font-weight: 600; }
</style>
