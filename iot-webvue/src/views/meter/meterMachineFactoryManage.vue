<template>
  <div class="list-meter-machine-factory-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.factoryId" placeholder="请输入厂家ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.factoryName" placeholder="请输入厂家名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.beanImpl" placeholder="请输入厂家处理类" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.machineModel" placeholder="请选择" clearable style="width: 150px">
            <el-option label="充值模式" value="1001" />
            <el-option label="抄表模式" value="2002" />
            <el-option label="都支持" value="3003" />
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
        <span>水电厂家</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="factoryId" label="厂家ID" min-width="150" />
        <el-table-column prop="factoryName" label="厂家名称" min-width="150" />
        <el-table-column prop="beanImpl" label="厂家处理类" min-width="200" show-overflow-tooltip />
        <el-table-column label="模式" width="120" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.machineModel === '1001'">充值模式</span>
            <span v-else-if="row.machineModel === '2002'">抄表模式</span>
            <span v-else-if="row.machineModel === '3003'">都支持</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" width="100" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
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
    <AddMeterMachineFactory :visible.sync="addVisible" @success="handleSuccess" />
    <EditMeterMachineFactory :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMeterMachineFactory :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMeterMachineFactorys } from '@/api/meter/meterApi'
import AddMeterMachineFactory from '@/components/meter/AddMeterMachineFactory.vue'
import EditMeterMachineFactory from '@/components/meter/EditMeterMachineFactory.vue'
import DeleteMeterMachineFactory from '@/components/meter/DeleteMeterMachineFactory.vue'

export default {
  name: 'MeterMachineFactoryManage',
  components: { AddMeterMachineFactory, EditMeterMachineFactory, DeleteMeterMachineFactory },
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
        factoryId: '',
        factoryName: '',
        beanImpl: '',
        machineModel: ''
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
        const res = await listMeterMachineFactorys(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, factoryId: '', factoryName: '', beanImpl: '', machineModel: '' }
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
      this.$router.push(`/pages/meter/meterMachineFactorySpecManage?factoryId=${row.factoryId}`)
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
