<template>
  <div class="list-charge-machine-factory-spec-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <div>
          <el-button type="default" size="small" @click="handleBack">返回</el-button>
          <span style="margin-left: 16px; font-weight: bold;">充电规格</span>
        </div>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="specId" label="规格ID" width="150" />
        <el-table-column prop="specName" label="规格名称" min-width="150" />
        <el-table-column label="描述" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewRemark(row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
    <AddChargeMachineFactorySpec :visible.sync="addVisible" :factory-id="factoryId" @success="handleSuccess" />
    <DeleteChargeMachineFactorySpec :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listChargeMachineFactorySpec } from '@/api/charge/chargeApi'
import AddChargeMachineFactorySpec from '@/components/charge/AddChargeMachineFactorySpec.vue'
import DeleteChargeMachineFactorySpec from '@/components/charge/DeleteChargeMachineFactorySpec.vue'

export default {
  name: 'ChargeMachineFactorySpecManage',
  components: { AddChargeMachineFactorySpec, DeleteChargeMachineFactorySpec },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      factoryId: '',
      addVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        factoryId: ''
      }
    }
  },
  mounted() {
    this.factoryId = this.$route.query.factoryId || ''
    this.queryParams.factoryId = this.factoryId
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listChargeMachineFactorySpec(this.queryParams)
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
    handleViewRemark(row) {
      this.$alert(row.remark || '无', '描述', { confirmButtonText: '确定' })
    },
    handleBack() {
      this.$router.back()
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
