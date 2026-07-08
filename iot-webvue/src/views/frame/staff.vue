<template>
  <div class="list-staff-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入员工名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.tel" placeholder="请输入手机号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.staffId" placeholder="请输入员工ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>员工管理</span>
        <el-button type="primary" size="small" @click="handleAdd">添加员工</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="userId" label="员工编号" width="120" />
        <el-table-column prop="name" label="名称" min-width="100" />
        <el-table-column prop="tel" label="手机号" width="120" />
        <el-table-column prop="orgName" label="关联组织" min-width="150" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="sex" label="性别" width="60">
          <template slot-scope="{ row }">
            {{ row.sex === 0 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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

    <EditStaff :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteStaff :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ResetStaffPwd ref="resetPwdRef" @success="handleSuccess" />
  </div>
</template>

<script>
import { listStaffs } from '@/api/frame/staffApi'
import EditStaff from '@/components/frame/EditStaff.vue'
import DeleteStaff from '@/components/frame/DeleteStaff.vue'
import ResetStaffPwd from '@/components/frame/ResetStaffPwd.vue'

export default {
  name: 'StaffManage',
  components: { EditStaff, DeleteStaff, ResetStaffPwd },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        name: '',
        tel: '',
        staffId: ''
      },
      editVisible: false,
      delVisible: false,
      currentRow: null
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listStaffs({ ...this.queryParams })
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
      this.queryParams = { page: 1, row: 10, name: '', tel: '', staffId: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/frame/addStaff')
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleResetPwd(row) {
      this.$refs.resetPwdRef && this.$refs.resetPwdRef.open({
        userId: row.userId,
        username: row.name || row.username
      })
    },
    handleDetail(row) {
      this.$router.push('/pages/frame/staffDetail?staffId=' + row.userId)
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
