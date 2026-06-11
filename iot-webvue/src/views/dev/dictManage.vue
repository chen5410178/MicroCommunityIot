<template>
  <div class="list-dict-manage">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="字典" name="dict">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="dictQuery">
            <el-form-item>
              <el-select v-model="dictQuery.specId" placeholder="请选择类型" clearable style="width: 180px">
                <el-option
                  v-for="item in specList"
                  :key="item.specId"
                  :label="item.specName"
                  :value="item.specId"
                />
              </el-select>
            </el-form-item>
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
            <el-table-column label="操作" width="150" fixed="right" align="center">
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
      </el-tab-pane>

      <el-tab-pane label="字典类型" name="dictSpec">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="specQuery">
            <el-form-item>
              <el-input v-model="specQuery.specName" placeholder="请输入类型名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSpecSearch">搜索</el-button>
              <el-button @click="handleSpecReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>字典类型</span>
            <el-button type="primary" size="small" @click="handleAddSpec">添加</el-button>
          </div>
          <el-table :data="specListPage" v-loading="specLoading" border stripe>
            <el-table-column prop="specId" label="类型ID" width="200" />
            <el-table-column prop="specName" label="类型名称" min-width="200" />
            <el-table-column label="操作" width="150" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleEditSpec(row)">修改</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDeleteSpec(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            :current-page="specQuery.page"
            :page-size="specQuery.row"
            :total="specTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSpecSizeChange"
            @current-change="handleSpecPageChange"
            class="pagination-container"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <AddDict :visible.sync="addDictVisible" @success="handleDictSuccess" />
    <EditDict :visible.sync="editDictVisible" :edit-data="currentDictRow" @success="handleDictSuccess" />
    <DeleteDict :visible.sync="delDictVisible" :del-data="currentDictRow" @success="handleDictSuccess" />
    <AddDictSpec :visible.sync="addSpecVisible" @success="handleSpecSuccess" />
    <EditDictSpec :visible.sync="editSpecVisible" :edit-data="currentSpecRow" @success="handleSpecSuccess" />
    <DeleteDictSpec :visible.sync="delSpecVisible" :del-data="currentSpecRow" @success="handleSpecSuccess" />
  </div>
</template>

<script>
import { listDicts } from '@/api/dev/devApi'
import { listDictSpecs } from '@/api/dev/devApi'
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
      activeTab: 'dict',
      specList: [],
      // Dict
      dictLoading: false,
      dictList: [],
      dictTotal: 0,
      dictQuery: { page: 1, row: 10, specId: '', name: '', statusCd: '' },
      addDictVisible: false,
      editDictVisible: false,
      delDictVisible: false,
      currentDictRow: null,
      // DictSpec
      specLoading: false,
      specListPage: [],
      specTotal: 0,
      specQuery: { page: 1, row: 10, specName: '' },
      addSpecVisible: false,
      editSpecVisible: false,
      delSpecVisible: false,
      currentSpecRow: null
    }
  },
  mounted() {
    const tab = this.$route.query.tab
    if (tab === '字典类型') {
      this.activeTab = 'dictSpec'
    }
    this.loadSpecs()
    if (this.activeTab === 'dict') {
      this.fetchDicts()
    } else {
      this.fetchDictSpecs()
    }
  },
  methods: {
    async loadSpecs() {
      try {
        const res = await listDictSpecs({ page: 1, row: 1000 })
        this.specList = res.data || []
      } catch { /* ignore */ }
    },
    handleTabClick() {
      const tabMap = { dict: '字典', dictSpec: '字典类型' }
      this.$router.replace({ query: { ...this.$route.query, tab: tabMap[this.activeTab] } })
      if (this.activeTab === 'dict') {
        this.fetchDicts()
      } else {
        this.fetchDictSpecs()
      }
    },
    // Dict methods
    async fetchDicts() {
      this.dictLoading = true
      try {
        const res = await listDicts(this.dictQuery)
        this.dictList = res.data || []
        this.dictTotal = res.records || 0
      } finally {
        this.dictLoading = false
      }
    },
    handleDictSearch() {
      this.dictQuery.page = 1
      this.fetchDicts()
    },
    handleDictReset() {
      this.dictQuery = { page: 1, row: 10, specId: '', name: '', statusCd: '' }
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
      this.loadSpecs()
    },
    handleDictSizeChange(val) {
      this.dictQuery.row = val
      this.fetchDicts()
    },
    handleDictPageChange(val) {
      this.dictQuery.page = val
      this.fetchDicts()
    },
    // DictSpec methods
    async fetchDictSpecs() {
      this.specLoading = true
      try {
        const res = await listDictSpecs(this.specQuery)
        this.specListPage = res.data || []
        this.specTotal = res.records || 0
      } finally {
        this.specLoading = false
      }
    },
    handleSpecSearch() {
      this.specQuery.page = 1
      this.fetchDictSpecs()
    },
    handleSpecReset() {
      this.specQuery = { page: 1, row: 10, specName: '' }
      this.fetchDictSpecs()
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
      this.fetchDictSpecs()
      this.loadSpecs()
    },
    handleSpecSizeChange(val) {
      this.specQuery.row = val
      this.fetchDictSpecs()
    },
    handleSpecPageChange(val) {
      this.specQuery.page = val
      this.fetchDictSpecs()
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
