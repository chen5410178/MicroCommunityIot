<template>
  <div class="list-event-template-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.eventWay" placeholder="请选择通知方式" clearable style="width: 200px">
            <el-option v-for="item in eventWayList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
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
        <span>事件模板</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="templateId" label="编号" min-width="150" />
        <el-table-column prop="templateName" label="模板名称" min-width="150" />
        <el-table-column prop="eventWayName" label="通知方式" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="备注" width="100" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
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
    <AddEventTemplate :visible.sync="addVisible" @success="handleSuccess" />
    <EditEventTemplate :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteEventTemplate :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listEventTemplates } from '@/api/event/eventApi'
import { listDicts } from '@/api/property/propertyApi'
import AddEventTemplate from '@/components/event/AddEventTemplate.vue'
import EditEventTemplate from '@/components/event/EditEventTemplate.vue'
import DeleteEventTemplate from '@/components/event/DeleteEventTemplate.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EventTemplateManage',
  components: { AddEventTemplate, EditEventTemplate, DeleteEventTemplate },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      eventWayList: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        templateName: '',
        eventWay: ''
      }
    }
  },
  mounted() {
    this.loadEventWayDict()
    this.fetchList()
  },
  methods: {
    async loadEventWayDict() {
      try {
        const res = await listDicts({ tableName: 'event_template', columnName: 'event_way' })
        this.eventWayList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        const res = await listEventTemplates(params)
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
      this.queryParams = { page: 1, row: 10, templateName: '', eventWay: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = { ...row, communityId: (getCurrentCommunity() || {}).communityId }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row, communityId: (getCurrentCommunity() || {}).communityId }
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
