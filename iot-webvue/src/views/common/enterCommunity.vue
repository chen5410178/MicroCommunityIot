<template>
  <div class="enter-community">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="小区名称">
          <el-input v-model="queryParams.communityName" placeholder="请输入小区名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>我的小区列表</span>
        <div>
          <el-button type="primary" size="small" @click="$refs.addRef.open()">申请入驻</el-button>
          <el-button size="small" @click="handleVisual">可视化</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="name" label="小区名称" min-width="150" />
        <el-table-column prop="communityId" label="小区编码" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="tel" label="联系方式" width="120" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleExit(row)">退出小区</el-button>
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="total > 0"
        style="margin-top: 15px; text-align: right"
        :current-page="queryParams.page"
        :page-sizes="[10, 20, 50]"
        :page-size="queryParams.row"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <AddEnterCommunity ref="addRef" :member-id="memberId" @success="fetchList" />
    <DeleteEnterCommunity ref="delRef" :member-id="memberId" @success="fetchList" />
    <EditCommunity ref="editRef" :visible.sync="editVisible" :edit-data="editData" @success="fetchList" />
  </div>
</template>

<script>
import { listMyEnteredCommunitys } from '@/api/community/communityApi'
import AddEnterCommunity from '@/components/common/AddEnterCommunity.vue'
import DeleteEnterCommunity from '@/components/common/DeleteEnterCommunity.vue'
import EditCommunity from '@/components/common/EditCommunity.vue'

export default {
  name: 'EnterCommunity',
  components: { AddEnterCommunity, DeleteEnterCommunity, EditCommunity },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        communityName: '',
        page: 1,
        row: 10
      },
      editVisible: false,
      editData: null
    }
  },
  computed: {
    memberId() {
      const userInfo = this.$store.state.user.userInfo
      return userInfo ? userInfo.userId : ''
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.queryParams.page = 1
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listMyEnteredCommunitys(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } catch { /* ignore */ } finally {
        this.loading = false
      }
    },
    handleVisual() {
      window.open('/3d.html#/pages/3d/community3dMap')
    },
    handleExit(row) {
      this.$refs.delRef.open(row)
    },
    handleEdit(row) {
      this.editData = row
      this.editVisible = true
    }
  }
}
</script>
