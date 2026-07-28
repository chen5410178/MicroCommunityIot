<template>
  <div class="list-lamp-machine-factory-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.factoryId" placeholder="请输入厂家ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.factoryName" placeholder="请输入厂家名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.beanImpl" placeholder="请输入厂家处理类" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>路灯厂家</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="factoryId" label="厂家ID" width="150" />
        <el-table-column prop="factoryName" label="厂家名称" min-width="150" />
        <el-table-column prop="beanImpl" label="厂家处理类" min-width="200" />
        <el-table-column label="备注" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewRemark(row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleSpec(row)">厂家规格</el-button>
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
    <AddLampMachineFactory :visible.sync="addVisible" @success="handleSuccess" />
    <EditLampMachineFactory :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteLampMachineFactory :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLampMachineFactory } from '@/api/lamp/lampApi'
import AddLampMachineFactory from '@/components/lamp/AddLampMachineFactory.vue'
import EditLampMachineFactory from '@/components/lamp/EditLampMachineFactory.vue'
import DeleteLampMachineFactory from '@/components/lamp/DeleteLampMachineFactory.vue'

export default {
  name: 'ListLampMachineFactoryManage',
  components: { AddLampMachineFactory, EditLampMachineFactory, DeleteLampMachineFactory },
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
        factoryId: '',
        factoryName: '',
        beanImpl: ''
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
        const res = await listLampMachineFactory(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, factoryId: '', factoryName: '', beanImpl: '' }
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
    handleSpec(row) {
      this.$router.push(`/pages/lamp/listLampMachineFactorySpecManage?factoryId=${row.factoryId}`)
    },
    handleViewRemark(row) {
      this.$alert(row.remark || '无', '备注', { confirmButtonText: '确定' })
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
