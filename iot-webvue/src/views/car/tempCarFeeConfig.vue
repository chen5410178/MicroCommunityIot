<template>
  <div class="temp-car-fee-config-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.feeName" placeholder="请输入标准名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.areaNum" placeholder="请输入停车场编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.carType" placeholder="请选择车辆类型" clearable style="width: 180px">
            <el-option v-for="item in carTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>收费标准</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="configId" label="标准ID" min-width="120" />
        <el-table-column prop="feeName" label="标准名称" min-width="140" />
        <el-table-column prop="areaNum" label="停车场" min-width="120" />
        <el-table-column prop="carTypeName" label="车辆类型" min-width="100" align="center" />
        <el-table-column prop="ruleName" label="收费规则" min-width="140" />
        <el-table-column prop="startTime" label="开始时间" min-width="120" align="center" />
        <el-table-column prop="endTime" label="结束时间" min-width="120" align="center" />
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
    <AddTempCarFeeConfig :visible.sync="addVisible" @success="handleSuccess" />
    <EditTempCarFeeConfig :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteTempCarFeeConfig :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listTempCarFeeConfigs } from '@/api/car/carApi'
import { listDicts } from '@/api/dev/devApi'
import AddTempCarFeeConfig from '@/components/car/AddTempCarFeeConfig.vue'
import EditTempCarFeeConfig from '@/components/car/EditTempCarFeeConfig.vue'
import DeleteTempCarFeeConfig from '@/components/car/DeleteTempCarFeeConfig.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'TempCarFeeConfig',
  components: { AddTempCarFeeConfig, EditTempCarFeeConfig, DeleteTempCarFeeConfig },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      carTypes: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        feeName: '',
        areaNum: '',
        carType: ''
      }
    }
  },
  mounted() {
    this.fetchCarTypes()
    this.fetchList()
  },
  methods: {
    async fetchCarTypes() {
      try {
        const res = await listDicts({ page: 1, row: 100, tableName: 'temp_car_fee_config', columnName: 'car_type' })
        this.carTypes = res.data || []
      } catch (e) {
        console.error('加载车辆类型字典失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listTempCarFeeConfigs({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, feeName: '', areaNum: '', carType: '' }
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
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
