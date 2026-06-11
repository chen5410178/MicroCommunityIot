<template>
  <div class="list-intercom-log">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="设备号码">
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备号码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 160px">
            <el-option label="全部" value="" />
            <el-option label="呼叫中" value="W" />
            <el-option label="通话中" value="D" />
            <el-option label="呼叫失败" value="F" />
            <el-option label="通话完成" value="C" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>云呼记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="amachineName" label="主呼设备" min-width="120" />
        <el-table-column prop="amachineCode" label="主呼号码" min-width="120" />
        <el-table-column prop="adataPort" label="主呼端口" width="100" align="center" />
        <el-table-column prop="zmachineName" label="被呼设备" min-width="120" />
        <el-table-column prop="zmachineCode" label="被呼号码" min-width="120" />
        <el-table-column prop="zdataPort" label="被呼端口" width="100" align="center" />
        <el-table-column label="类型" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.intercomType == '1001' ? '语音' : '视频' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            {{
              row.state == 'W' ? '呼叫中' :
              row.state == 'D' ? '通话中' :
              row.state == 'F' ? '呼叫失败' :
              row.state == 'C' ? '通话完成' : row.state
            }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="呼叫时间" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="120">
          <template slot-scope="{ row }">
            {{ row.remark || '-' }}
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
  </div>
</template>

<script>
import { listIntercomLog } from '@/api/accessControl/accessControlApi'

export default {
  name: 'ListIntercomLog',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineCode: '',
        machineName: '',
        state: ''
      }
    }
  },
  computed: {
    currentCommunityId() {
      return this.$store.state.app.currentCommunity.communityId || ''
    }
  },
  watch: {
    currentCommunityId: {
      immediate: true,
      handler(val) {
        this.queryParams.communityId = val
        if (val) {
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listIntercomLog(this.queryParams)
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
.list-intercom-log { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
