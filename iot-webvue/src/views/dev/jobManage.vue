<template>
  <div class="list-job-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.taskId" placeholder="请输入任务ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>定时任务</span>
        <el-button type="primary" size="small" @click="handleAdd">添加任务</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="taskCron" label="运行时间" min-width="120" />
        <el-table-column prop="templateName" label="模板名称" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="启停状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state === '002'" type="success" size="small">运行中</el-tag>
            <el-tag v-else-if="row.state === '001'" type="info" size="small">停止</el-tag>
            <el-tag v-else type="warning" size="small">{{ row.state }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button v-if="row.state === '001'" size="small" type="text" @click="handleStart(row)">启动</el-button>
            <el-button v-if="row.state === '002'" size="small" type="text" @click="handleStop(row)">停止</el-button>
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
    <AddJob :visible.sync="addVisible" @success="handleSuccess" />
    <EditJob :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteJob :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listTasks, startTask, stopTask } from '@/api/dev/devApi'
import AddJob from '@/components/dev/AddJob.vue'
import EditJob from '@/components/dev/EditJob.vue'
import DeleteJob from '@/components/dev/DeleteJob.vue'

export default {
  name: 'ListJobManage',
  components: { AddJob, EditJob, DeleteJob },
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
        taskId: '',
        taskName: '',
        templateName: ''
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
        const res = await listTasks(this.queryParams)
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
    handleReset() {
      this.queryParams = { page: 1, row: 10, taskId: '', taskName: '', templateName: '' }
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
    async handleStart(row) {
      try {
        await startTask({ taskId: row.taskId })
        this.$message.success('启动成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '启动失败')
      }
    },
    async handleStop(row) {
      try {
        await stopTask({ taskId: row.taskId })
        this.$message.success('停止成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '停止失败')
      }
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
