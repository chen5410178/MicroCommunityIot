<template>
  <div class="calling-contact-manage">
    <div class="content-wrap">
      <div class="left-tree">
        <el-card shadow="never" class="tree-card">
          <div slot="header" class="card-header">
            <span>门禁设备</span>
          </div>
          <div class="tree-body">
            <div
              v-for="item in deviceList"
              :key="item.machineId"
              class="tree-item"
              :class="{ 'tree-item-active': selectedMachineId === item.machineId }"
              @click="switchDevice(item)"
            >
              {{ item.machineName }}
            </div>
            <div v-if="deviceList.length === 0" class="tree-empty">暂无设备</div>
          </div>
        </el-card>
      </div>
      <div class="right-content">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.callingNum" placeholder="请输入呼叫号码" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>通讯录信息</span>
            <el-button type="primary" size="small" @click="handleAdd" :disabled="!selectedMachineId">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="deviceId" label="通讯录ID" min-width="80" />
            <el-table-column prop="deviceName" label="设备名称" min-width="120" />
            <el-table-column prop="callingNum" label="呼叫号码" min-width="100" />
            <el-table-column prop="platformName" label="云呼平台" min-width="100" />
            <el-table-column label="操作" width="100" fixed="right" align="center">
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
      </div>
    </div>
    <ChooseCallingDevice :visible.sync="chooseVisible" @choose="handleChooseDevice" />
    <DeleteCallingContact :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listCallingContact, saveCallingContact, listAccessControl } from '@/api/accessControl/accessControlApi'
import ChooseCallingDevice from '@/components/accessControl/ChooseCallingDevice.vue'
import DeleteCallingContact from '@/components/accessControl/DeleteCallingContact.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'CallingContactManage',
  components: { ChooseCallingDevice, DeleteCallingContact },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      deviceList: [],
      selectedMachineId: '',
      chooseVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        deviceName: '',
        callingNum: ''
      }
    }
  },
  mounted() {
    this.loadDevices()
    this.fetchList()
  },
  methods: {
    async loadDevices() {
      try {
        const res = await listAccessControl({
          page: 1,
          row: 50,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        this.deviceList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          machineId: this.selectedMachineId,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        const res = await listCallingContact(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    switchDevice(item) {
      this.selectedMachineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.deviceName = ''
      this.queryParams.callingNum = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleAdd() {
      this.chooseVisible = true
    },
    async handleChooseDevice(row) {
      try {
        await saveCallingContact({
          deviceId: row.deviceId,
          platformId: row.platformId,
          machineId: this.selectedMachineId,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        this.$message.success('添加成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      }
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
.content-wrap {
  display: flex;
  gap: 16px;
}
.left-tree {
  width: 200px;
  flex-shrink: 0;
}
.right-content {
  flex: 1;
  min-width: 0;
}
.tree-card {
  height: 100%;
}
.tree-body {
  max-height: calc(100vh - 260px);
  overflow-y: auto;
}
.tree-item {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.tree-item:hover {
  background-color: #f5f7fa;
}
.tree-item-active {
  background-color: #ecf5ff;
  color: #409eff;
}
.tree-empty {
  padding: 20px;
  text-align: center;
  color: #909399;
}
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
