<template>
  <el-dialog :visible.sync="dialogVisible" title="申请入驻" width="800px" @close="handleClose">
    <el-form :inline="true" :model="searchForm">
      <el-form-item>
        <el-input v-model="searchForm.communityName" placeholder="请输入小区名称" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="communityList" v-loading="tableLoading" border stripe max-height="300">
      <el-table-column prop="communityId" label="小区ID" width="180" />
      <el-table-column prop="name" label="小区名称" min-width="150" />
      <el-table-column prop="address" label="地址" min-width="200" />
      <el-table-column label="操作" width="120" fixed="right" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="handleApply(row)">申请入驻</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="total > 0"
      style="margin-top: 15px; text-align: right"
      :current-page="searchForm.page"
      :page-sizes="[10, 20, 50]"
      :page-size="searchForm.row"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />

    <el-dialog :visible.sync="applyVisible" title="选择功能权限" width="500px" append-to-body @close="applyVisible = false">
      <el-checkbox v-model="isAll" @change="changeAll" style="margin-bottom: 10px">全选</el-checkbox>
      <el-checkbox-group v-model="applyGroupIds" @change="changeItem">
        <el-checkbox v-for="item in menuGroups" :key="item.gId" :label="item.gId">{{ item.name }}</el-checkbox>
      </el-checkbox-group>
      <span slot="footer">
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="doApply" :loading="applyLoading">确定入驻</el-button>
      </span>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { joinCommunity, listMenuGroup, listFreeCommunity } from '@/api/admin/propertyCommunityApi'

export default {
  name: 'AddEnterCommunity',
  props: {
    memberId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      tableLoading: false,
      communityList: [],
      total: 0,
      searchForm: {
        communityName: '',
        page: 1,
        row: 10
      },
      menuGroups: [],
      applyVisible: false,
      applyLoading: false,
      applyCommunity: null,
      applyGroupIds: [],
      isAll: true
    }
  },
  methods: {
    open() {
      this.dialogVisible = true
      this.searchForm.communityName = ''
      this.searchForm.page = 1
      this.fetchList()
    },
    handleClose() {
      this.dialogVisible = false
      this.communityList = []
      this.total = 0
      this.applyCommunity = null
    },
    handleSearch() {
      this.searchForm.page = 1
      this.fetchList()
    },
    handleSizeChange(val) {
      this.searchForm.row = val
      this.searchForm.page = 1
      this.fetchList()
    },
    handlePageChange(val) {
      this.searchForm.page = val
      this.fetchList()
    },
    async fetchList() {
      this.tableLoading = true
      try {
        const res = await listFreeCommunity(this.searchForm)
        this.communityList = res.data || []
        this.total = res.total || 0
      } catch { /* ignore */ } finally {
        this.tableLoading = false
      }
    },
    async loadMenuGroups() {
      try {
        const res = await listMenuGroup({ page: 1, row: 100, storeType: '800900000003' })
        this.menuGroups = res.data || []
        this.applyGroupIds = this.menuGroups.map(item => item.gId)
        this.isAll = true
      } catch { /* ignore */ }
    },
    changeAll(val) {
      this.applyGroupIds = []
      if (val) {
        this.menuGroups.forEach(item => {
          this.applyGroupIds.push(item.gId)
        })
      }
    },
    changeItem() {
      this.isAll = this.applyGroupIds.length === this.menuGroups.length
    },
    handleApply(row) {
      this.applyCommunity = row
      this.applyVisible = true
      this.loadMenuGroups()
    },
    async doApply() {
      this.applyLoading = true
      try {
        const data = {
          memberId: this.memberId,
          communityIds: [this.applyCommunity.communityId],
          groupIds: this.applyGroupIds
        }
        await joinCommunity(data)
        this.$message.success('申请入驻成功')
        this.applyVisible = false
        this.$emit('success')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '申请入驻失败')
      } finally {
        this.applyLoading = false
      }
    }
  }
}
</script>
