<template>
  <div class="list-lift-machine-factory-spec-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>电梯厂家</span>
        <div>
          <el-button size="small" @click="handleBack">返回</el-button>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="specId" label="规格ID" min-width="150" />
        <el-table-column prop="specName" label="规格名称" min-width="150" />
        <el-table-column label="描述" width="100" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
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
    <AddLiftMachineFactorySpec :visible.sync="addVisible" :factory-id="factoryId" @success="handleSuccess" />
    <DeleteLiftMachineFactorySpec :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLiftMachineFactorySpecs } from '@/api/lift/liftApi'
import AddLiftMachineFactorySpec from '@/components/lift/AddLiftMachineFactorySpec.vue'
import DeleteLiftMachineFactorySpec from '@/components/lift/DeleteLiftMachineFactorySpec.vue'

export default {
  name: 'LiftMachineFactorySpecManage',
  components: { AddLiftMachineFactorySpec, DeleteLiftMachineFactorySpec },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      delVisible: false,
      currentRow: null,
      factoryId: '',
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.factoryId = this.$route.query.factoryId || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listLiftMachineFactorySpecs({
          page: this.queryParams.page,
          row: this.queryParams.row,
          factoryId: this.factoryId
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
    handleBack() {
      this.$router.push('/pages/lift/liftMachineFactoryManage')
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
