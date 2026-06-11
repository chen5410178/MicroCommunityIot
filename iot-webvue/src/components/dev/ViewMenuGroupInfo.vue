<template>
  <div class="view-menu-group-info">
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="菜单组名称">
        <el-input v-model="queryParams.name" placeholder="请输入菜单组名称" clearable style="width: 220px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="gId" label="组ID" width="180" />
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="icon" label="图标" width="100" />
      <el-table-column prop="label" label="标签" width="80" />
      <el-table-column label="归属商户" width="100" align="center">
        <template slot-scope="{ row }">{{ storeTypeMap[row.storeType] || row.storeType }}</template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="150" />
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="handleSelect(row)">选择</el-button>
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
  </div>
</template>

<script>
import { listMenuGroups } from '@/api/dev/devApi'

export default {
  name: 'ViewMenuGroupInfo',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        name: ''
      },
      storeTypeMap: {
        '800900000001': '运营团队',
        '800900000002': '代理商',
        '800900000003': '物业',
        '800900000004': '物流公司',
        '800900000005': '商家',
        '800900000000': '开发团队',
        '800900000006': '跑腿'
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
        const res = await listMenuGroups(this.queryParams)
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
    handleSelect(row) {
      this.$emit('notify', {
        flowComponent: 'viewMenuGroupInfo',
        gId: row.gId,
        name: row.name,
        icon: row.icon,
        label: row.label,
        seq: row.seq,
        description: row.description
      })
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
