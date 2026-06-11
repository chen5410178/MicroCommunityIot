<template>
  <div class="list-mapping-manage">
    <el-row :gutter="16">
      <el-col :span="5">
        <el-card shadow="never" class="domain-card">
          <div slot="header" class="card-header">
            <span>配置项域</span>
          </div>
          <el-menu
            :default-active="curDomain.domain"
            class="domain-menu"
            @select="handleDomainSelect"
          >
            <el-menu-item index="">
              <span>全部配置</span>
            </el-menu-item>
            <el-menu-item
              v-for="item in domains"
              :key="item.domain"
              :index="item.domain"
            >
              {{ item.domainName }}({{ item.domain }})
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="19">
        <el-card v-if="curDomain.domain" shadow="never" class="info-card">
          <div slot="header" class="card-header">
            <span>{{ curDomain.domainName }}({{ curDomain.domain }})</span>
          </div>
          <div>{{ curDomain.remark }}</div>
        </el-card>
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="名称">
              <el-input v-model="queryParams.nameLike" placeholder="请输入名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item label="键">
              <el-input v-model="queryParams.key" placeholder="请输入键" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>编码映射</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="id" label="编码ID" width="100" />
            <el-table-column prop="domain" label="域" min-width="120" />
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="mappingKey" label="键" min-width="120" />
            <el-table-column prop="value" label="值" min-width="120" />
            <el-table-column label="操作" width="180" fixed="right" align="center">
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
          <div class="hint-text">温馨提示：改动后，需要到 缓存->刷新缓存 下刷新所有缓存（ALL）</div>
        </el-card>
      </el-col>
    </el-row>
    <AddMapping :visible.sync="addVisible" @success="handleSuccess" />
    <EditMapping :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMapping :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMappings, listMappingDomains } from '@/api/dev/devApi'
import AddMapping from '@/components/dev/AddMapping.vue'
import EditMapping from '@/components/dev/EditMapping.vue'
import DeleteMapping from '@/components/dev/DeleteMapping.vue'

export default {
  name: 'ListMappingManage',
  components: { AddMapping, EditMapping, DeleteMapping },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      domains: [],
      curDomain: {
        domain: '',
        domainName: '',
        remark: ''
      },
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        domain: '',
        nameLike: '',
        key: ''
      }
    }
  },
  mounted() {
    this.fetchDomains()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listMappings(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchDomains() {
      try {
        const res = await listMappingDomains({ page: 1, row: 300 })
        this.domains = res.data || []
      } catch { /* ignore */ }
    },
    handleDomainSelect(index) {
      if (!index) {
        this.curDomain = { domain: '', domainName: '', remark: '' }
        this.queryParams.domain = ''
      } else {
        const domain = this.domains.find(d => d.domain === index)
        if (domain) {
          this.curDomain = { ...domain }
          this.queryParams.domain = domain.domain
        }
      }
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
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
.domain-card { margin-bottom: 16px; }
.domain-menu { border-right: none; }
.domain-menu .el-menu-item { height: 40px; line-height: 40px; }
.search-card { margin-bottom: 16px; }
.info-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.hint-text { margin-top: 12px; color: #909399; font-size: 13px; }
</style>
