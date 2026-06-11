<template>
  <div class="list-lock-person">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="业主名称">
          <el-input v-model="queryParams.name" placeholder="请选择业主" readonly clearable style="width: 180px" />
          <el-button size="small" style="margin-left: 4px" @click="searchVisible = true">选择</el-button>
        </el-form-item>
        <el-form-item label="有效期">
          <el-date-picker
            v-model="queryParams.queryTime"
            type="date"
            placeholder="请选择时间（在有效期内）"
            value-format="yyyy-MM-dd"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="授权状态">
          <el-select v-model="queryParams.state" placeholder="请选择授权状态" clearable style="width: 180px">
            <el-option label="可用" value="1001" />
            <el-option label="冻结" value="2002" />
          </el-select>
        </el-form-item>
        <el-form-item label="开锁方式">
          <el-select v-model="queryParams.openModel" placeholder="请选择开锁方式" clearable style="width: 180px">
            <el-option label="密码开锁" value="1001" />
            <el-option label="蓝牙开锁" value="2002" />
          </el-select>
        </el-form-item>
        <el-form-item label="门锁名称">
          <el-input v-model="queryParams.machineName" placeholder="请输入门锁名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="门锁编号">
          <el-input v-model="queryParams.machineCode" placeholder="请输入门锁编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>门锁授权</span>
        <el-button type="primary" size="small" @click="handleAdd">授权</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="lpId" label="授权编号" min-width="120" />
        <el-table-column prop="machineName" label="门锁名称" min-width="150" />
        <el-table-column prop="machineCode" label="门锁编码" min-width="150" />
        <el-table-column prop="name" label="业主名称" min-width="150" />
        <el-table-column label="开锁方式" width="100" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.openModel === '1001'">密码开锁</span>
            <span v-else-if="row.openModel === '2002'">蓝牙开锁</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="授权状态" width="120" align="center">
          <template slot-scope="{ row }">
            <template v-if="row.state === '1001'">
              <span>可用</span>
              <el-button type="text" size="small" @click="handleSwitchState(row)">冻结</el-button>
            </template>
            <template v-else-if="row.state === '2002'">
              <span>冻结</span>
              <el-button type="text" size="small" @click="handleSwitchState(row)">解冻</el-button>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="授权开始时间" min-width="160" />
        <el-table-column prop="endTime" label="授权结束时间" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleRenewal(row)">续期</el-button>
            <el-button size="small" type="text" @click="handleResetPwd(row)">重置密码</el-button>
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
    <SearchOwner :visible.sync="searchVisible" @selected="handleSelected" />
    <DeleteLockPerson :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <RenewalLockPerson :visible.sync="renewalVisible" :edit-data="currentRow" @success="handleSuccess" />
    <ResetLockPwd :visible.sync="resetPwdVisible" :edit-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLockPersons, updateLockPerson } from '@/api/lock/lockApi'
import SearchOwner from '@/components/monitor/SearchOwner.vue'
import DeleteLockPerson from '@/components/lock/DeleteLockPerson.vue'
import RenewalLockPerson from '@/components/lock/RenewalLockPerson.vue'
import ResetLockPwd from '@/components/lock/ResetLockPwd.vue'

export default {
  name: 'ListLockPerson',
  components: { SearchOwner, DeleteLockPerson, RenewalLockPerson, ResetLockPwd },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      searchVisible: false,
      delVisible: false,
      renewalVisible: false,
      resetPwdVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        name: '',
        personId: '',
        queryTime: '',
        state: '',
        openModel: '',
        machineName: '',
        machineCode: ''
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
        const res = await listLockPersons({ ...this.queryParams, communityId })
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
        page: 1, row: 10,
        name: '', personId: '', queryTime: '',
        state: '', openModel: '', machineName: '', machineCode: ''
      }
      this.fetchList()
    },
    handleSelected(row) {
      this.queryParams.name = row.name
      this.queryParams.personId = row.memberId
    },
    handleAdd() {
      this.$router.push('/pages/lock/addLockPerson')
    },
    handleRenewal(row) {
      this.currentRow = { ...row }
      this.renewalVisible = true
    },
    handleResetPwd(row) {
      this.currentRow = { ...row }
      this.resetPwdVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    async handleSwitchState(row) {
      const newState = row.state === '1001' ? '2002' : '1001'
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await updateLockPerson({ ...row, state: newState, communityId })
        this.$message.success('操作成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      }
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
