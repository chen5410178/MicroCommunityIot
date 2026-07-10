<template>
  <div class="list-pass-qrcode-setting">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.qrcodeName" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>通行码设置</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="qrcodeName" label="名称" min-width="120" />
        <el-table-column label="验证" min-width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.smsValidate === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="审核" min-width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.audit === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="失效时间" min-width="100">
          <template slot-scope="{ row }">
            {{ row.expired ? row.expired + '分钟' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.state === 'ON' ? '启用' : '停用' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column prop="remark" label="说明" min-width="120" show-overflow-tooltip>
          <template slot-scope="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right" align="center">
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
    <AddPassQrcodeSetting :visible.sync="addVisible" @success="handleSuccess" />
    <EditPassQrcodeSetting :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeletePassQrcodeSetting :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewQrCode :visible.sync="qrcodeVisible" :qr-url="qrcodeUrl" title="通行码申请" remark="请将二维码解码后，地址绑定到公众号菜单" />
  </div>
</template>

<script>
import { listPassQrcodeSettings } from '@/api/accessControl/accessControlApi'
import AddPassQrcodeSetting from '@/components/accessControl/AddPassQrcodeSetting.vue'
import EditPassQrcodeSetting from '@/components/accessControl/EditPassQrcodeSetting.vue'
import DeletePassQrcodeSetting from '@/components/accessControl/DeletePassQrcodeSetting.vue'
import ViewQrCode from '@/components/common/ViewQrCode.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListPassQrcodeSetting',
  components: { AddPassQrcodeSetting, EditPassQrcodeSetting, DeletePassQrcodeSetting, ViewQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      qrcodeVisible: false,
      currentRow: null,
      qrcodeUrl: '',
      queryParams: {
        page: 1,
        row: 10,
        qrcodeName: ''
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
        const params = {
          ...this.queryParams,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        const res = await listPassQrcodeSettings(params)
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
      this.queryParams = { page: 1, row: 10, qrcodeName: '' }
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
