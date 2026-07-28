<template>
  <div class="car-black-white-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <div class="left-nav">
          <ul class="nav-list">
            <li
              v-for="(item, index) in blackWhites"
              :key="index"
              :class="{ 'nav-item-active': queryParams.blackWhite === item.statusCd }"
              class="nav-item"
              @click="swatchBlackWhiteType(item)"
            >
              {{ item.name }}
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
            <span>黑白名单</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="bwId" label="名单ID" min-width="120" />
            <el-table-column prop="blackWhiteName" label="名单类型" min-width="100" align="center" />
            <el-table-column prop="paNum" label="停车场" min-width="120" />
            <el-table-column prop="carNum" label="车牌号" min-width="120" />
            <el-table-column prop="startTime" label="开始时间" min-width="160" />
            <el-table-column prop="endTime" label="结束时间" min-width="160" />
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
    <AddCarBlackWhite :visible.sync="addVisible" @success="handleSuccess" />
    <EditCarBlackWhite :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteCarBlackWhite :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listCarBlackWhites } from '@/api/car/carApi'
import AddCarBlackWhite from '@/components/car/AddCarBlackWhite.vue'
import EditCarBlackWhite from '@/components/car/EditCarBlackWhite.vue'
import DeleteCarBlackWhite from '@/components/car/DeleteCarBlackWhite.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'CarBlackWhite',
  components: { AddCarBlackWhite, EditCarBlackWhite, DeleteCarBlackWhite },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      blackWhites: [
        { name: '全部', statusCd: '' },
        { name: '白名单', statusCd: '2222' },
        { name: '黑名单', statusCd: '1111' }
      ],
      queryParams: {
        page: 1,
        row: 10,
        carNum: '',
        blackWhite: ''
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarBlackWhites({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    swatchBlackWhiteType(item) {
      this.queryParams.blackWhite = item.statusCd
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
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
