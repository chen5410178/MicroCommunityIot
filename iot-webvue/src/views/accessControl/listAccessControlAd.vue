<template>
  <div class="list-access-control-ad">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="广告名称">
          <el-input v-model="queryParams.adName" placeholder="请输入广告名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>广告</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="adName" label="广告名称" min-width="120" />
        <el-table-column label="状态" min-width="80">
          <template slot-scope="{ row }">
            {{ row.state === 'Y' ? '在用' : '停用' }}
          </template>
        </el-table-column>
        <el-table-column label="广告类型" min-width="100">
          <template slot-scope="{ row }">
            {{ row.adType === 'IMG' ? '图片' : row.adType === 'VIDEO' ? '视频' : row.adType }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
    <AddAccessControlAd :visible.sync="addVisible" @success="handleSuccess" />
    <EditAccessControlAd :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteAccessControlAd :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAccessControlAds } from '@/api/accessControl/accessControlApi'
import AddAccessControlAd from '@/components/accessControl/AddAccessControlAd.vue'
import EditAccessControlAd from '@/components/accessControl/EditAccessControlAd.vue'
import DeleteAccessControlAd from '@/components/accessControl/DeleteAccessControlAd.vue'

export default {
  name: 'ListAccessControlAd',
  components: { AddAccessControlAd, EditAccessControlAd, DeleteAccessControlAd },
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
        adName: ''
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
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        const res = await listAccessControlAds(params)
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
        adName: ''
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
