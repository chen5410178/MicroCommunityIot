<template>
  <div class="list-public-wechat-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.appId" placeholder="请输入APPID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>公众号管理</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="appId" label="APPID" min-width="200" show-overflow-tooltip />
        <el-table-column prop="appSecret" label="应用密钥" min-width="240" show-overflow-tooltip />
        <el-table-column prop="remarks" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
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
    <AddPublicWeChat :visible.sync="addVisible" @success="handleSuccess" />
    <EditPublicWeChat :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeletePublicWeChat :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listPublicWeChats } from '@/api/property/publicWeChatApi'
import AddPublicWeChat from '@/components/property/AddPublicWeChat.vue'
import EditPublicWeChat from '@/components/property/EditPublicWeChat.vue'
import DeletePublicWeChat from '@/components/property/DeletePublicWeChat.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListPublicWeChat',
  components: { AddPublicWeChat, EditPublicWeChat, DeletePublicWeChat },
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
        name: '',
        appId: ''
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
        const communityId = (getCurrentCommunity() || {}).communityId
        const params = {
          ...this.queryParams,
          communityId,
          weChatType: '1100'
        }
        const res = await listPublicWeChats(params)
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
      this.queryParams = { page: 1, row: 10, name: '', appId: '' }
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
    handleMenu(row) {
      this.$router.push(`/pages/property/wechatMenuManage?wechatId=${row.wechatId}`)
    },
    handleTemplate(row) {
      this.$router.push(`/pages/property/wechatSmsTemplateManage?wechatId=${row.wechatId}`)
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
