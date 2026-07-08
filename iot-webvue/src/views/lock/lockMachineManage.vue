<template>
  <div class="lock-machine-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入门锁名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入门锁编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.roomId" placeholder="请选择房屋编号" readonly style="width: 180px" />
          <el-button style="margin-left: 4px" @click="roomVisible = true">选择</el-button>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.implBean" placeholder="请选择门禁厂家" clearable style="width: 180px">
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
        <span>门锁设备</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineId" label="门锁ID" min-width="140" />
        <el-table-column prop="machineName" label="门锁名称" min-width="140" />
        <el-table-column prop="machineCode" label="门锁编号" min-width="120" />
        <el-table-column prop="roomName" label="房屋名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="charge" label="剩余电量(%)" width="120" align="center" />
        <el-table-column prop="factoryName" label="门禁厂家" min-width="120" />
        <el-table-column prop="stateName" label="在线状态" width="100" align="center" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" type="text" @click="handleUnlock(row)">开锁</el-button>
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
    <AddLockMachine :visible.sync="addVisible" @success="handleSuccess" />
    <EditLockMachine :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteLockMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <RoomTree :visible.sync="roomVisible" @confirm="handleRoomConfirm" />
  </div>
</template>

<script>
import { listLockMachines, listLockMachineFactorys, unLockMachine } from '@/api/lock/lockApi'
import AddLockMachine from '@/components/lock/AddLockMachine.vue'
import EditLockMachine from '@/components/lock/EditLockMachine.vue'
import DeleteLockMachine from '@/components/lock/DeleteLockMachine.vue'
import RoomTree from '@/components/accessControl/RoomTree.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LockMachineManage',
  components: { AddLockMachine, EditLockMachine, DeleteLockMachine, RoomTree },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      roomVisible: false,
      currentRow: null,
      factoryList: [],
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        roomId: '',
        implBean: ''
      }
    }
  },
  mounted() {
    this.loadFactoryList()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        const res = await listLockMachines(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async loadFactoryList() {
      try {
        const res = await listLockMachineFactorys({ page: -1, row: 100 })
        this.factoryList = res.data || []
      } catch (e) {
        console.error('加载厂家列表失败', e)
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
        machineName: '',
        machineCode: '',
        roomId: '',
        implBean: ''
      }
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
      this.$router.push(`/pages/lock/lockMachineDetail?machineId=${row.machineId}&roomId=${row.roomId}`)
    },
    async handleUnlock(row) {
      if (row.state === 'OFFLINE') {
        this.$message.warning('该门锁已离线，无法开锁')
        return
      }
      try {
        const communityId = (getCurrentCommunity() || {}).communityId
        await unLockMachine({ machineId: row.machineId, communityId })
        this.$message.success('开锁成功')
      } catch (e) {
        this.$message.error(e.msg || '开锁失败')
      }
    },
    handleSuccess() {
      this.fetchList()
    },
    handleRoomConfirm({ roomId }) {
      this.queryParams.roomId = roomId
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
