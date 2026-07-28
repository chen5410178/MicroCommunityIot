<template>
  <div class="instrument-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入仪表编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入仪表名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.typeId" placeholder="请选择仪表类型" clearable style="width: 180px">
            <el-option v-for="item in instrumentTypeList" :key="item.typeId" :label="item.name" :value="item.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.implBean" placeholder="请选择仪表厂家" clearable style="width: 180px">
            <el-option v-for="item in factoryList" :key="item.factoryId" :label="item.factoryName" :value="item.factoryId" />
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
        <span>仪表设备</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineCode" label="仪表编码" min-width="120" />
        <el-table-column prop="machineName" label="仪表名称" min-width="120" />
        <el-table-column prop="typeName" label="仪表类型" width="100" />
        <el-table-column label="剩余电量" width="100" align="center">
          <template slot-scope="{ row }">{{ row.charge }}%</template>
        </el-table-column>
        <el-table-column label="信号强度" width="100" align="center">
          <template slot-scope="{ row }">{{ row.sign }}dbm</template>
        </el-table-column>
        <el-table-column prop="factoryName" label="仪表厂家" min-width="120" />
        <el-table-column label="上报时间" width="100" align="center">
          <template slot-scope="{ row }">{{ row.upMin }}min</template>
        </el-table-column>
        <el-table-column label="采样时间" width="100" align="center">
          <template slot-scope="{ row }">{{ row.checkSec }}sec</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
    <DeleteInstrument :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listInstruments } from '@/api/meter/meterApi'
import { listInstrumentFactory } from '@/api/meter/meterApi'
import { listInstrumentTypes } from '@/api/meter/meterApi'
import DeleteInstrument from '@/components/meter/DeleteInstrument.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'InstrumentManage',
  components: { DeleteInstrument },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      instrumentTypeList: [],
      factoryList: [],
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        typeId: '',
        implBean: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.fetchInstrumentTypes()
    this.fetchFactoryList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listInstruments({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    async fetchInstrumentTypes() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listInstrumentTypes({ page: -1, row: 500, communityId })
        this.instrumentTypeList = res.data || []
      } catch (e) {
        console.log('获取仪表类型失败')
      }
    },
    async fetchFactoryList() {
      try {
        const res = await listInstrumentFactory({ page: -1, row: 500 })
        this.factoryList = res.data || []
      } catch (e) {
        console.log('获取厂家列表失败')
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', typeId: '', implBean: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/meter/addInstrument')
    },
    handleEdit(row) {
      this.$router.push({ path: '/pages/meter/editInstrument', query: { machineId: row.machineId } })
    },
    handleDetail(row) {
      this.$router.push({ path: '/pages/meter/instrumentDetail', query: { machineId: row.machineId } })
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
