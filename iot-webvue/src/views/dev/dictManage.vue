<template>
  <div class="list-dict-manage">
    <el-row :gutter="16">
      <el-col :span="5">
        <el-card shadow="never" class="spec-card">
          <div slot="header" class="card-header">
            <span>字典类型</span>
            <el-button type="primary" size="small" @click="handleAddSpec">添加</el-button>
          </div>
          <el-menu
            :default-active="curSpec.specId"
            class="spec-menu"
            @select="handleSpecSelect"
          >
            <el-menu-item index="">
              <span>全部</span>
            </el-menu-item>
            <el-menu-item
              v-for="item in specs"
              :key="item.specId"
              :index="item.specId"
            >
              <span>{{ item.specName }}</span>
              <span class="spec-actions" @click.stop>
                <el-button type="text" size="mini" @click.stop="handleEditSpec(item)">修改</el-button>
                <el-button type="text" size="mini" style="color: #f56c6c" @click.stop="handleDeleteSpec(item)">删除</el-button>
              </span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="19">
        <el-card v-if="curSpec.specId" shadow="never" class="info-card">
          <div slot="header" class="card-header">
            <span>{{ curSpec.specName }}</span>
          </div>
          <div>表名：{{ curSpec.tableName || '-' }} | 字段：{{ curSpec.tableColumns || '-' }}</div>
        </el-card>
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header"><span>查询条件</span></div>
          <el-form :inline="true" :model="dictQuery">
            <el-form-item>
              <el-input v-model="dictQuery.name" placeholder="请输入名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="dictQuery.statusCd" placeholder="请输入值" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleDictSearch">搜索</el-button>
              <el-button @click="handleDictReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>字典信息</span>
            <el-button type="primary" size="small" @click="handleAddDict">添加</el-button>
          </div>
          <el-table :data="dictList" v-loading="dictLoading" border stripe>
            <el-table-column prop="specName" label="字典类型" min-width="120" />
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="statusCd" label="值" min-width="120" />
            <el-table-column prop="description" label="描述" min-width="200" />
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleEditDict(row)">修改</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDeleteDict(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            :current-page="dictQuery.page"
            :page-size="dictQuery.row"
            :total="dictTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleDictSizeChange"
            @current-change="handleDictPageChange"
            class="pagination-container"
          />
        </el-card>
      </el-col>
    </el-row>

    <AddDict :visible.sync="addDictVisible" :spec-list="specs" :default-spec-id="curSpec.specId" @success="handleDictSuccess" />
    <EditDict :visible.sync="editDictVisible" :edit-data="currentDictRow" @success="handleDictSuccess" />
    <DeleteDict :visible.sync="delDictVisible" :del-data="currentDictRow" @success="handleDictSuccess" />
    <AddDictSpec :visible.sync="addSpecVisible" @success="handleSpecSuccess" />
    <EditDictSpec :visible.sync="editSpecVisible" :edit-data="currentSpecRow" @success="handleSpecSuccess" />
    <DeleteDictSpec :visible.sync="delSpecVisible" :del-data="currentSpecRow" @success="handleSpecSuccess" />
  </div>
</template>

<script>
import { listDicts, listDictSpecs } from '@/api/dev/devApi'
import AddDict from '@/components/dev/AddDict.vue'
import EditDict from '@/components/dev/EditDict.vue'
import DeleteDict from '@/components/dev/DeleteDict.vue'
import AddDictSpec from '@/components/dev/AddDictSpec.vue'
import EditDictSpec from '@/components/dev/EditDictSpec.vue'
import DeleteDictSpec from '@/components/dev/DeleteDictSpec.vue'

export default {
  name: 'ListDictManage',
  components: { AddDict, EditDict, DeleteDict, AddDictSpec, EditDictSpec, DeleteDictSpec },
  data() {
    return {
      specs: [],
      curSpec: { specId: '', specName: '', tableName: '', tableColumns: '' },
      dictLoading: false,
      dictList: [],
      dictTotal: 0,
      dictQuery: { page: 1, row: 10, specId: '', name: '', statusCd: '' },
      addDictVisible: false,
      editDictVisible: false,
      delDictVisible: false,
      currentDictRow: null,
      addSpecVisible: false,
      editSpecVisible: false,
      delSpecVisible: false,
      currentSpecRow: null
    }
  },
  mounted() {
    this.loadSpecs()
    this.fetchDicts()
  },
  methods: {
    async loadSpecs() {
      try {
        const res = await listDictSpecs({ page: 1, row: 1000, statusCd: '0' })
        this.specs = res.data || []
      } catch { /* ignore */ }
    },
    handleSpecSelect(specId) {
      if (!specId) {
        this.curSpec = { specId: '', specName: '', tableName: '', tableColumns: '' }
      } else {
        const found = this.specs.find(s => s.specId === specId)
        this.curSpec = found ? { ...found } : { specId: specId, specName: '', tableName: '', tableColumns: '' }
      }
      this.dictQuery.specId = this.curSpec.specId
      this.dictQuery.page = 1
      this.fetchDicts()
    },
    async fetchDicts() {
      this.dictLoading = true
      try {
        const res = await listDicts(this.dictQuery)
        this.dictList = res.data || []
        this.dictTotal = res.total || 0
      } finally {
        this.dictLoading = false
      }
    },
    handleDictSearch() {
      this.dictQuery.page = 1
      this.fetchDicts()
    },
    handleDictReset() {
      this.dictQuery = { page: 1, row: 10, specId: this.curSpec.specId, name: '', statusCd: '' }
      this.fetchDicts()
    },
    handleAddDict() {
      this.addDictVisible = true
    },
    handleEditDict(row) {
      this.currentDictRow = { ...row }
      this.editDictVisible = true
    },
    handleDeleteDict(row) {
      this.currentDictRow = { ...row }
      this.delDictVisible = true
    },
    handleDictSuccess() {
      this.fetchDicts()
    },
    handleDictSizeChange(val) {
      this.dictQuery.row = val
      this.fetchDicts()
    },
    handleDictPageChange(val) {
      this.dictQuery.page = val
      this.fetchDicts()
    },
    handleAddSpec() {
      this.addSpecVisible = true
    },
    handleEditSpec(row) {
      this.currentSpecRow = { ...row }
      this.editSpecVisible = true
    },
    handleDeleteSpec(row) {
      this.currentSpecRow = { ...row }
      this.delSpecVisible = true
    },
    handleSpecSuccess() {
      this.loadSpecs()
    }
  }
}
</script>

<style scoped>
.spec-card { margin-bottom: 16px; }
.spec-menu { border-right: none; }
.spec-menu .el-menu-item { height: 40px; line-height: 40px; }
.spec-actions {
  float: right;
  margin-right: 8px;
  display: none;
}
.spec-menu .el-menu-item:hover .spec-actions { display: inline-block; }
.search-card { margin-bottom: 16px; }
.info-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
