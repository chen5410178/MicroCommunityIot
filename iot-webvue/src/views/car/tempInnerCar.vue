<template>
  <div class="temp-inner-car-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <div class="left-nav">
          <ul class="nav-list">
            <li
              v-for="(item, index) in configs"
              :key="item.configId || 'all'"
              :class="{ 'nav-item-active': currentConfigId === item.configId }"
              class="nav-item"
              @click="switchConfig(item)"
            >
              {{ item.feeName || '全部' }}
            </li>
          </ul>
        </div>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.carNum" placeholder="请输入车牌号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>临时内部车</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="ticId" label="编号" min-width="120" />
            <el-table-column prop="carNum" label="车牌号" min-width="120" />
            <el-table-column prop="paNum" label="停车场" min-width="120" />
            <el-table-column prop="feeName" label="收费名称" min-width="140" />
            <el-table-column prop="startTime" label="开始时间" min-width="160" />
            <el-table-column prop="endTime" label="结束时间" min-width="160" />
            <el-table-column prop="remark" label="说明" min-width="140" />
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
      </el-col>
    </el-row>
    <AddTempInnerCar :visible.sync="addVisible" :config-data="currentConfigData" @success="handleSuccess" />
    <EditTempInnerCar :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteTempInnerCar :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listTempInnerCars, listTempCarFeeConfigs } from '@/api/car/carApi'
import AddTempInnerCar from '@/components/car/AddTempInnerCar.vue'
import EditTempInnerCar from '@/components/car/EditTempInnerCar.vue'
import DeleteTempInnerCar from '@/components/car/DeleteTempInnerCar.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListTempInnerCar',
  components: { AddTempInnerCar, EditTempInnerCar, DeleteTempInnerCar },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      configs: [],
      currentConfigId: '',
      currentConfigData: null,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        carNum: ''
      }
    }
  },
  mounted() {
    this.loadConfigs()
    this.fetchList()
  },
  methods: {
    async loadConfigs() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listTempCarFeeConfigs({ page: 1, row: 50, carType: '9902', communityId })
        this.configs = [{ configId: '', feeName: '全部' }]
        if (res.data) {
          res.data.forEach(item => {
            this.configs.push(item)
          })
        }
      } catch (e) {
        console.error('加载收费名称失败', e)
      }
    },
    switchConfig(item) {
      this.currentConfigId = item.configId
      this.currentConfigData = item
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listTempInnerCars({
          ...this.queryParams,
          configId: this.currentConfigId,
          communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleAdd() {
      if (!this.currentConfigId) {
        this.$message.warning('请先选择左侧的临时车费')
        return
      }
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
.left-nav {
  background: #fff;
  border-radius: 4px;
  padding: 8px 0;
}
.nav-list { list-style: none; margin: 0; padding: 0; }
.nav-item {
  padding: 14px 20px;
  cursor: pointer;
  border-radius: 4px;
  margin: 0 8px 5px;
  text-align: center;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}
.nav-item:last-child { border-bottom: none; }
.nav-item:hover { background-color: #f5f7fa; }
.nav-item-active { background-color: #409eff; color: white; font-weight: bold; }
</style>
