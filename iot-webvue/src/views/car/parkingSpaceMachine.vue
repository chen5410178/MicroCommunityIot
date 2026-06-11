<template>
  <div class="parking-space-machine-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="设备编码">
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="门禁厂家">
          <el-input v-model="queryParams.implBean" placeholder="请输入门禁厂家" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>车位地磁</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineId" label="编号" min-width="120" />
        <el-table-column prop="machineName" label="设备名称" min-width="150" />
        <el-table-column prop="machineCode" label="设备编码" min-width="150" />
        <el-table-column prop="machineIp" label="设备IP" min-width="140" />
        <el-table-column prop="machineMac" label="设备MAC" min-width="140" />
        <el-table-column prop="implBeanName" label="门禁厂家" min-width="150" show-overflow-tooltip />
        <el-table-column label="车位" min-width="200">
          <template slot-scope="{ row }">
            <div v-for="(item, index) in row.parkingSpaces" :key="index">{{ item.psName }}</div>
            <span v-if="!row.parkingSpaces || row.parkingSpaces.length === 0">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="stateName" label="状态" min-width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
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
    </el-card>
    <DeleteParkingSpaceMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listParkingSpaceMachines } from '@/api/car/carApi'
import DeleteParkingSpaceMachine from '@/components/car/DeleteParkingSpaceMachine.vue'

export default {
  name: 'ParkingSpaceMachine',
  components: { DeleteParkingSpaceMachine },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        implBean: ''
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingSpaceMachines({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', implBean: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/car/addParkingSpaceMachine')
    },
    handleEdit(row) {
      this.$router.push('/pages/car/editParkingSpaceMachine?machineId=' + row.machineId)
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
