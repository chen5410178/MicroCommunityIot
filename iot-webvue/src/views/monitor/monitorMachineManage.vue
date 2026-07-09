<template>
  <div class="monitor-machine-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入监控名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入监控编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.maId" placeholder="请选择区域" clearable style="width: 180px">
            <el-option
              v-for="item in areaList"
              :key="item.maId"
              :label="item.maName"
              :value="item.maId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.protocol" placeholder="请选择协议" clearable style="width: 180px">
            <el-option
              v-for="item in protocolList"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option label="打开中" value="1001" />
            <el-option label="关闭中" value="1002" />
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
        <span>监控设备</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="画面" width="80" align="center">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.photoUrl"
              :src="row.photoUrl"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px"
              @click="handleImagePreview(row)"
            />
            <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
          </template>
        </el-table-column>
        <el-table-column prop="machineId" label="设备ID" min-width="150" />
        <el-table-column prop="machineName" label="名称" min-width="150" />
        <el-table-column prop="machineCode" label="编号" min-width="120" />
        <el-table-column prop="locationName" label="位置" min-width="120" />
        <el-table-column prop="maName" label="区域" min-width="120" />
        <el-table-column prop="mmName" label="厂家" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handlePreview(row)">预览</el-button>
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
    <AddMonitorMachine :visible.sync="addVisible" @success="handleSuccess" />
    <EditMonitorMachine :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMonitorMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <PlayMonitorVideo :visible.sync="previewVisible" :video-info="currentRow" />
  </div>
</template>

<script>
import { listMonitorMachines, listMonitorAreas } from '@/api/monitor/monitorApi'
import { listDicts } from '@/api/system/systemApi'
import AddMonitorMachine from '@/components/monitor/AddMonitorMachine.vue'
import EditMonitorMachine from '@/components/monitor/EditMonitorMachine.vue'
import DeleteMonitorMachine from '@/components/monitor/DeleteMonitorMachine.vue'
import PlayMonitorVideo from '@/components/monitor/PlayMonitorVideo.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MonitorMachineManage',
  components: { AddMonitorMachine, EditMonitorMachine, DeleteMonitorMachine, PlayMonitorVideo },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      previewVisible: false,
      currentRow: null,
      areaList: [],
      protocolList: [],
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        maId: '',
        protocol: '',
        state: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.fetchAreaList()
    this.fetchProtocolList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorMachines({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchAreaList() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorAreas({ page: 1, row: 100, communityId })
        this.areaList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchProtocolList() {
      try {
        const res = await listDicts({ tableName: 'monitor_machine', tableColumns: 'protocol' })
        this.protocolList = res || []
      } catch { /* ignore */ }
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
        maId: '',
        protocol: '',
        state: ''
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
    handleImagePreview(row) {
      const html = '<img style="max-width:100%" src="' + row.photoUrl + '" />'
      this.$alert(html, '画面', { dangerouslyUseHTMLString: true })
    },
    handlePreview(row) {
      this.currentRow = { ...row }
      this.previewVisible = true
    },
    handleDetail(row) {
      this.$router.push(`/pages/monitor/monitorMachineDetail?machineId=${row.machineId}&maId=${row.maId || ''}`)
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
