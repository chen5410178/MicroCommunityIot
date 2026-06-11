<template>
  <div class="staff-detail">
    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header">
        <span>员工基本信息</span>
      </div>
      <div v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="4" style="text-align: center">
            <img :src="staffInfo.headImgUrl || '/img/noPhoto.jpg'" style="width: 100px; height: 100px; border-radius: 50%;" />
          </el-col>
          <el-col :span="20">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="info-item"><span class="info-label">编号：</span><span>{{ staffInfo.userId }}</span></div>
              </el-col>
              <el-col :span="8">
                <div class="info-item"><span class="info-label">名称：</span><span>{{ staffInfo.name }}</span></div>
              </el-col>
              <el-col :span="8">
                <div class="info-item"><span class="info-label">邮箱：</span><span>{{ staffInfo.email }}</span></div>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="info-item"><span class="info-label">手机号：</span><span>{{ staffInfo.tel }}</span></div>
              </el-col>
              <el-col :span="8">
                <div class="info-item"><span class="info-label">性别：</span><span>{{ staffInfo.sex === '0' ? '男' : staffInfo.sex === '1' ? '女' : '' }}</span></div>
              </el-col>
              <el-col :span="8">
                <div class="info-item"><span class="info-label">住址：</span><span>{{ staffInfo.address }}</span></div>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header">
        <span>关联组织</span>
      </div>
      <el-table :data="orgList" border stripe>
        <el-table-column prop="orgName" label="组织名称" min-width="150" />
        <el-table-column prop="parentOrgName" label="上级组织" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header">
        <span>关联角色</span>
      </div>
      <el-table :data="roleList" border stripe>
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="communityName" label="所属小区" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header">
        <span>员工权限</span>
      </div>
      <el-tree
        :data="privilegeTree"
        node-key="id"
        default-expand-all
        :props="{ children: 'children', label: 'label' }"
      >
        <span class="custom-tree-node" slot-scope="{ data }">
          <span>{{ data.label }}</span>
          <el-tag v-if="data.checked" size="mini" type="success" style="margin-left: 8px;">已授权</el-tag>
          <el-tag v-else size="mini" type="info" style="margin-left: 8px;">未授权</el-tag>
        </span>
      </el-tree>
    </el-card>
  </div>
</template>

<script>
import { listStaffs, queryStaffPrivileges, listStaffOrgs, listStaffRoles } from '@/api/frame/orgApi'

export default {
  name: 'StaffDetail',
  data() {
    return {
      loading: false,
      staffId: '',
      staffInfo: {},
      orgList: [],
      roleList: [],
      privilegeTree: []
    }
  },
  mounted() {
    this.staffId = this.$route.query.staffId || ''
    if (this.staffId) {
      this.fetchStaffInfo()
      this.fetchStaffOrgs()
      this.fetchStaffRoles()
      this.fetchPrivileges()
    }
  },
  methods: {
    async fetchStaffInfo() {
      this.loading = true
      try {
        const community = this.$store.state.app.currentCommunity || {}
        const res = await listStaffs({
          page: 1,
          row: 1,
          userId: this.staffId,
          communityId: community.communityId
        })
        const list = res.data || []
        this.staffInfo = list.length > 0 ? list[0] : {}
      } finally {
        this.loading = false
      }
    },
    async fetchStaffOrgs() {
      try {
        const community = this.$store.state.app.currentCommunity || {}
        const res = await listStaffOrgs({
          staffId: this.staffId,
          communityId: community.communityId
        })
        this.orgList = res.data || []
      } catch (e) {
        console.error('加载关联组织失败', e)
      }
    },
    async fetchStaffRoles() {
      try {
        const community = this.$store.state.app.currentCommunity || {}
        const res = await listStaffRoles({
          staffId: this.staffId,
          communityId: community.communityId
        })
        this.roleList = res.data || []
      } catch (e) {
        console.error('加载关联角色失败', e)
      }
    },
    async fetchPrivileges() {
      try {
        const res = await queryStaffPrivileges({ staffId: this.staffId })
        const data = res.data || []
        this.privilegeTree = this.convertPrivilegeTree(Array.isArray(data) ? data : [])
      } catch (e) {
        console.error('加载权限信息失败', e)
      }
    },
    convertPrivilegeTree(list) {
      return list.map(item => ({
        id: item.pId || item.id,
        label: item.pName || item.name || item.label || '',
        checked: item.flag === '1' || item.checked === true,
        children: item.subPrivileges ? this.convertPrivilegeTree(item.subPrivileges) : (item.children || [])
      }))
    }
  }
}
</script>

<style scoped>
.detail-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-item { padding: 10px 0; font-size: 14px; }
.info-label { color: #909399; }
.custom-tree-node { display: flex; align-items: center; }
</style>
