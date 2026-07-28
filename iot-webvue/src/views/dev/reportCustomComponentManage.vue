<template>
  <div class="list-report-custom-component-rel-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>{{ title }}组件</span>
        <div>
          <el-button size="small" @click="handleGoBack">返回</el-button>
          <el-button type="primary" size="small" @click="handleAdd">关联组件</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="relId" label="关系编号" width="150" />
        <el-table-column prop="componentName" label="组件" min-width="150" />
        <el-table-column prop="customId" label="报表编号" width="150" />
        <el-table-column prop="seq" label="组件序号" width="100" />
        <el-table-column prop="createTime" label="关联时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right" align="center">
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
    <AddReportCustomComponentRel :visible.sync="addVisible" :custom-id="customId" @success="handleSuccess" />
    <DeleteReportCustomComponentRel :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listReportCustomComponentRels } from '@/api/dev/devApi'
import AddReportCustomComponentRel from '@/components/dev/AddReportCustomComponentRel.vue'
import DeleteReportCustomComponentRel from '@/components/dev/DeleteReportCustomComponentRel.vue'

export default {
  name: 'ListReportCustomComponentRelManage',
  components: { AddReportCustomComponentRel, DeleteReportCustomComponentRel },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      delVisible: false,
      currentRow: null,
      customId: '',
      title: '',
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.customId = this.$route.query.customId || ''
    this.title = this.$route.query.title || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listReportCustomComponentRels({
          ...this.queryParams,
          customId: this.customId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleAdd() {
      this.addVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleGoBack() {
      this.$router.go(-1)
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
