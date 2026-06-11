<template>
  <div class="attendance-staff-manage">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="never" class="device-card">
          <div slot="header" class="card-header">
            <span>考勤设备</span>
          </div>
          <ul class="device-list">
            <li
              v-for="item in machines"
              :key="item.machineId"
              :class="{ 'device-item-active': selectedMachineId === item.machineId }"
              class="device-item"
              @click="switchMachine(item)"
            >
              {{ item.machineName }}{{ item.classesName ? '(' + item.classesName + ')' : '' }}
            </li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="员工名称">
              <el-input v-model="queryParams.staffNameLike" placeholder="请输入员工名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>考勤员工</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="人脸" width="100" align="center">
              <template slot-scope="{ row }">
                <el-image
                  style="width: 50px; height: 50px; cursor: pointer"
                  :src="row.facePath || '/img/noPhoto.jpg'"
                  :preview-src-list="[row.facePath || '/img/noPhoto.jpg']"
                  fit="cover"
                />
              </template>
            </el-table-column>
            <el-table-column prop="staffId" label="员工编号" min-width="150" />
            <el-table-column prop="staffName" label="员工名称" min-width="150" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="120" fixed="right" align="center">
              <template slot-scope="{ row }">
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
      </el-col>
    </el-row>
    <DeleteAttendanceStaff :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAttendanceStaffs, listAttendanceMachines } from '@/api/attendance/attendanceApi'
import DeleteAttendanceStaff from '@/components/attendance/DeleteAttendanceStaff.vue'

export default {
  name: 'AttendanceStaffManage',
  components: { DeleteAttendanceStaff },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machines: [],
      selectedMachineId: '',
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        staffNameLike: ''
      }
    }
  },
  mounted() {
    this.loadMachines()
  },
  methods: {
    async loadMachines() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId
        const res = await listAttendanceMachines({ page: 1, row: 100, communityId })
        this.machines = res.data || []
        if (this.machines.length > 0) {
          this.switchMachine(this.machines[0])
        }
      } catch (e) {
        console.error('加载设备列表失败', e)
      }
    },
    switchMachine(item) {
      this.selectedMachineId = item.machineId
      this.queryParams.machineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      if (!this.queryParams.machineId) return
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId
        const res = await listAttendanceStaffs({ ...this.queryParams, communityId })
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
      this.queryParams.staffNameLike = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/attendance/addAttendanceStaff?machineId=' + this.selectedMachineId)
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
.attendance-staff-manage { padding: 0; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.device-list { list-style: none; padding: 0; margin: 0; }
.device-item {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.device-item:hover { background-color: #f5f7fa; color: #409eff; }
.device-item-active { background-color: #ecf5ff; color: #409eff; font-weight: 600; }
</style>
