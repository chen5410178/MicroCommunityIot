<template>
  <div class="lamp-machine-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.locationName" placeholder="请输入位置" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.factoryId" placeholder="请选择厂家" clearable style="width: 180px">
            <el-option
              v-for="item in factoryList"
              :key="item.factoryId"
              :label="item.factoryName"
              :value="item.factoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option
              v-for="item in stateList"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
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
        <span>路灯设备</span>
        <div>
          <el-button type="primary" size="small" @click="handleOpenClose">设置开关灯时间</el-button>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineId" label="设备ID" width="150" />
        <el-table-column prop="machineName" label="名称" min-width="120" />
        <el-table-column prop="machineCode" label="设备编号" min-width="120" />
        <el-table-column prop="locationName" label="位置" min-width="120" />
        <el-table-column prop="factoryName" label="厂家" min-width="100" />
        <el-table-column label="状态" min-width="150" align="center">
          <template slot-scope="{ row }">
            <span>{{ row.stateName }}</span>
            <el-link
              v-if="row.state === '1001'"
              type="primary"
              style="margin-left: 4px"
              @click="handleSwitchState(row, '1002')"
            >(开灯)</el-link>
            <el-link
              v-if="row.state === '1002'"
              type="warning"
              style="margin-left: 4px"
              @click="handleSwitchState(row, '1001')"
            >(关灯)</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="isOnlineStateName" label="在线状态" width="100" align="center" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
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
    <AddLampMachine :visible.sync="addVisible" @success="handleSuccess" />
    <EditLampMachine :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteLampMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLampMachine, listLampMachineFactory, switchLampState } from '@/api/lamp/lampApi'
import { listDicts } from '@/api/dev/devApi'
import AddLampMachine from '@/components/lamp/AddLampMachine.vue'
import EditLampMachine from '@/components/lamp/EditLampMachine.vue'
import DeleteLampMachine from '@/components/lamp/DeleteLampMachine.vue'

export default {
  name: 'LampMachineManage',
  components: { AddLampMachine, EditLampMachine, DeleteLampMachine },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      factoryList: [],
      stateList: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        locationName: '',
        factoryId: '',
        state: ''
      }
    }
  },
  mounted() {
    this.loadDictionaries()
    this.fetchList()
  },
  methods: {
    async loadDictionaries() {
      try {
        const [factoryRes, stateRes] = await Promise.all([
          listLampMachineFactory({ page: 1, row: 100 }),
          listDicts({ tableName: 'lamp_machine', columnName: 'state' })
        ])
        this.factoryList = factoryRes.data || []
        this.stateList = stateRes.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listLampMachine({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, machineName: '', machineCode: '', locationName: '', factoryId: '', state: '' }
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
    handleDetail(row) {
      this.$router.push('/pages/lamp/lampMachineDetail?machineId=' + row.machineId)
    },
    handleOpenClose() {
      this.$router.push('/pages/lamp/lampMachineOpenCloseManage')
    },
    async handleSwitchState(row, state) {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await switchLampState({ machineId: row.machineId, state, communityId })
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
