<template>
  <div class="list-hardware-manufacturer-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="厂商ID">
          <el-input v-model="queryParams.hmId" placeholder="请输入厂商ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="厂商名称">
          <el-input v-model="queryParams.hmName" placeholder="请输入厂商名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="协议版本号">
          <el-input v-model="queryParams.version" placeholder="请输入协议版本号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>门禁厂家</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="hmId" label="厂商ID" min-width="150" />
        <el-table-column prop="hmName" label="厂商名称" min-width="150" />
        <el-table-column prop="version" label="厂商协议版本号" min-width="130" />
        <el-table-column prop="protocolImpl" label="协议实现" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="该协议开发者" min-width="120" />
        <el-table-column prop="link" label="开发者联系方式" min-width="130" />
        <el-table-column label="该协议许可" width="100" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.license" placement="top" trigger="hover" :content="row.license">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="prodUrl" label="产品官网" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleSpec(row)">厂家规格</el-button>
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
    <AddHardwareManufacturer :visible.sync="addVisible" @success="handleSuccess" />
    <EditHardwareManufacturer :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteHardwareManufacturer :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listHardwareManufacturers } from '@/api/accessControl/hardwareManufacturerApi'
import AddHardwareManufacturer from '@/components/accessControl/AddHardwareManufacturer.vue'
import EditHardwareManufacturer from '@/components/accessControl/EditHardwareManufacturer.vue'
import DeleteHardwareManufacturer from '@/components/accessControl/DeleteHardwareManufacturer.vue'

export default {
  name: 'HardwareManufacturerManage',
  components: { AddHardwareManufacturer, EditHardwareManufacturer, DeleteHardwareManufacturer },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        hmId: '',
        hmName: '',
        version: ''
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
        const res = await listHardwareManufacturers(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, hmId: '', hmName: '', version: '' }
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
    handleSpec(row) {
      this.$router.push(`/pages/accessControl/hardwareManufacturerAttrManage?hmId=${row.hmId}`)
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
