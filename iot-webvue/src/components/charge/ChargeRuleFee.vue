<template>
  <div>
    <div style="margin-bottom: 16px;">
      <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
      <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="crfId" label="编号" width="100" align="center" />
      <el-table-column prop="maxEnergyPrice" label="最大功率(W)" min-width="120" align="center" />
      <el-table-column prop="durationPrice" label="电费单价" min-width="120" align="center" />
      <el-table-column prop="servicePrice" label="服务费单价" min-width="120" align="center" />
      <el-table-column prop="remark" label="备注" min-width="150" align="center" />
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
    <AddChargeRuleFee :visible.sync="addVisible" :rule-id="ruleId" :charge-type="chargeType" @success="handleSuccess" />
    <EditChargeRuleFee :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteChargeRuleFee :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listChargeRuleFee } from '@/api/charge/chargeApi'
import AddChargeRuleFee from '@/components/charge/AddChargeRuleFee.vue'
import EditChargeRuleFee from '@/components/charge/EditChargeRuleFee.vue'
import DeleteChargeRuleFee from '@/components/charge/DeleteChargeRuleFee.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeRuleFee',
  components: { AddChargeRuleFee, EditChargeRuleFee, DeleteChargeRuleFee },
  props: {
    ruleId: { type: String, default: '' },
    chargeType: { type: String, default: '' }
  },
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
        row: 10
      }
    }
  },
  watch: {
    ruleId(val) {
      if (val) this.fetchList()
    }
  },
  mounted() {
    if (this.ruleId) this.fetchList()
  },
  methods: {
    async fetchList() {
      if (!this.ruleId) return
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeRuleFee({ ...this.queryParams, ruleId: this.ruleId, chargeType: this.chargeType, communityId })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryParams.page = 1
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
