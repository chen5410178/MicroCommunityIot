<template>
  <div class="list-person-face-qrcode">
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
        <span>人脸二维码</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="pfqId" label="编号" min-width="80" />
        <el-table-column prop="qrcodeName" label="名称" min-width="120" />
        <el-table-column label="短信验证" min-width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.smsValidate === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="手机号验证" min-width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.telValidate === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="审核" min-width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.audit === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="邀请开关" min-width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.invite === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="邀请码" min-width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.invite === 'ON' ? row.inviteCode : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.state === 'ON' ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column prop="createStaffName" label="创建人员" min-width="120" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewQrCode(row)">二维码</el-button>
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
    <AddPersonFaceQrcode :visible.sync="addVisible" @success="handleSuccess" />
    <EditPersonFaceQrcode :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeletePersonFaceQrcode :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewQrCode :visible.sync="qrVisible" :title="qrTitle" :qr-url="qrUrl" :remark="qrRemark" />
  </div>
</template>

<script>
import { listPersonFaceQrcodes } from '@/api/accessControl/accessControlApi'
import AddPersonFaceQrcode from '@/components/accessControl/AddPersonFaceQrcode.vue'
import EditPersonFaceQrcode from '@/components/accessControl/EditPersonFaceQrcode.vue'
import DeletePersonFaceQrcode from '@/components/accessControl/DeletePersonFaceQrcode.vue'
import ViewQrCode from '@/components/common/ViewQrCode.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListPersonFaceQrcode',
  components: { AddPersonFaceQrcode, EditPersonFaceQrcode, DeletePersonFaceQrcode, ViewQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
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
        const res = await listPersonFaceQrcodes(params)
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
      this.queryParams = {
        page: 1,
        row: 10,
        qrcodeName: ''
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
    handleViewQrCode(row) {
      this.qrTitle = row.qrcodeName || '人脸二维码'
      this.qrUrl = row.qrCodeUrl || ''
      this.qrRemark = row.remark || ''
      this.qrVisible = true
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
