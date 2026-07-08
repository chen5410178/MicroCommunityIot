<template>
  <div class="list-access-control-face">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="device-card">
          <div slot="header" class="card-header">
            <span>门禁设备</span>
          </div>
          <div class="device-list">
            <div
              v-for="item in devices"
              :key="item.machineId"
              class="device-item"
              :class="{ active: currentMachineId === item.machineId }"
              @click="switchDevice(item)"
            >
              {{ item.machineName }}
            </div>
          </div>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 140px">
                <el-option label="待下发" value="W" />
                <el-option label="下发成功" value="C" />
                <el-option label="下发设备" value="F" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.personType" placeholder="请选择人员类型" clearable style="width: 140px">
                <el-option label="人员" value="OWNER" />
                <el-option label="员工" value="STAFF" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.name" placeholder="请输入用户名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryStartTime" type="date" placeholder="请选择开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryEndTime" type="date" placeholder="请选择结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.comeType" placeholder="请选择来自类型" clearable style="width: 140px">
                <el-option label="楼栋授权" value="FLOOR" />
                <el-option label="人员授权" value="OWNER" />
                <el-option label="员工授权" value="STAFF" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>门禁人员</span>
            <el-button type="primary" size="small" @click="handleAuthorize">人员授权</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="人脸" width="90" align="center">
              <template slot-scope="{ row }">
                <el-image v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" />
                <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
              </template>
            </el-table-column>
            <el-table-column prop="machineName" label="门禁名称" min-width="120" />
            <el-table-column label="人员类型" width="90" align="center">
              <template slot-scope="{ row }">{{ row.personType == 'OWNER' ? '人员' : '员工' }}</template>
            </el-table-column>
            <el-table-column prop="name" label="用户名称" min-width="100" />
            <el-table-column prop="startTime" label="开始时间" min-width="140" />
            <el-table-column prop="endTime" label="结束时间" min-width="140" />
            <el-table-column label="身份证" min-width="160">
              <template slot-scope="{ row }">{{ row.idNumber || '-' }}</template>
            </el-table-column>
            <el-table-column prop="stateName" label="状态" width="90" align="center" />
            <el-table-column label="消息" min-width="100">
              <template slot-scope="{ row }">
                <span :title="row.message">{{ row.message }}</span>
              </template>
            </el-table-column>
            <el-table-column label="门禁卡号" min-width="140">
              <template slot-scope="{ row }">{{ row.cardNumber || '-' }}</template>
            </el-table-column>
            <el-table-column prop="comeTypeName" label="来自类型" width="100" align="center" />
            <el-table-column prop="createTime" label="创建时间" min-width="150" />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleReSend(row)">重新下发</el-button>
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
      </div>
    </div>
    <ReSendAccessControlFace :visible.sync="resendVisible" :resend-data="currentFace" @success="fetchList" />
    <DeleteAccessControlFace :visible.sync="delVisible" :del-data="currentFace" @success="fetchList" />
  </div>
</template>

<script>
import { listAccessControlFace, listAccessControl } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'
import ReSendAccessControlFace from '@/components/accessControl/ReSendAccessControlFace.vue'
import DeleteAccessControlFace from '@/components/accessControl/DeleteAccessControlFace.vue'

export default {
  name: 'ListAccessControlFace',
  components: { ReSendAccessControlFace, DeleteAccessControlFace },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      devices: [{ machineId: '', machineName: '全部人脸' }],
      currentMachineId: '',
      currentFace: null,
      resendVisible: false,
      delVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        state: '',
        personType: '',
        name: '',
        queryStartTime: '',
        queryEndTime: '',
        comeType: ''
      }
    }
  },
  mounted() {
    this.fetchDevices()
    this.fetchList()
  },
  methods: {
    async fetchDevices() {
      try {
        const community = getCurrentCommunity()
        const res = await listAccessControl({
          page: 1,
          row: 100,
          communityId: community ? community.communityId : ''
        })
        const list = res.data || []
        this.devices = [{ machineId: '', machineName: '全部人脸' }, ...list]
      } catch (e) {
        console.error('获取门禁设备失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const params = {
          ...this.queryParams,
          communityId: community ? community.communityId : ''
        }
        const res = await listAccessControlFace(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    switchDevice(item) {
      this.currentMachineId = item.machineId
      this.queryParams.machineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
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
    },
    handleAuthorize() {
      this.$router.push({ name: 'AccessControlPerson' })
    },
    handleReSend(row) {
      this.currentFace = row
      this.resendVisible = true
    },
    handleDelete(row) {
      this.currentFace = row
      this.delVisible = true
    }
  }
}
</script>

<style scoped>
.page-container { display: flex; gap: 16px; }
.left-panel { width: 200px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.device-card { height: 100%; }
.device-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.device-list { max-height: calc(100vh - 200px); overflow-y: auto; }
.device-item { padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #ebeef5; font-size: 13px; transition: all 0.2s; }
.device-item:hover { background: #ecf5ff; color: #409eff; }
.device-item.active { background: #ecf5ff; color: #409eff; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
