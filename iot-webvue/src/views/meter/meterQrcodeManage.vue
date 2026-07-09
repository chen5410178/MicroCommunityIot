<template>
  <div class="meter-qrcode-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.qrcodeNameLike" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.queryWay" placeholder="请选择查询方式" clearable style="width: 180px">
            <el-option
              v-for="item in queryWayList"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="启用" value="ON" />
            <el-option label="停用" value="OFF" />
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
        <span>水电二维码</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="mqId" label="编号" min-width="100" />
        <el-table-column prop="qrcodeName" label="名称" min-width="120" />
        <el-table-column prop="queryWayName" label="查询方式" min-width="100" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.state === 'ON' ? '启用' : '停用' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="150" show-overflow-tooltip>
          <template slot-scope="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column prop="createStaffName" label="创建员工" min-width="100" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewQrcode(row)">二维码</el-button>
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

    <AddMeterQrcode :visible.sync="addVisible" @success="handleSuccess" />
    <EditMeterQrcode :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMeterQrcode :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewQrCode :visible.sync="qrcodeVisible" :qr-url="qrcodeUrl" title="水电二维码" remark="用户扫码充值水电费" />
  </div>
</template>

<script>
import { listMeterQrcodes } from '@/api/meter/meterApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddMeterQrcode from '@/components/meter/AddMeterQrcode.vue'
import EditMeterQrcode from '@/components/meter/EditMeterQrcode.vue'
import DeleteMeterQrcode from '@/components/meter/DeleteMeterQrcode.vue'
import ViewQrCode from '@/components/common/ViewQrCode.vue'

export default {
  name: 'MeterQrcodeManage',
  components: { AddMeterQrcode, EditMeterQrcode, DeleteMeterQrcode, ViewQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryWayList: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      qrcodeVisible: false,
      currentRow: null,
      qrcodeUrl: '',
      queryParams: {
        page: 1,
        row: 10,
        qrcodeNameLike: '',
        queryWay: '',
        state: ''
      }
    }
  },
  mounted() {
    this.loadQueryWays()
    this.fetchList()
  },
  methods: {
    async loadQueryWays() {
      try {
        const res = await listDicts({ tableName: 'meter_qrcode', tableColumns: 'query_way' })
        this.queryWayList = res || []
      } catch (e) {
        console.error('加载查询方式字典失败', e)
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
        const res = await listMeterQrcodes(params)
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
      this.queryParams = { page: 1, row: 10, qrcodeNameLike: '', queryWay: '', state: '' }
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
    handleViewQrcode(row) {
      this.qrcodeUrl = row.qrcodeUrl || ''
      this.qrcodeVisible = true
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
