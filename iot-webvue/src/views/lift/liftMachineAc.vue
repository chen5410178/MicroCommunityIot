<template>
  <div class="lift-machine-ac-page">
    <el-row :gutter="16">
      <el-col :span="5">
        <el-card shadow="never" class="left-card">
          <el-menu
            :default-active="selectedMachineId"
            class="lift-menu"
            @select="handleSelectMachine"
          >
            <el-menu-item
              v-for="item in machineList"
              :key="item.machineId"
              :index="item.machineId"
            >
              <span slot="title">{{ item.machineName }}</span>
            </el-menu-item>
          </el-menu>
          <div v-if="machineList.length === 0" style="text-align: center; padding: 40px 0; color: #909399;">
            暂无电梯设备
          </div>
        </el-card>
      </el-col>
      <el-col :span="19">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.acMachineName" placeholder="请输入门禁设备名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.acMachineCode" placeholder="请输入门禁设备编号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>电梯门禁</span>
            <el-button type="primary" size="small" :disabled="!selectedMachineId" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="machineName" label="电梯名称" min-width="120" />
            <el-table-column prop="machineCode" label="电梯编号" min-width="120" />
            <el-table-column prop="acMachineName" label="门禁名称" min-width="120" />
            <el-table-column prop="acMachineCode" label="门禁编号" min-width="120" />
            <el-table-column label="门禁类型" min-width="100" align="center">
              <template slot-scope="{ row }">
                <span v-if="row.acType === '1001'">人脸机</span>
                <span v-else-if="row.acType === '2002'">刷卡机</span>
                <span v-else-if="row.acType === '3003'">二维码机</span>
                <span v-else>{{ row.acType }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
    <AddLiftMachineAc :visible.sync="addVisible" :machine-data="addMachineData" @success="handleSuccess" />
    <DeleteLiftMachineAc :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLiftMachines, listLiftMachineAcs } from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddLiftMachineAc from '@/components/lift/AddLiftMachineAc.vue'
import DeleteLiftMachineAc from '@/components/lift/DeleteLiftMachineAc.vue'

export default {
  name: 'LiftMachineAc',
  components: { AddLiftMachineAc, DeleteLiftMachineAc },
  data() {
    return {
      loading: false,
      machineList: [],
      list: [],
      total: 0,
      selectedMachineId: '',
      addVisible: false,
      delVisible: false,
      currentRow: null,
      addMachineData: null,
      queryParams: {
        page: 1,
        row: 10,
        acMachineName: '',
        acMachineCode: ''
      }
    }
  },
  mounted() {
    this.fetchMachineList()
  },
  methods: {
    async fetchMachineList() {
      const community = getCurrentCommunity()
      try {
        const res = await listLiftMachines({ page: 1, row: 100, communityId: community ? community.communityId : '' })
        this.machineList = res.data || []
        if (this.machineList.length > 0) {
          this.selectedMachineId = this.machineList[0].machineId
          this.fetchList()
        }
      } catch { /* ignore */ }
    },
    handleSelectMachine(machineId) {
      this.selectedMachineId = machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      if (!this.selectedMachineId) return
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const params = { ...this.queryParams, machineId: this.selectedMachineId, communityId: community ? community.communityId : '' }
        const res = await listLiftMachineAcs(params)
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
      this.queryParams = { page: 1, row: 10, acMachineName: '', acMachineCode: '' }
      this.fetchList()
    },
    handleAdd() {
      const machine = this.machineList.find(m => m.machineId === this.selectedMachineId)
      this.addMachineData = { machineId: this.selectedMachineId, machineName: machine ? machine.machineName : '' }
      this.addVisible = true
    },
    handleDetail(row) {
      this.$router.push(`/pages/lift/liftMachineDetail?machineId=${row.machineId}`)
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
.lift-machine-ac-page { padding: 0; }
.left-card { min-height: 500px; }
.left-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.lift-menu { border-right: none; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
