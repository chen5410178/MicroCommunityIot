<template>
  <div class="lift-warn">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="sidebar-card">
          <div slot="header" class="card-header">
            <span>告警类型</span>
          </div>
          <ul class="warn-type-list">
            <li
              v-for="(item, index) in warnTypes"
              :key="index"
              :class="{ active: queryParams.warnType === item.statusCd }"
              @click="handleWarnTypeClick(item)"
            >
              {{ item.name }}
            </li>
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
              <el-input v-model="queryParams.cameraName" placeholder="请输入相机名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.cameraCode" placeholder="请输入相机编号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.liftName" placeholder="请输入电梯名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>电梯告警</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="cameraName" label="相机名称" min-width="150" />
            <el-table-column prop="cameraCode" label="相机编号" min-width="150" />
            <el-table-column prop="liftName" label="电梯名称" min-width="150" />
            <el-table-column prop="warnTypeName" label="告警类型" min-width="120" />
            <el-table-column prop="warnImg" label="告警图片" min-width="120" />
            <el-table-column prop="enterPeopleNum" label="进入电梯人数" min-width="120" align="center" />
            <el-table-column prop="leavePeopleNum" label="离开电梯人数" min-width="120" align="center" />
            <el-table-column prop="currentPeopleNum" label="当前电梯人数" min-width="120" align="center" />
            <el-table-column prop="alarmInPolarity" label="开门状态" min-width="100" align="center" />
            <el-table-column label="非机动车" min-width="100" align="center">
              <template slot-scope="{ row }">{{ row.haveBicycle === 'Y' ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column prop="currentSpeed" label="当前速度" min-width="100" align="center" />
            <el-table-column prop="warnTime" label="告警时间" min-width="160" />
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
  </div>
</template>

<script>
import { listLiftWarns } from '@/api/lift/liftApi'
import { listDicts } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LiftWarn',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      warnTypes: [],
      queryParams: {
        page: 1,
        row: 10,
        cameraName: '',
        cameraCode: '',
        liftName: '',
        warnType: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.loadWarnTypes()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        const params = { ...this.queryParams, communityId }
        const res = await listLiftWarns(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async loadWarnTypes() {
      try {
        const res = await listDicts({ tableName: 'lift_warn', columnName: 'warn_type' })
        const dictList = res.data || []
        this.warnTypes = [{ name: '全部', statusCd: '' }, ...dictList]
      } catch (e) {
        console.error('获取告警类型失败', e)
      }
    },
    handleWarnTypeClick(item) {
      this.queryParams.warnType = item.statusCd
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        cameraName: '',
        cameraCode: '',
        liftName: '',
        warnType: this.queryParams.warnType
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
    }
  }
}
</script>

<style scoped>
.sidebar-card { margin-bottom: 16px; }
.sidebar-card .card-header { display: flex; justify-content: center; }
.warn-type-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.warn-type-list li {
  padding: 10px 16px;
  cursor: pointer;
  text-align: center;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
  font-size: 14px;
}
.warn-type-list li:hover { color: #409eff; background-color: #ecf5ff; }
.warn-type-list li.active { color: #409eff; background-color: #ecf5ff; font-weight: bold; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
