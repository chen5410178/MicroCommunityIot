<template>
  <div class="list-access-control-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.locationName" placeholder="请输入位置名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>门禁设备</span>
        <el-button type="primary" size="small" @click="handleAdd">添加门禁</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineCode" label="门禁编码" min-width="120" />
        <el-table-column prop="machineName" label="门禁名称" min-width="120" />
        <el-table-column prop="machineIp" label="IP" min-width="130" />
        <el-table-column prop="machineMac" label="Mac" min-width="140" />
        <el-table-column label="开门提示" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.promptSwitch == 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="方向" width="70" align="center">
          <template slot-scope="{ row }">
            {{ row.direction == '3306' ? '进' : '出' }}
          </template>
        </el-table-column>
        <el-table-column prop="implBean" label="厂家" min-width="120" />
        <el-table-column prop="locationName" label="位置" min-width="120" />
        <el-table-column prop="stateName" label="在线" width="80" align="center" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="150" />
        <el-table-column label="监控视频" width="120" align="center">
          <template slot-scope="{ row }">
            <div>
              <span v-if="row.monitorName">{{ row.monitorName }}</span>
              <span v-else>-</span>
            </div>
            <el-button v-if="row.monitorId && row.monitorId != -1" type="text" size="small" @click="handleWatch(row)">观看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="{ row }">
            <div class="action-row">
              <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
              <el-button size="small" type="text" :loading="btnLoading['open_' + row.machineId]" @click="handleOpen(row)">开门</el-button>
              <el-button size="small" type="text" :loading="btnLoading['restart_' + row.machineId]" @click="handleRestart(row)">重启</el-button>
              <el-button size="small" type="text" :loading="btnLoading['startEle_' + row.machineId]" @click="handleStartEle(row)">开放电梯</el-button>
              <el-button size="small" type="text" :loading="btnLoading['stopEle_' + row.machineId]" @click="handleStopEle(row)">限制电梯</el-button>
            </div>
            <div class="action-row">
              <el-button size="small" type="text" @click="handleReSend(row)">重新下发</el-button>
              <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
              <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
            </div>
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
    <AddAccessControl ref="addRef" @success="handleSuccess" />
    <EditAccessControl ref="editRef" @success="handleSuccess" />
    <DeleteAccessControl ref="delRef" @success="handleSuccess" />
    <ReSendAccessControlFace ref="resendRef" @success="handleSuccess" />
  </div>
</template>

<script>
import {
  listAccessControl,
  openAccessControlDoor,
  restartAccessControlDoor,
  startAccessControlDoor,
  stopAccessControlDoor
} from '@/api/accessControl/accessControlApi'
import AddAccessControl from '@/components/accessControl/AddAccessControl.vue'
import EditAccessControl from '@/components/accessControl/EditAccessControl.vue'
import DeleteAccessControl from '@/components/accessControl/DeleteAccessControl.vue'
import ReSendAccessControlFace from '@/components/accessControl/ReSendAccessControlFace.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListAccessControlManage',
  components: { AddAccessControl, EditAccessControl, DeleteAccessControl, ReSendAccessControlFace },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      currentRow: null,
      btnLoading: {},
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        locationName: ''
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
        const community = getCurrentCommunity()
        const res = await listAccessControl({
          ...this.queryParams,
          communityId: community ? community.communityId : ''
        })
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
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', locationName: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$refs.addRef.open()
    },
    handleEdit(row) {
      this.$refs.editRef.open(row)
    },
    handleDelete(row) {
      this.$refs.delRef.open(row)
    },
    handleReSend(row) {
      this.$refs.resendRef.open(row)
    },
    handleDetail(row) {
      this.$router.push(`/pages/accessControl/accessControlDetail?machineId=${row.machineId}`)
    },
    async handleOpen(row) {
      const key = 'open_' + row.machineId
      this.$set(this.btnLoading, key, true)
      try {
        const community = getCurrentCommunity()
        await openAccessControlDoor({ machineId: row.machineId, communityId: community ? community.communityId : '' })
        this.$message.success('开门成功')
      } catch (e) {
        this.$message.error(e.msg || '开门失败')
      } finally {
        this.$set(this.btnLoading, key, false)
      }
    },
    async handleRestart(row) {
      const key = 'restart_' + row.machineId
      this.$set(this.btnLoading, key, true)
      try {
        const community = getCurrentCommunity()
        await restartAccessControlDoor({ machineId: row.machineId, communityId: community ? community.communityId : '' })
        this.$message.success('重启成功')
      } catch (e) {
        this.$message.error(e.msg || '重启失败')
      } finally {
        this.$set(this.btnLoading, key, false)
      }
    },
    async handleStartEle(row) {
      const key = 'startEle_' + row.machineId
      this.$set(this.btnLoading, key, true)
      try {
        const community = getCurrentCommunity()
        await startAccessControlDoor({ machineId: row.machineId, communityId: community ? community.communityId : '' })
        this.$message.success('开放电梯成功')
      } catch (e) {
        this.$message.error(e.msg || '开放电梯失败')
      } finally {
        this.$set(this.btnLoading, key, false)
      }
    },
    async handleStopEle(row) {
      const key = 'stopEle_' + row.machineId
      this.$set(this.btnLoading, key, true)
      try {
        const community = getCurrentCommunity()
        await stopAccessControlDoor({ machineId: row.machineId, communityId: community ? community.communityId : '' })
        this.$message.success('限制电梯成功')
      } catch (e) {
        this.$message.error(e.msg || '限制电梯失败')
      } finally {
        this.$set(this.btnLoading, key, false)
      }
    },
    handleWatch(row) {
      this.$message.info('监控播放功能暂未开放')
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
.action-row { display: flex; flex-wrap: wrap; justify-content: center; gap: 0; }
</style>
