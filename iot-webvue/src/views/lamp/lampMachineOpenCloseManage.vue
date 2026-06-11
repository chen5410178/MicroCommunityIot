<template>
  <div class="lamp-machine-open-close-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="路灯编号">
          <el-input v-model="queryParams.machineCode" placeholder="请输入路灯编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="路灯名称">
          <el-input v-model="queryParams.machineName" placeholder="请输入路灯名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.typeCd" placeholder="请选择类型" clearable style="width: 180px">
            <el-option
              v-for="item in typeList"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
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
        <span>开关灯时间</span>
        <div>
          <el-button type="primary" size="small" @click="handleAdd">设置</el-button>
          <el-button size="small" @click="handleBack">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineCode" label="路灯编号" min-width="120" />
        <el-table-column prop="machineName" label="路灯名称" min-width="120" />
        <el-table-column prop="typeName" label="类型" min-width="100" />
        <el-table-column prop="hours" label="小时" width="80" align="center" />
        <el-table-column prop="min" label="分钟" width="80" align="center" />
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
    <AddLampMachineOpenClose :visible.sync="addVisible" @success="handleSuccess" />
  </div>
</template>

<script>
import { listLampMachineOpenClose } from '@/api/lamp/lampApi'
import { listDicts } from '@/api/dev/devApi'
import AddLampMachineOpenClose from '@/components/lamp/AddLampMachineOpenClose.vue'

export default {
  name: 'LampMachineOpenCloseManage',
  components: { AddLampMachineOpenClose },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      typeList: [],
      addVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        machineName: '',
        typeCd: ''
      }
    }
  },
  mounted() {
    this.loadTypes()
    this.fetchList()
  },
  methods: {
    async loadTypes() {
      try {
        const res = await listDicts({ tableName: 'lamp_machine_operation', columnName: 'type' })
        if (res.data) {
          this.typeList = res.data
        }
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listLampMachineOpenClose({ ...this.queryParams, communityId })
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
      this.queryParams = { page: 1, row: 10, machineCode: '', machineName: '', typeCd: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
