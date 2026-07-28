<template>
  <div class="list-machine-location">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.locationName" placeholder="请输入位置名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.locationType" placeholder="请选择位置类型" clearable style="width: 180px">
            <el-option label="小区" value="1000" />
            <el-option label="楼栋" value="6000" />
            <el-option label="单元" value="2000" />
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
        <span>设备位置</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="locationName" label="位置名称" min-width="150" />
        <el-table-column prop="locationTypeName" label="位置类型" width="120" />
        <el-table-column prop="locationObjName" label="位置对象" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
    <AddMachineLocation :visible.sync="addVisible" @success="handleSuccess" />
    <EditMachineLocation :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMachineLocation :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMachineLocations } from '@/api/machine/machineApi'
import AddMachineLocation from '@/components/machine/AddMachineLocation.vue'
import EditMachineLocation from '@/components/machine/EditMachineLocation.vue'
import DeleteMachineLocation from '@/components/machine/DeleteMachineLocation.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListMachineLocation',
  components: { AddMachineLocation, EditMachineLocation, DeleteMachineLocation },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: { page: 1, row: 10, locationName: '', locationType: '' }
    }
  },
  mounted() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const params = { ...this.queryParams, communityId: communityInfo.communityId }
        const res = await listMachineLocations(params)
        this.list = res.data || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleSearch() { this.queryParams.page = 1; this.fetchList() },
    handleReset() { this.queryParams = { page: 1, row: 10, locationName: '', locationType: '' }; this.fetchList() },
    handleAdd() { this.addVisible = true },
    handleEdit(row) { this.currentRow = { ...row }; this.editVisible = true },
    handleDelete(row) { this.currentRow = { ...row }; this.delVisible = true },
    handleSuccess() { this.fetchList() },
    handleSizeChange(val) { this.queryParams.row = val; this.fetchList() },
    handlePageChange(val) { this.queryParams.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
