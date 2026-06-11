<template>
  <div class="list-community-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.communityId" placeholder="请输入小区ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入小区名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <AreaSelect ref="searchAreaRef" v-model="queryParams.cityCode" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>小区信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加小区</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="communityId" label="小区ID" width="180" />
        <el-table-column prop="name" label="小区名称" min-width="150" />
        <el-table-column prop="cityName" label="城市编码" width="120" />
        <el-table-column prop="tel" label="客服电话" width="120" />
        <el-table-column prop="extCommunityId" label="外部ID" width="120" />
        <el-table-column
          v-for="spec in showAttrSpecs"
          :key="spec.specCd"
          :prop="'attr_' + spec.specCd"
          :label="spec.specName"
          min-width="120"
        >
          <template slot-scope="{ row }">
            {{ getAttrValue(row, spec.specCd) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button
              v-if="isAdmin"
              size="small"
              type="text"
              style="color: #f56c6c"
              @click="handleDelete(row)"
            >删除</el-button>
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

    <AddCommunity :visible.sync="addVisible" @success="handleSuccess" />
    <EditCommunity :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteCommunity :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listCommunitys } from '@/api/community/communityApi'
import { listAttrSpecs } from '@/api/dev/devApi'
import AreaSelect from '@/components/common/AreaSelect.vue'
import AddCommunity from '@/components/common/AddCommunity.vue'
import EditCommunity from '@/components/common/EditCommunity.vue'
import DeleteCommunity from '@/components/common/DeleteCommunity.vue'

export default {
  name: 'ListCommunityManage',
  components: { AreaSelect, AddCommunity, EditCommunity, DeleteCommunity },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        name: '',
        cityCode: ''
      },
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      showAttrSpecs: [],
      allAttrSpecs: []
    }
  },
  computed: {
    isAdmin() {
      const userInfo = this.$store.state.user.userInfo
      return userInfo && userInfo.storeTypeCd === '800900000001'
    }
  },
  mounted() {
    this.loadAttrSpecs()
    this.fetchList()
  },
  methods: {
    async loadAttrSpecs() {
      try {
        const res = await listAttrSpecs({ tableName: 'building_community_attr', page: 1, row: 100 })
        this.allAttrSpecs = res.data || []
        this.showAttrSpecs = this.allAttrSpecs.filter(s => s.listShow === 'Y')
      } catch { /* ignore */ }
    },
    getAttrValue(row, specCd) {
      if (row.communityAttrDtos) {
        const attr = row.communityAttrDtos.find(a => a.specCd === specCd)
        if (attr) return attr.value
      }
      return '-'
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listCommunitys({ ...this.queryParams })
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
      this.queryParams = { page: 1, row: 10, communityId: '', name: '', cityCode: '' }
      this.$refs.searchAreaRef && this.$refs.searchAreaRef.resetAll()
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
