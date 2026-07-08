<template>
  <div class="parking-box-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.boxId" placeholder="请输入岗亭编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.boxName" placeholder="请输入岗亭名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.tempCarIn" placeholder="请选择" clearable style="width: 180px">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
            <el-option label="需访客登记" value="V" />
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
        <span>岗亭信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="boxId" label="岗亭编号" min-width="120" />
        <el-table-column prop="boxName" label="岗亭名称" min-width="120" />
        <el-table-column prop="paNum" label="停车场" min-width="120" />
        <el-table-column label="临时车进场" width="120" align="center">
          <template slot-scope="{ row }">
            {{ row.tempCarIn === 'Y' ? '是' : row.tempCarIn === 'N' ? '否' : row.tempCarIn === 'V' ? '需访客登记' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="临时车审核" width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.tempAuth === 'Y' ? '是' : row.tempAuth === 'N' ? '否' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="是否收费" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.fee === 'Y' ? '是' : row.fee === 'N' ? '否' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="已在场" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.blueCarIn === 'Y' ? '是' : row.blueCarIn === 'N' ? '否' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="未在场" width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.yelowCarIn === 'Y' ? '是' : row.yelowCarIn === 'N' ? '否' : '-' }}
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
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleParkingBoxArea(row)">共享岗亭</el-button>
            <el-button size="small" type="text" @click="handleControl(row)">控制台</el-button>
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
    <AddParkingBox :visible.sync="addVisible" @success="handleSuccess" />
    <EditParkingBox :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteParkingBox :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listParkingBoxs } from '@/api/car/carApi'
import AddParkingBox from '@/components/car/AddParkingBox.vue'
import EditParkingBox from '@/components/car/EditParkingBox.vue'
import DeleteParkingBox from '@/components/car/DeleteParkingBox.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingBox',
  components: { AddParkingBox, EditParkingBox, DeleteParkingBox },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        boxId: '',
        boxName: '',
        tempCarIn: ''
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
        const res = await listParkingBoxs({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, boxId: '', boxName: '', tempCarIn: '' }
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
    handleParkingBoxArea(row) {
      this.$router.push({ path: '/pages/car/parkingBoxArea', query: { boxId: row.boxId, boxName: row.boxName } })
    },
    handleControl(row) {
      this.$router.push({ path: '/pages/car/parkingAreaControl', query: { boxId: row.boxId, paId: row.paId } })
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
