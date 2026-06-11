<template>
  <div class="list-meter-type-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>抄表类型</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="typeId" label="抄表类型ID" min-width="150" />
        <el-table-column prop="typeName" label="名称" min-width="120" />
        <el-table-column prop="price" label="单价" min-width="100" />
        <el-table-column prop="remark" label="说明" min-width="150" show-overflow-tooltip />
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
      <div class="hint-text">如果一个房屋有多个电表水表可以在这里新建多个 比如电表1 电表2</div>
    </el-card>
    <AddMeterType :visible.sync="addVisible" @success="handleSuccess" />
    <EditMeterType :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMeterType :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMeterTypes } from '@/api/meter/meterApi'
import AddMeterType from '@/components/meter/AddMeterType.vue'
import EditMeterType from '@/components/meter/EditMeterType.vue'
import DeleteMeterType from '@/components/meter/DeleteMeterType.vue'

export default {
  name: 'MeterTypeManage',
  components: { AddMeterType, EditMeterType, DeleteMeterType },
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
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMeterTypes({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.hint-text { margin-top: 16px; color: #909399; font-size: 13px; text-align: center; }
</style>
