<template>
  <div class="list-visit-type">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.nameLike" placeholder="请输入类型名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.visitWay" placeholder="请选择通行方式" clearable style="width: 180px">
            <el-option v-for="item in visitWayList" :key="item.statusCd" :label="item.statusName" :value="item.statusCd" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.auditWay" placeholder="请选择审核方式" clearable style="width: 180px">
            <el-option v-for="item in auditWayList" :key="item.statusCd" :label="item.statusName" :value="item.statusCd" />
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
        <span>访客类型</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="typeId" label="类型ID" min-width="120" />
        <el-table-column prop="name" label="类型名称" min-width="120" />
        <el-table-column prop="visitWayName" label="通行方式" min-width="100" />
        <el-table-column prop="auditWayName" label="审核方式" min-width="100" />
        <el-table-column label="拜访天数" min-width="100">
          <template slot-scope="{ row }">
            {{ row.visitDay ? row.visitDay + '天' : '-' }}
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
    <AddVisitType :visible.sync="addVisible" @success="handleSuccess" />
    <EditVisitType :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteVisitType :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listVisitTypes } from '@/api/accessControl/accessControlApi'
import { listDicts } from '@/api/dev/devApi'
import AddVisitType from '@/components/accessControl/AddVisitType.vue'
import EditVisitType from '@/components/accessControl/EditVisitType.vue'
import DeleteVisitType from '@/components/accessControl/DeleteVisitType.vue'

export default {
  name: 'ListVisitType',
  components: { AddVisitType, EditVisitType, DeleteVisitType },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      visitWayList: [],
      auditWayList: [],
      queryParams: {
        page: 1,
        row: 10,
        nameLike: '',
        visitWay: '',
        auditWay: ''
      }
    }
  },
  mounted() {
    this.loadDicts()
    this.fetchList()
  },
  methods: {
    async loadDicts() {
      try {
        const [wayRes, auditRes] = await Promise.all([
          listDicts({ tableName: 'visit_type', columnName: 'visit_way' }),
          listDicts({ tableName: 'visit_type', columnName: 'audit_way' })
        ])
        this.visitWayList = wayRes.data || []
        this.auditWayList = auditRes.data || []
      } catch (e) {
        console.error('加载字典失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        const res = await listVisitTypes(params)
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
        nameLike: '',
        visitWay: '',
        auditWay: ''
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
