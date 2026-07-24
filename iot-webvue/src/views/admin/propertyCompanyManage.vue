<template>
  <div class="list-property-company">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.storeId" placeholder="请输入物业编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.tel" placeholder="请输入电话" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>物业公司</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="storeId" label="编号" width="150" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="tel" label="管理员" width="120" />
        <el-table-column prop="tel" label="电话" width="120" />
        <el-table-column prop="corporation" label="公司法人" width="120" />
        <el-table-column prop="foundingTime" label="成立日期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="340" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
            <el-button size="small" type="text" @click="handleManageCommunity(row)">管理小区</el-button>
            <el-button size="small" type="text" @click="handleAdminLogin(row)">登录</el-button>
            <el-button v-if="row.state !== '48002'" size="small" type="text" @click="handleUpdateState(row, '48002')">限制登录</el-button>
            <el-button v-else size="small" type="text" @click="handleUpdateState(row, '48001')">恢复登录</el-button>
            <el-button size="small" type="text" @click="handleResetPwd(row)">重置密码</el-button>
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

    <AddPropertyCompany ref="addRef" @success="fetchList" />
    <EditPropertyCompany ref="editRef" :edit-data="currentRow" @success="fetchList" />
    <DeletePropertyCompany ref="delRef" :del-data="currentRow" @success="fetchList" />
    <AdminLoginProperty ref="adminLoginRef" @success="handleLoginSuccess" />
    <UpdateStoreState ref="updateStateRef" @success="fetchList" />
    <ResetStaffPwd ref="resetPwdRef" />
  </div>
</template>

<script>
import { listProperty } from '@/api/admin/propertyCompanyApi'
import { listMyStore, listMyEnteredCommunitys, queryStaffPrivileges } from '@/api/community/communityApi'
import { setToken, setUserInfo, setCommunityList, setCurrentCommunity } from '@/utils/auth'
import AddPropertyCompany from '@/components/admin/AddPropertyCompany.vue'
import EditPropertyCompany from '@/components/admin/EditPropertyCompany.vue'
import DeletePropertyCompany from '@/components/admin/DeletePropertyCompany.vue'
import AdminLoginProperty from '@/components/admin/AdminLoginProperty.vue'
import UpdateStoreState from '@/components/admin/UpdateStoreState.vue'
import ResetStaffPwd from '@/components/frame/ResetStaffPwd.vue'

export default {
  name: 'ListPropertyCompany',
  components: { AddPropertyCompany, EditPropertyCompany, DeletePropertyCompany, AdminLoginProperty, UpdateStoreState, ResetStaffPwd },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        storeId: '',
        name: '',
        tel: ''
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
        const res = await listProperty(this.queryParams)
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
    handleReset() {
      this.queryParams = { page: 1, row: 10, storeId: '', name: '', tel: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$refs.addRef.open()
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.$refs.editRef.open()
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.$refs.delRef.open()
    },
    handleManageCommunity(row) {
      this.$router.push('/pages/admin/propertyCommunityManage?storeId=' + row.storeId)
    },
    handleAdminLogin(row) {
      this.$refs.adminLoginRef.open(row)
    },
    handleUpdateState(row, state) {
      this.$refs.updateStateRef.open(row, state)
    },
    handleResetPwd(row) {
      this.$refs.resetPwdRef.open(row)
    },
    handleLoginSuccess(res) {
      setToken(res.token)
      setUserInfo({
        userId: res.userId,
        name: res.name,
        tel: res.tel,
        levelCd: res.levelCd
      })
      listMyStore({ _uId: 'ccdd00opikookjuhyyttvhnnjuuu' }).then(() => {
        return queryStaffPrivileges({ a: 'HC' })
      }).then(() => {
        return listMyEnteredCommunitys({ _uId: 'ccdd00opikookjuhyyttvhnnjuuu', page: 1, row: 3 })
      }).then(res => {
        const communityList = res && res.data ? res.data : []
        if (communityList.length > 0) {
          setCommunityList(communityList)
          setCurrentCommunity(communityList[0])
        }
        this.$router.push('/pages/index/index')
      }).catch(err => {
        const msg = err && err.msg ? err.msg : '初始化数据失败'
        this.$message.error(msg)
      })
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
