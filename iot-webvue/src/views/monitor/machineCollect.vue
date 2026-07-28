<template>
  <div class="machine-collect">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>收藏视频</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineName" label="设备名称" min-width="180" />
        <el-table-column prop="machineCode" label="设备编号" min-width="150" />
        <el-table-column prop="createTime" label="时间" width="180" />
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
    <SelectVideoMachine :visible.sync="selectVisible" @selected="handleSelected" />
    <DeleteMachineCollect :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMonitorCollects, saveMonitorCollect } from '@/api/monitor/monitorApi'
import SelectVideoMachine from '@/components/monitor/SelectVideoMachine.vue'
import DeleteMachineCollect from '@/components/monitor/DeleteMachineCollect.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MachineCollect',
  components: { SelectVideoMachine, DeleteMachineCollect },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      selectVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        communityId: ''
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        this.queryParams.communityId = communityId
        const res = await listMonitorCollects(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleAdd() {
      this.selectVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    async handleSelected(item) {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveMonitorCollect({
          machineId: item.machineId,
          machineName: item.machineName,
          machineCode: item.machineCode,
          communityId
        })
        this.$message.success('添加成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
