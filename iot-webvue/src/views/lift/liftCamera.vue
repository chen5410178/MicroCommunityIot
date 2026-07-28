<template>
  <div class="list-lift-camera">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.cameraName" placeholder="请输入相机名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.cameraCode" placeholder="请输入相机编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.liftName" placeholder="请输入电梯名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>电梯相机</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="lcId" label="编号" min-width="120" />
        <el-table-column prop="cameraName" label="相机名称" min-width="150" />
        <el-table-column prop="cameraCode" label="相机编号" min-width="150" />
        <el-table-column prop="factoryId" label="相机厂家" min-width="120" />
        <el-table-column prop="liftName" label="电梯名称" min-width="150" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
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
    <AddLiftCamera :visible.sync="addVisible" @success="handleSuccess" />
    <EditLiftCamera :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteLiftCamera :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLiftCameras } from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddLiftCamera from '@/components/lift/AddLiftCamera.vue'
import EditLiftCamera from '@/components/lift/EditLiftCamera.vue'
import DeleteLiftCamera from '@/components/lift/DeleteLiftCamera.vue'

export default {
  name: 'LiftCamera',
  components: { AddLiftCamera, EditLiftCamera, DeleteLiftCamera },
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
        cameraName: '',
        cameraCode: '',
        liftName: ''
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
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        const params = { ...this.queryParams, communityId }
        const res = await listLiftCameras(params)
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
      this.queryParams = { page: 1, row: 10, cameraName: '', cameraCode: '', liftName: '' }
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
