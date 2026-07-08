<template>
  <div class="list-owner">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入人员名称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.roomName" placeholder="楼栋-单元-房屋" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.link" placeholder="请输入联系方式" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.ownerId" placeholder="请输入人员ID" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>人员信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加人员</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="人员人脸" width="100" align="center">
          <template slot-scope="{ row }">
            <img v-if="row.url" :src="row.url" style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer" @click="viewImage(row.url)" />
            <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
          </template>
        </el-table-column>
        <el-table-column label="姓名" min-width="120">
          <template slot-scope="{ row }">{{ row.name }}({{ row.link }})</template>
        </el-table-column>
        <el-table-column label="性别" width="70" align="center">
          <template slot-scope="{ row }">{{ row.sex == '0' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证" min-width="160">
          <template slot-scope="{ row }">{{ row.idCard || '-' }}</template>
        </el-table-column>
        <el-table-column label="家庭住址" min-width="150">
          <template slot-scope="{ row }">{{ row.address || '-' }}</template>
        </el-table-column>
        <el-table-column label="门禁卡" min-width="120">
          <template slot-scope="{ row }">{{ row.cardNumber || '-' }}</template>
        </el-table-column>
        <el-table-column label="房屋数" width="80" align="center">
          <template slot-scope="{ row }">{{ row.roomCount || 0 }}</template>
        </el-table-column>
        <el-table-column prop="ownerTypeName" label="人员类型" width="100" />
        <el-table-column label="车辆数" width="80" align="center">
          <template slot-scope="{ row }">{{ row.carCount || 0 }}</template>
        </el-table-column>
        <el-table-column v-for="col in dynamicColumns" :key="col.specCd" :label="col.specName" min-width="120">
          <template slot-scope="{ row }">{{ getDynamicValue(row, col.specCd) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
    <AddOwner :visible.sync="addVisible" @success="handleSuccess" />
    <EditOwner :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteOwner :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <el-dialog :visible.sync="imageVisible" title="头像预览" width="400px" append-to-body>
      <img :src="imageUrl" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script>
import { queryOwners } from '@/api/owner/ownerApi'
import { listAttrSpecs } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddOwner from '@/components/property/AddOwner.vue'
import EditOwner from '@/components/property/EditOwner.vue'
import DeleteOwner from '@/components/property/DeleteOwner.vue'

export default {
  name: 'ListOwner',
  components: { AddOwner, EditOwner, DeleteOwner },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      dynamicColumns: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      imageVisible: false,
      imageUrl: '',
      queryParams: { page: 1, row: 10, name: '', roomName: '', link: '', ownerId: '', idCard: '' }
    }
  },
  mounted() {
    this.loadDynamicColumns().then(() => this.fetchList())
  },
  methods: {
    async loadDynamicColumns() {
      try {
        const res = await listAttrSpecs({ specCd: 'building_owner_attr', page: 1, row: 100 })
        const specs = res.data || []
        this.dynamicColumns = specs.filter(item => item.listShow === 'Y').map(item => ({
          specCd: item.specCd,
          specName: item.specName
        }))
      } catch (e) { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const params = {
          ...this.queryParams,
          communityId: community ? community.communityId : ''
        }
        const res = await queryOwners(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally { this.loading = false }
    },
    getDynamicValue(row, specCd) {
      if (!row.ownerAttrDtos || row.ownerAttrDtos.length === 0) return '-'
      const attr = row.ownerAttrDtos.find(item => item.specCd === specCd)
      return attr ? (attr.valueName || attr.value || '-') : '-'
    },
    handleSearch() { this.queryParams.page = 1; this.fetchList() },
    handleReset() {
      this.queryParams = { page: 1, row: 10, name: '', roomName: '', link: '', ownerId: '', idCard: '' }
      this.fetchList()
    },
    handleAdd() { this.addVisible = true },
    handleEdit(row) { this.currentRow = { ...row }; this.editVisible = true },
    handleDelete(row) { this.currentRow = { ...row }; this.delVisible = true },
    handleDetail(row) {
      this.$router.push({
        path: '/pages/owner/ownerDetail',
        query: { memberId: row.memberId, ownerId: row.ownerId, ownerName: row.name, needBack: 'true' }
      })
    },
    viewImage(url) { this.imageUrl = url; this.imageVisible = true },
    handleSuccess() { this.fetchList() },
    handleSizeChange(val) { this.queryParams.row = val; this.fetchList() },
    handlePageChange(val) { this.queryParams.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
