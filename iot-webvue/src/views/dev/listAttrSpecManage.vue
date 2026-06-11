<template>
  <div class="list-attrspec-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
        <el-button type="text" @click="showMore = !showMore">{{ showMore ? '隐藏' : '更多' }}</el-button>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="规格名称">
          <el-input v-model="queryParams.specName" placeholder="请输入规格名称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="属性表">
          <el-select v-model="queryParams.tableName" placeholder="请选择属性表" clearable style="width: 160px">
            <el-option label="房屋属性" value="building_room_attr" />
            <el-option label="业主属性" value="building_owner_attr" />
            <el-option label="小区属性" value="building_community_attr" />
            <el-option label="设备属性" value="machine_attr" />
            <el-option label="停车场属性" value="parking_area_attr" />
            <el-option label="车辆属性" value="owner_car_attr" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格编码">
          <el-input v-model="queryParams.specCd" placeholder="请输入规格编码" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item v-if="showMore" label="域">
          <el-input v-model="queryParams.domain" placeholder="请输入域" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>属性配置</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="domain" label="域" width="120" />
        <el-table-column prop="specName" label="规格名称" min-width="120" />
        <el-table-column prop="specCd" label="规格" width="120" />
        <el-table-column label="属性表" width="120">
          <template slot-scope="{ row }">{{ tableNameMap[row.tableName] || row.tableName }}</template>
        </el-table-column>
        <el-table-column label="必填" width="70">
          <template slot-scope="{ row }">{{ row.required === 'Y' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="展示" width="70">
          <template slot-scope="{ row }">{{ row.specShow === 'Y' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="值类型" width="100">
          <template slot-scope="{ row }">{{ specValueTypeMap[row.specValueType] || row.specValueType }}</template>
        </el-table-column>
        <el-table-column label="规格类型" width="100">
          <template slot-scope="{ row }">{{ specTypeMap[row.specType] || row.specType }}</template>
        </el-table-column>
        <el-table-column label="查询显示" width="90">
          <template slot-scope="{ row }">{{ row.listShow === 'Y' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleAttrValue(row)">离散值</el-button>
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
    <AddAttrSpec :visible.sync="addVisible" @success="handleSuccess" />
    <EditAttrSpec :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteAttrSpec :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAttrSpecs } from '@/api/dev/devApi'
import AddAttrSpec from '@/components/dev/AddAttrSpec.vue'
import EditAttrSpec from '@/components/dev/EditAttrSpec.vue'
import DeleteAttrSpec from '@/components/dev/DeleteAttrSpec.vue'

export default {
  name: 'ListAttrSpecManage',
  components: { AddAttrSpec, EditAttrSpec, DeleteAttrSpec },
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
      specValueTypeMap: { '1001': '字符串', '2002': '整数', '3003': '金额', '4004': '日期', '5005': '时间' },
      specTypeMap: { '2233': 'input', '3344': 'select', '4455': '日期' },
      tableNameMap: {
        'building_room_attr': '房屋属性',
        'building_owner_attr': '业主属性',
        'building_community_attr': '小区属性',
        'machine_attr': '设备属性',
        'parking_area_attr': '停车场属性',
        'owner_car_attr': '车辆属性'
      },
      queryParams: { page: 1, row: 10, specName: '', tableName: '', specCd: '', domain: '' }
    }
  },
  mounted() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAttrSpecs(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally { this.loading = false }
    },
    handleSearch() { this.queryParams.page = 1; this.fetchList() },
    handleReset() { this.queryParams = { page: 1, row: 10, specName: '', tableName: '', specCd: '', domain: '' }; this.fetchList() },
    handleAdd() { this.addVisible = true },
    handleEdit(row) { this.currentRow = { ...row }; this.editVisible = true },
    handleDelete(row) { this.currentRow = { ...row }; this.delVisible = true },
    handleAttrValue(row) { this.$router.push('/pages/dev/listAttrValueManage?specId=' + row.specId + '&specName=' + row.specName + '&domain=' + row.domain) },
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
