<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择人员"
    width="800px"
    append-to-body
  >
    <el-form :inline="true" :model="queryParams">
      <el-form-item>
        <el-input v-model="queryParams.roomName" placeholder="楼栋-单元-房屋" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.name" placeholder="请输入业主名称" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border stripe max-height="400">
      <el-table-column prop="name" label="名称" min-width="100" />
      <el-table-column label="性别" width="70" align="center">
        <template slot-scope="{ row }">{{ row.sex == 0 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="idCard" label="身份证" min-width="160" />
      <el-table-column prop="link" label="联系方式" min-width="120" />
      <el-table-column prop="userName" label="创建员工" min-width="100" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="primary" @click="chooseOwner(row)">选择</el-button>
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
  </el-dialog>
</template>

<script>
import { queryOwners } from '@/api/property/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChooseOwner',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      list: [],
      total: 0,
      queryParams: { page: 1, row: 10, name: '', roomName: '' }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.queryParams = { page: 1, row: 10, name: '', roomName: '' }
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await queryOwners({
          ...this.queryParams,
          communityId: community ? community.communityId : ''
        })
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
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    chooseOwner(owner) {
      this.$emit('confirm', owner)
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
