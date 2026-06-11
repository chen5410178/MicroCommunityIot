<template>
  <div class="car-month-card-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="tree-card">
          <div slot="header" class="card-header">
            <span>停车场</span>
          </div>
          <ul class="parking-tree">
            <li
              v-for="item in parkingAreas"
              :key="item.paId"
              :class="{ 'tree-item': true, 'active': queryParams.paId === item.paId }"
              @click="handleAreaClick(item)"
            >
              {{ item.num }}
            </li>
            <li v-if="parkingAreas.length === 0" class="tree-item-empty">暂无停车场</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.cardName" placeholder="请输入月卡名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>停车月卡</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <div class="table-wrapper">
          <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
            <el-table-column prop="cardName" label="名称" min-width="120" />
            <el-table-column prop="areaNum" label="停车场" min-width="120" />
            <el-table-column prop="cardMonth" label="月数" min-width="80" align="center" />
            <el-table-column prop="cardPrice" label="月价" min-width="100" align="center" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" align="center" />
            <el-table-column label="备注" min-width="150" align="center">
              <template slot-scope="{ row }">
                <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
                  <el-button slot="reference" size="small" type="text">查看</el-button>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>
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
      </el-col>
    </el-row>
    <AddCarMonthCard :visible.sync="addVisible" @success="handleSuccess" />
    <EditCarMonthCard :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteCarMonthCard :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listCarMonthCards, listParkingAreas } from '@/api/car/carApi'
import AddCarMonthCard from '@/components/car/AddCarMonthCard.vue'
import EditCarMonthCard from '@/components/car/EditCarMonthCard.vue'
import DeleteCarMonthCard from '@/components/car/DeleteCarMonthCard.vue'

export default {
  name: 'CarMonthCard',
  components: { AddCarMonthCard, EditCarMonthCard, DeleteCarMonthCard },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      parkingAreas: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        cardName: '',
        paId: ''
      }
    }
  },
  mounted() {
    this.fetchParkingAreas()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listCarMonthCards({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchParkingAreas() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
      } catch (e) { /* ignore */ }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, cardName: '', paId: '' }
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
    handleAreaClick(item) {
      if (this.queryParams.paId === item.paId) {
        this.queryParams.paId = ''
      } else {
        this.queryParams.paId = item.paId
      }
      this.handleSearch()
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
.tree-card { height: 100%; }
.table-card { margin-top: 16px; }
.search-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.table-wrapper { overflow-x: auto; }
.pagination-container { margin-top: 16px; text-align: right; }
.parking-tree { list-style: none; margin: 0; padding: 0; }
.tree-item { padding: 10px 15px; cursor: pointer; border-bottom: 1px solid #eee; font-size: 14px; color: #606266; }
.tree-item:hover { background-color: #f5f7fa; }
.tree-item.active { background-color: #ecf5ff; color: #409eff; font-weight: bold; }
.tree-item-empty { padding: 20px; text-align: center; color: #c0c4cc; font-size: 13px; }
</style>
