<template>
  <div class="list-menu-group-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
        <el-button type="text" @click="showMore = !showMore">{{ showMore ? '隐藏' : '更多' }}</el-button>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.gId" placeholder="请输入菜单组ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入菜单组名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.icon" placeholder="请输入图标" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <template v-if="showMore">
          <el-form-item>
            <el-select v-model="queryParams.label" placeholder="请选择标签" clearable style="width: 180px">
              <el-option label="HOT" value="HOT" />
              <el-option label="TOP" value="TOP" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryParams.storeType" placeholder="请选择归属商户" clearable style="width: 180px">
              <el-option label="运营团队" value="800900000001" />
              <el-option label="代理商" value="800900000002" />
              <el-option label="物业" value="800900000003" />
              <el-option label="物流公司" value="800900000004" />
              <el-option label="商家" value="800900000005" />
              <el-option label="开发团队" value="800900000000" />
              <el-option label="跑腿" value="800900000006" />
            </el-select>
          </el-form-item>
        </template>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>菜单组信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="gId" label="菜单组Id" width="180" />
        <el-table-column prop="name" label="菜单组名称" min-width="150" />
        <el-table-column label="菜单组类型" width="120" align="center">
          <template slot-scope="{ row }">{{ row.groupType === 'P_WEB' ? 'web菜单' : 'App菜单' }}</template>
        </el-table-column>
        <el-table-column label="归属商户" width="120" align="center">
          <template slot-scope="{ row }">{{ storeTypeMap[row.storeType] || row.storeType }}</template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="120" />
        <el-table-column prop="label" label="标签" width="80" />
        <el-table-column prop="seq" label="序列" width="80" />
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
    </el-card>
    <AddMenuGroup :visible.sync="addVisible" @success="handleSuccess" />
    <EditMenuGroup :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMenuGroup :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMenuGroups } from '@/api/dev/devApi'
import AddMenuGroup from '@/components/dev/AddMenuGroup.vue'
import EditMenuGroup from '@/components/dev/EditMenuGroup.vue'
import DeleteMenuGroup from '@/components/dev/DeleteMenuGroup.vue'

export default {
  name: 'ListMenuGroupManage',
  components: { AddMenuGroup, EditMenuGroup, DeleteMenuGroup },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showMore: false,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        gId: '',
        name: '',
        icon: '',
        label: '',
        storeType: ''
      },
      storeTypeMap: {
        '800900000001': '运营团队',
        '800900000002': '代理商',
        '800900000003': '物业',
        '800900000004': '物流公司',
        '800900000005': '商家',
        '800900000000': '开发团队',
        '800900000006': '跑腿'
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
        const res = await listMenuGroups(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, gId: '', name: '', icon: '', label: '', storeType: '' }
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
