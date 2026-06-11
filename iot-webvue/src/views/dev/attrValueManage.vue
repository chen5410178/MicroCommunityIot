<template>
  <div class="list-attrvalue-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header"><span>查询条件</span></div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.value" placeholder="请输入值" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.valueShow" placeholder="请选择显示" clearable style="width: 160px">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.valueName" placeholder="请输入值名称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>属性值</span>
        <div>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          <el-button size="small" @click="handleBack">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="valueName" label="值名称" min-width="120" />
        <el-table-column prop="value" label="值" width="120" />
        <el-table-column prop="valueId" label="值ID" width="120" />
        <el-table-column label="显示" width="80">
          <template slot-scope="{ row }">{{ row.valueShow === 'Y' ? '是' : '否' }}</template>
        </el-table-column>
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
    <AddAttrValue :visible.sync="addVisible" :spec-id="specId" @success="handleSuccess" />
    <EditAttrValue :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteAttrValue :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAttrValues } from '@/api/dev/devApi'
import AddAttrValue from '@/components/dev/AddAttrValue.vue'
import EditAttrValue from '@/components/dev/EditAttrValue.vue'
import DeleteAttrValue from '@/components/dev/DeleteAttrValue.vue'

export default {
  name: 'ListAttrValueManage',
  components: { AddAttrValue, EditAttrValue, DeleteAttrValue },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      specId: '',
      specName: '',
      domain: '',
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: { page: 1, row: 10, value: '', valueShow: '', valueName: '', specId: '', domain: '' }
    }
  },
  mounted() {
    this.specId = this.$route.query.specId || ''
    this.specName = this.$route.query.specName || ''
    this.domain = this.$route.query.domain || ''
    this.queryParams.specId = this.specId
    this.queryParams.domain = this.domain
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAttrValues(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally { this.loading = false }
    },
    handleSearch() { this.queryParams.page = 1; this.fetchList() },
    handleReset() { this.queryParams = { page: 1, row: 10, value: '', valueShow: '', valueName: '', specId: this.specId, domain: this.domain }; this.fetchList() },
    handleAdd() { this.addVisible = true },
    handleEdit(row) { this.currentRow = { ...row }; this.editVisible = true },
    handleDelete(row) { this.currentRow = { ...row }; this.delVisible = true },
    handleBack() { this.$router.back() },
    handleSuccess() { this.fetchList() },
    handleSizeChange(val) { this.queryParams.row = val; this.fetchList() },
    handlePageChange(val) { this.queryParams.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
