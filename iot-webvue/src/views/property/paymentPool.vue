<template>
  <div class="list-payment-pool">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.paymentName" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.paymentType" placeholder="请选择支付厂家" clearable style="width: 180px">
            <el-option
              v-for="item in paymentTypes"
              :key="item.paymentType"
              :label="item.name"
              :value="item.paymentType"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 140px">
            <el-option value="Y" label="启用" />
            <el-option value="N" label="停用" />
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
        <span>支付配置</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="paymentName" label="名称" min-width="150" />
        <el-table-column prop="paymentTypeName" label="支付厂家" min-width="120" />
        <el-table-column label="支付范围" min-width="120">
          <template slot-scope="{ row }">
            <span v-if="row.payType === '1001'">小区费用</span>
            <span v-else-if="row.payType === '2002'">临时停车费</span>
            <span v-else>指定费用项</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="{ row }">
            {{ row.state === 'Y' ? '启用' : '停用' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column prop="remark" label="使用说明" min-width="200" show-overflow-tooltip />
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

    <AddPaymentPool :visible.sync="addVisible" @success="handleSuccess" />
    <EditPaymentPool :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeletePaymentPool :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listPaymentPool, listPaymentAdapt } from '@/api/property/paymentPoolApi'
import AddPaymentPool from '@/components/property/AddPaymentPool.vue'
import EditPaymentPool from '@/components/property/EditPaymentPool.vue'
import DeletePaymentPool from '@/components/property/DeletePaymentPool.vue'

export default {
  name: 'PaymentPool',
  components: { AddPaymentPool, EditPaymentPool, DeletePaymentPool },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      paymentTypes: [],
      queryParams: {
        page: 1,
        row: 10,
        paymentName: '',
        paymentType: '',
        state: ''
      },
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null
    }
  },
  mounted() {
    this.loadPaymentTypes()
    this.fetchList()
  },
  methods: {
    async loadPaymentTypes() {
      try {
        const res = await listPaymentAdapt({ page: 1, row: 100 })
        this.paymentTypes = res.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        const res = await listPaymentPool(params)
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) {
        this.$message.error(e.msg || '查询失败')
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
        paymentName: '',
        paymentType: '',
        state: ''
      }
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
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = row
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = row
      this.delVisible = true
    },
    handleSuccess() {
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 15px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 15px; text-align: right; }
</style>
