<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="人员人脸" width="100" align="center">
        <template slot-scope="{ row }">
          <img v-if="row.url" :src="row.url" style="width: 60px; height: 60px; border-radius: 4px;" @error="row.url = ''" />
          <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px;" />
        </template>
      </el-table-column>
      <el-table-column label="姓名" min-width="120">
        <template slot-scope="{ row }">{{ row.name }}({{ row.link }})</template>
      </el-table-column>
      <el-table-column label="性别" width="80">
        <template slot-scope="{ row }">{{ row.sex == '0' ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="idCard" label="身份证" min-width="160">
        <template slot-scope="{ row }">{{ row.idCard || '-' }}</template>
      </el-table-column>
      <el-table-column prop="address" label="家庭住址" min-width="180">
        <template slot-scope="{ row }">{{ row.address || '-' }}</template>
      </el-table-column>
      <el-table-column prop="ownerTypeName" label="人员类型" min-width="100" />
      <el-table-column v-for="(col, idx) in dynamicColumns" :key="col" :label="col" min-width="120">
        <template slot-scope="{ row }">{{ (row.listValues && row.listValues[idx]) || '-' }}</template>
      </el-table-column>
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { queryOwners } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailMember',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      page: 1,
      row: 10,
      dynamicColumns: []
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await queryOwners({
          ownerId: this.ownerId,
          communityId: community ? community.communityId : '',
          page: this.page,
          row: this.row
        })
        this.list = res.data || []
        this.total = res.records || 0
        if (this.list.length && this.list[0].listColumns) {
          this.dynamicColumns = this.list[0].listColumns
        }
      } finally { this.loading = false }
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchList() },
    handlePageChange(val) { this.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
