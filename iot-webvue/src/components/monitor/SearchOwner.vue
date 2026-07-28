<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择业主"
    width="800px"
    @close="handleClose"
  >
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入业主名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.link" placeholder="请输入联系方式" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-table
      :data="list"
      v-loading="loading"
      border
      stripe
      highlight-current-row
      style="margin-top: 12px"
      @row-click="handleRowClick"
    >
      <el-table-column prop="memberId" label="业主ID" min-width="120" />
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="link" label="联系方式" min-width="130" />
      <el-table-column prop="idCard" label="身份证" min-width="160" />
      <el-table-column label="操作" width="80" align="center">
        <template slot-scope="{ row }">
          <el-button
            size="mini"
            type="primary"
            @click="handleSelect(row)"
          >选中</el-button>
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
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { queryOwners } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'SearchOwner',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        name: '',
        link: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await queryOwners({ ...this.queryParams, communityId, ownerTypeCd: '1001' })
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
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleRowClick(row) {
      this.$emit('selected', row)
      this.handleClose()
    },
    handleSelect(row) {
      this.$emit('selected', row)
      this.handleClose()
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 0; }
.pagination-container { margin-top: 12px; text-align: right; }
</style>
