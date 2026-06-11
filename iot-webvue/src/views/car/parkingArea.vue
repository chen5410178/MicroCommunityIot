<template>
  <div class="parking-area-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="停车场编号">
          <el-input v-model="queryParams.num" placeholder="请输入编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.typeCd" placeholder="请选择类型" clearable style="width: 180px">
            <el-option label="地上停车场" value="1001" />
            <el-option label="地下停车场" value="2002" />
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
        <span>停车场</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="paId" label="ID" min-width="120" />
        <el-table-column prop="num" label="编号" min-width="120" />
        <el-table-column label="类型" min-width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.typeCd === '1001' ? '地上' : row.typeCd === '2002' ? '地下' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="extPaId" label="三方ID" min-width="120">
          <template slot-scope="{ row }">
            {{ row.extPaId || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="备注" width="100" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
            <el-button size="small" type="text" @click="handleTotalControl(row)">控制台</el-button>
            <el-button size="small" type="text" @click="handleFindCar(row)">找车</el-button>
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
    <AddParkingArea :visible.sync="addVisible" @success="handleSuccess" />
    <EditParkingArea :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteParkingArea :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewQrCode :visible.sync="qrVisible" :title="qrTitle" :qr-url="qrUrl" :remark="qrRemark" />
  </div>
</template>

<script>
import { listParkingAreas } from '@/api/car/carApi'
import AddParkingArea from '@/components/car/AddParkingArea.vue'
import EditParkingArea from '@/components/car/EditParkingArea.vue'
import DeleteParkingArea from '@/components/car/DeleteParkingArea.vue'
import ViewQrCode from '@/components/common/ViewQrCode.vue'

export default {
  name: 'ParkingArea',
  components: { AddParkingArea, EditParkingArea, DeleteParkingArea, ViewQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      qrVisible: false,
      currentRow: null,
      qrTitle: '',
      qrUrl: '',
      qrRemark: '',
      queryParams: {
        page: 1,
        row: 10,
        num: '',
        typeCd: ''
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingAreas({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, num: '', typeCd: '' }
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
    handleTotalControl(row) {
      this.$router.push('/pages/car/parkingAreaTotalControl?paId=' + row.paId)
    },
    handleFindCar(row) {
      this.qrTitle = '找车二维码'
      this.qrUrl = row.findQrcodeUrl || ''
      this.qrRemark = '将二维码贴到停车场，方便用户扫码找车'
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
