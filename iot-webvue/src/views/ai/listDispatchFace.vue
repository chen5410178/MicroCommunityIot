<template>
  <div class="dispatch-face-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.personName" placeholder="请输入人员名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.personLink" placeholder="请输入手机号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>布控人员</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="人脸" width="100" align="center">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.faceUrl"
              :src="row.faceUrl"
              :preview-src-list="[row.faceUrl]"
              style="width: 60px; height: 60px; border-radius: 4px"
              fit="cover"
            />
            <span v-else>暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="personName" label="人员名称" min-width="150" />
        <el-table-column prop="personId" label="编号" width="150" />
        <el-table-column prop="communityName" label="项目名称" min-width="150" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
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
    <AddDispatchFace :visible.sync="addVisible" @success="handleSuccess" />
    <DeleteDispatchFace :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listDispatchFaces } from '@/api/ai/dispatchFaceApi'
import AddDispatchFace from '@/components/ai/AddDispatchFace.vue'
import DeleteDispatchFace from '@/components/ai/DeleteDispatchFace.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListDispatchFace',
  components: { AddDispatchFace, DeleteDispatchFace },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        personName: '',
        personLink: '',
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
        const res = await listDispatchFaces(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, personName: '', personLink: '', communityId: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
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
