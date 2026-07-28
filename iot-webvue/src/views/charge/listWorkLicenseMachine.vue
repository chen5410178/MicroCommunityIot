<template>
  <div class="list-work-license-machine">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请选择名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请选择工牌编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.staffName" placeholder="请选择员工名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>员工工牌</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineName" label="名称" min-width="120" />
        <el-table-column prop="machineCode" label="工牌编号" min-width="120" />
        <el-table-column prop="staffName" label="员工" min-width="100" />
        <el-table-column prop="posUploadSec" label="上报间隔" width="90" />
        <el-table-column prop="lat" label="经度" width="100" />
        <el-table-column prop="lon" label="纬度" width="100" />
        <el-table-column prop="address" label="位置" min-width="150" />
        <el-table-column prop="factoryName" label="工牌厂家" min-width="120" />
        <el-table-column label="版本" width="80">
          <template slot-scope="{ row }">
            {{ row.version === 'GBK' ? '2.0' : '1.0' }}
          </template>
        </el-table-column>
        <el-table-column prop="stateName" label="状态" width="80" />
        <el-table-column prop="heartbeatTime" label="心跳时间" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handlePlay(row)">播放</el-button>
            <el-button size="small" type="text" @click="handleWhiteLine(row)">通话白名单</el-button>
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
    <WorklicensePps :visible.sync="ppsVisible" :machine-data="currentRow" @success="handleSuccess" />
    <WorklicenseWhiteLine :visible.sync="whiteLineVisible" :machine-data="currentRow" @success="handleSuccess" />
    <DeleteWorkLicenseMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listWorkLicenseMachines } from '@/api/charge/chargeApi'
import WorklicensePps from '@/components/charge/WorklicensePps.vue'
import WorklicenseWhiteLine from '@/components/charge/WorklicenseWhiteLine.vue'
import DeleteWorkLicenseMachine from '@/components/charge/DeleteWorkLicenseMachine.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListWorkLicenseMachine',
  components: { WorklicensePps, WorklicenseWhiteLine, DeleteWorkLicenseMachine },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      ppsVisible: false,
      whiteLineVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        staffName: ''
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listWorkLicenseMachines({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, machineName: '', machineCode: '', staffName: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push({ path: '/pages/charge/addWorkLicenseMachine' })
    },
    handleEdit(row) {
      this.$router.push({ path: '/pages/charge/editWorkLicenseMachine', query: { machineId: row.machineId } })
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handlePlay(row) {
      this.currentRow = { ...row }
      this.ppsVisible = true
    },
    handleWhiteLine(row) {
      this.currentRow = { ...row }
      this.whiteLineVisible = true
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
