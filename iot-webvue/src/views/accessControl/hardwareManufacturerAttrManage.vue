<template>
  <div class="list-hardware-manufacturer-attr-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>门禁厂家规格</span>
        <div>
          <el-button size="small" @click="handleBack">返回</el-button>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="attrId" label="主键" min-width="120" />
        <el-table-column prop="specCd" label="规格" min-width="150" />
        <el-table-column prop="value" label="规格值" min-width="200" show-overflow-tooltip />
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
    <AddHardwareManufacturerAttr :visible.sync="addVisible" :hm-id="hmId" @success="handleSuccess" />
    <DeleteHardwareManufacturerAttr :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listHardwareManufacturerAttrs } from '@/api/accessControl/hardwareManufacturerApi'
import AddHardwareManufacturerAttr from '@/components/accessControl/AddHardwareManufacturerAttr.vue'
import DeleteHardwareManufacturerAttr from '@/components/accessControl/DeleteHardwareManufacturerAttr.vue'

export default {
  name: 'HardwareManufacturerAttrManage',
  components: { AddHardwareManufacturerAttr, DeleteHardwareManufacturerAttr },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      delVisible: false,
      currentRow: null,
      hmId: '',
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.hmId = this.$route.query.hmId || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listHardwareManufacturerAttrs({
          page: this.queryParams.page,
          row: this.queryParams.row,
          hmId: this.hmId
        })
        this.list = res.data || []
        this.total = res.records || 0
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
      this.$router.push('/pages/accessControl/hardwareManufacturerManage')
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
