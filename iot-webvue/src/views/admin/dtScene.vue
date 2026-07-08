<template>
  <div class="list-dt-scene">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="tree-card">
          <ul class="community-list">
            <li
              v-for="item in communitys"
              :key="item.communityId"
              :class="{ active: queryParams.communityId === item.communityId }"
              @click="switchCommunity(item.communityId)"
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
              <el-input v-model="queryParams.sceneName" placeholder="请输入场景名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 120px">
                <el-option label="启用中" value="Y" />
                <el-option label="停用中" value="N" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>场景信息</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="sceneId" label="编号" width="150" />
            <el-table-column prop="sceneName" label="场景名称" min-width="150" />
            <el-table-column label="状态" width="100" align="center">
              <template slot-scope="{ row }">
                <el-tag :type="row.state === 'Y' ? 'success' : 'info'" size="small">
                  {{ row.state === 'Y' ? '启用中' : '停用中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="seq" label="场景顺序" width="100" align="center" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleDesign(row)">设计</el-button>
                <el-button size="small" type="text" @click="handleScript(row)">脚本</el-button>
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

    <AddDtScene :visible.sync="addVisible" :community-id="queryParams.communityId" @success="fetchList" />
    <EditDtScene :visible.sync="editVisible" :edit-data="currentRow" @success="fetchList" />
    <DeleteDtScene :visible.sync="delVisible" :del-data="currentRow" @success="fetchList" />
  </div>
</template>

<script>
import { listDtScenes, listCommunitys } from '@/api/admin/dtSceneApi'
import AddDtScene from '@/components/admin/AddDtScene.vue'
import EditDtScene from '@/components/admin/EditDtScene.vue'
import DeleteDtScene from '@/components/admin/DeleteDtScene.vue'

export default {
  name: 'ListDtScene',
  components: { AddDtScene, EditDtScene, DeleteDtScene },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      communitys: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        sceneName: '',
        state: '',
        communityId: ''
      }
    }
  },
  mounted() {
    this.loadCommunitys()
  },
  methods: {
    async loadCommunitys() {
      try {
        const res = await listCommunitys({ page: 1, row: 100 })
        this.communitys = res.data || []
        if (this.communitys.length > 0) {
          this.switchCommunity(this.communitys[0].communityId)
        }
      } catch { /* ignore */ }
    },
    switchCommunity(communityId) {
      this.queryParams.communityId = communityId
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listDtScenes(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, sceneName: '', state: '', communityId: this.queryParams.communityId }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleDesign(row) {
      window.open('/3d.html#/pages/3d/dtSceneSetting?sceneId=' + row.sceneId + '&communityId=' + row.communityId)
    },
    handleScript(row) {
      this.$router.push('/pages/admin/dtSceneObjScript?sceneId=' + row.sceneId + '&communityId=' + row.communityId)
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
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
.tree-card { min-height: 400px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.community-list { list-style: none; padding: 0; margin: 0; }
.community-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 4px;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.community-list li:hover { background-color: #f5f7fa; }
.community-list li.active { background-color: #409eff; color: white; font-weight: 600; }
</style>
