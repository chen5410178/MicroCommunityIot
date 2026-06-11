<template>
  <div class="lock-inout-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.userName" placeholder="请选择业主" readonly clearable style="width: 180px" @clear="handleClearOwner" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleOpenSearchOwner">选择业主</el-button>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.openTypeCd" placeholder="请选择开门方式" clearable style="width: 160px">
            <el-option label="全部" value="" />
            <el-option label="密码开锁" value="1001" />
            <el-option label="蓝牙开锁" value="2002" />
            <el-option label="远程开锁" value="3003" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 160px">
            <el-option label="全部" value="" />
            <el-option label="开门成功" value="C" />
            <el-option label="开门失败" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入门锁编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryStartTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryEndTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>进出记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="inoutId" label="进出编号" min-width="120" />
        <el-table-column prop="machineName" label="门锁名称" min-width="150" />
        <el-table-column prop="machineCode" label="门锁编码" min-width="120" />
        <el-table-column prop="userName" label="操作人名称" min-width="120" />
        <el-table-column prop="tel" label="操作人手机号" min-width="130" />
        <el-table-column label="开门方式" min-width="100" align="center">
          <template slot-scope="{ row }">
            {{
              row.openTypeCd == '1001' ? '密码开锁' :
              row.openTypeCd == '2002' ? '蓝牙开锁' :
              row.openTypeCd == '3003' ? '远程开锁' : '其他'
            }}
          </template>
        </el-table-column>
        <el-table-column label="开门状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state == 'C'" type="success" size="small">开门成功</el-tag>
            <el-tag v-else-if="row.state == 'F'" type="danger" size="small">开门失败</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
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
    <SearchOwner :visible.sync="ownerDialogVisible" @selected="handleOwnerSelected" />
  </div>
</template>

<script>
import { listLockInouts } from '@/api/lock/lockApi'
import SearchOwner from '@/components/monitor/SearchOwner.vue'

export default {
  name: 'LockInoutManage',
  components: { SearchOwner },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      ownerDialogVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        userId: '',
        userName: '',
        openTypeCd: '',
        state: '',
        machineCode: '',
        queryStartTime: '',
        queryEndTime: ''
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
        const res = await listLockInouts(this.queryParams)
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
      this.queryParams = {
        page: 1,
        row: 10,
        communityId: this.queryParams.communityId,
        userId: '',
        userName: '',
        openTypeCd: '',
        state: '',
        machineCode: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleOpenSearchOwner() {
      this.ownerDialogVisible = true
    },
    handleOwnerSelected(row) {
      this.queryParams.userId = row.memberId
      this.queryParams.userName = row.name
    },
    handleClearOwner() {
      this.queryParams.userId = ''
      this.queryParams.userName = ''
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
