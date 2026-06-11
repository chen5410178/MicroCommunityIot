<template>
  <div class="list-barrier-manage">
    <div class="barrier-layout">
      <div class="barrier-left">
        <el-card shadow="never" class="box-card">
          <div slot="header" class="card-header">
            <span>岗亭列表</span>
          </div>
          <ul class="box-list">
            <li
              v-for="item in parkingBoxs"
              :key="item.boxId"
              :class="{ active: queryParams.boxId === item.boxId }"
              @click="switchParkingBox(item)"
            >
              {{ item.boxName }}
            </li>
          </ul>
        </el-card>
      </div>
      <div class="barrier-right">
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
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>道闸摄像头</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="machineCode" label="设备编码" min-width="120" />
            <el-table-column prop="machineName" label="设备名称" min-width="120" />
            <el-table-column prop="machineIp" label="设备IP" min-width="130" />
            <el-table-column label="设备方向" width="80" align="center">
              <template slot-scope="{ row }">
                {{ row.direction == '3306' ? '进' : '出' }}
              </template>
            </el-table-column>
            <el-table-column prop="implBeanName" label="道闸厂家" min-width="120" />
            <el-table-column prop="boxName" label="岗亭" min-width="100" />
            <el-table-column prop="stateName" label="状态" width="80" align="center" />
            <el-table-column prop="monitorName" label="监控" min-width="120" />
            <el-table-column prop="heartbeatTime" label="心跳时间" min-width="150" />
            <el-table-column prop="createTime" label="创建时间" min-width="150" />
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button v-if="row.direction == '3306'" size="small" type="text" @click="handleQrCode(row)">访客码</el-button>
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
      </div>
    </div>
    <AddBarrier :visible.sync="addVisible" @success="handleSuccess" />
    <EditBarrier :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteBarrier :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewQrCode
      :visible.sync="qrVisible"
      :qrcode-title="qrTitle"
      :url="qrUrl"
      :remark="qrRemark"
    />
  </div>
</template>

<script>
import { listBarriers, listParkingBoxs, getVisitQrCodeUrl } from '@/api/car/carApi'
import AddBarrier from '@/components/car/AddBarrier.vue'
import EditBarrier from '@/components/car/EditBarrier.vue'
import DeleteBarrier from '@/components/car/DeleteBarrier.vue'
import ViewQrCode from '@/components/common/ViewQrCode.vue'

export default {
  name: 'ListBarrier',
  components: { AddBarrier, EditBarrier, DeleteBarrier, ViewQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      parkingBoxs: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      qrVisible: false,
      qrTitle: '',
      qrUrl: '',
      qrRemark: '',
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        boxId: ''
      }
    }
  },
  mounted() {
    this.fetchParkingBoxs()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listBarriers({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchParkingBoxs() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingBoxs({ page: 1, row: 50, communityId })
        this.parkingBoxs = [{ boxId: '', boxName: '全部' }, ...(res.data || [])]
      } catch (e) { /* ignore */ }
    },
    switchParkingBox(item) {
      this.queryParams.boxId = item.boxId
      this.handleSearch()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', boxId: this.queryParams.boxId }
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
    handleQrCode(row) {
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      getVisitQrCodeUrl({ communityId }).then(res => {
        const baseUrl = (res.data && res.data.url) || ''
        this.qrTitle = (row.machineName || '') + '访客码'
        this.qrUrl = baseUrl + '&machineId=' + row.machineId
        this.qrRemark = '道闸口访客码，扫码后自动关联待进场车辆信息'
        this.qrVisible = true
      }).catch(e => {
        this.$message.error(e.msg || '获取访客码失败')
      })
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
.barrier-layout { display: flex; gap: 16px; }
.barrier-left { width: 200px; flex-shrink: 0; }
.barrier-right { flex: 1; min-width: 0; }
.box-card { margin-bottom: 0; }
.box-list { list-style: none; margin: 0; padding: 0; }
.box-list li { padding: 8px 12px; cursor: pointer; border-radius: 4px; color: #606266; }
.box-list li:hover { background: #f5f7fa; }
.box-list li.active { background: #ecf5ff; color: #409eff; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
