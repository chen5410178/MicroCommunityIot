<template>
  <div class="list-dt-scene-obj-script">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="tree-card">
          <div slot="header" class="card-header">
            <span>场景列表</span>
          </div>
          <ul class="scene-list">
            <li
              v-for="item in scenes"
              :key="item.sceneId"
              :class="{ active: queryParams.sceneId === item.sceneId }"
              @click="switchScene(item.sceneId)"
            >
              {{ item.sceneName }}
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
            <el-form-item label="对象名称">
              <el-input v-model="queryParams.objName" placeholder="请输入对象名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>对象脚本</span>
            <el-button size="small" @click="$router.back()">返回</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="objName" label="对象名称" min-width="150" />
            <el-table-column prop="scriptName" label="脚本名称" min-width="150" />
            <el-table-column prop="objTypeName" label="对象类型" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="100" fixed="right" align="center">
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
      </el-col>
    </el-row>

    <DeleteDtSceneObjScript :visible.sync="delVisible" :del-data="currentRow" @success="fetchList" />
  </div>
</template>

<script>
import { listDtSceneObjScripts, listDtScenes } from '@/api/admin/dtSceneApi'
import DeleteDtSceneObjScript from '@/components/admin/DeleteDtSceneObjScript.vue'

export default {
  name: 'ListDtSceneObjScript',
  components: { DeleteDtSceneObjScript },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      scenes: [],
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        objName: '',
        sceneId: '',
        communityId: ''
      }
    }
  },
  mounted() {
    this.queryParams.sceneId = this.$route.query.sceneId || ''
    this.queryParams.communityId = this.$route.query.communityId || ''
    this.loadScenes()
  },
  methods: {
    async loadScenes() {
      try {
        const res = await listDtScenes({ page: 1, row: 100, communityId: this.queryParams.communityId })
        this.scenes = res.data || []
        if (!this.queryParams.sceneId && this.scenes.length > 0) {
          this.switchScene(this.scenes[0].sceneId)
        } else if (this.queryParams.sceneId) {
          this.fetchList()
        }
      } catch { /* ignore */ }
    },
    switchScene(sceneId) {
      this.queryParams.sceneId = sceneId
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listDtSceneObjScripts(this.queryParams)
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
      this.queryParams.objName = ''
      this.queryParams.page = 1
      this.fetchList()
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
.scene-list { list-style: none; padding: 0; margin: 0; }
.scene-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 4px;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.scene-list li:hover { background-color: #f5f7fa; }
.scene-list li.active { background-color: #ecf5ff; color: #409eff; font-weight: bold; }
</style>
