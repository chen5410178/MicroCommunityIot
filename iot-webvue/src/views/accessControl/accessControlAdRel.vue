<template>
  <div class="access-control-ad-rel">
    <div class="rel-layout">
      <div class="rel-left">
        <el-card shadow="never" class="left-card">
          <div slot="header" class="card-header">
            <span>广告列表</span>
          </div>
          <ul class="ad-list">
            <li
              v-for="item in ads"
              :key="item.adId"
              :class="{ 'ad-item': true, 'active': currentAdId === item.adId }"
              @click="handleSwitchAd(item)"
            >
              {{ item.adName }}
            </li>
          </ul>
          <div v-if="ads.length === 0" style="text-align: center; color: #909399; padding: 20px;">暂无广告数据</div>
        </el-card>
      </div>
      <div class="rel-right">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="门禁名称">
              <el-input v-model="queryParams.machineName" placeholder="请输入门禁名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>广告门禁</span>
            <el-button type="primary" size="small" @click="handleAddRel" :disabled="!currentAdId">关联门禁</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="adName" label="广告" min-width="120" />
            <el-table-column prop="machineName" label="门禁名称" min-width="120" />
            <el-table-column prop="machineCode" label="门禁编号" min-width="120" />
            <el-table-column prop="createTime" label="关联时间" min-width="160" />
            <el-table-column label="操作" width="120" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDeleteRel(row)">取消授权</el-button>
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
      </div>
    </div>
    <AddAccessControlAdRel :visible.sync="addRelVisible" :ad-id="currentAdId" @success="handleSuccess" />
    <DeleteAccessControlAdRel :visible.sync="delRelVisible" :del-data="currentRelRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAccessControlAds, listAccessControlAdRels } from '@/api/accessControl/accessControlApi'
import AddAccessControlAdRel from '@/components/accessControl/AddAccessControlAdRel.vue'
import DeleteAccessControlAdRel from '@/components/accessControl/DeleteAccessControlAdRel.vue'

export default {
  name: 'AccessControlAdRel',
  components: { AddAccessControlAdRel, DeleteAccessControlAdRel },
  data() {
    return {
      loading: false,
      ads: [],
      list: [],
      total: 0,
      currentAdId: '',
      addRelVisible: false,
      delRelVisible: false,
      currentRelRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineName: ''
      }
    }
  },
  mounted() {
    this.fetchAds()
  },
  methods: {
    async fetchAds() {
      try {
        const res = await listAccessControlAds({
          page: 1,
          row: 100,
          communityId: this.$store.state.app.currentCommunity.communityId
        })
        this.ads = res.data || []
        if (this.ads.length > 0) {
          this.handleSwitchAd(this.ads[0])
        }
      } catch {
        this.ads = []
      }
    },
    handleSwitchAd(ad) {
      this.currentAdId = ad.adId
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      if (!this.currentAdId) return
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          adId: this.currentAdId,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        const res = await listAccessControlAdRels(params)
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
      this.queryParams = {
        page: 1,
        row: 10,
        machineName: ''
      }
      this.fetchList()
    },
    handleAddRel() {
      if (!this.currentAdId) {
        this.$message.warning('请先选择左侧广告')
        return
      }
      this.addRelVisible = true
    },
    handleDeleteRel(row) {
      this.currentRelRow = { ...row }
      this.delRelVisible = true
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
.rel-layout {
  display: flex;
  gap: 16px;
}
.rel-left {
  width: 200px;
  flex-shrink: 0;
}
.rel-right {
  flex: 1;
  min-width: 0;
}
.left-card {
  height: 100%;
}
.ad-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 500px;
  overflow-y: auto;
}
.ad-item {
  padding: 10px 16px;
  cursor: pointer;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
}
.ad-item:hover {
  background-color: #f5f7fa;
}
.ad-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}
.search-card {
  margin-bottom: 16px;
}
.table-card {
  margin-bottom: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
