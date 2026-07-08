<template>
  <div class="role-page">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="role-list-card">
          <div slot="header" class="card-header">
            <span>角色列表</span>
          </div>
          <div class="role-actions">
            <el-button type="primary" size="small" @click="handleAddRole">添加</el-button>
            <el-button type="warning" size="small" @click="handleEditRole" :disabled="!currentRole">修改</el-button>
            <el-button type="danger" size="small" @click="handleDeleteRoleClick" :disabled="!currentRole">删除</el-button>
          </div>
          <ul class="role-list">
            <li
              v-for="role in roleList"
              :key="role.pgId"
              :class="{ active: currentRole && currentRole.pgId === role.pgId }"
              @click="handleSwitchRole(role)"
            >
              {{ role.name }}
            </li>
          </ul>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="detail-card" v-if="currentRole">
          <div class="role-header">
            <h4>{{ currentRole.name }}</h4>
            <p class="role-desc">{{ currentRole.description }}</p>
          </div>
          <div class="tab-buttons">
            <span
              :class="{ active: activeTab === 'privilege' }"
              @click="handleTabChange('privilege')"
            >功能授权</span>
            <span
              :class="{ active: activeTab === 'community' }"
              @click="handleTabChange('community')"
            >小区授权</span>
            <span
              :class="{ active: activeTab === 'staff' }"
              @click="handleTabChange('staff')"
            >员工关联</span>
          </div>
          <div class="tab-content">
            <PrivilegeTree ref="privilegeTreeRef" v-show="activeTab === 'privilege'" />
            <RoleCommunity ref="roleCommunityRef" v-show="activeTab === 'community'" />
            <RoleStaff ref="roleStaffRef" v-show="activeTab === 'staff'" />
          </div>
        </el-card>
        <el-card shadow="never" class="detail-card empty-card" v-else>
          <p style="text-align: center; color: #909399; padding: 40px 0;">请选择左侧角色</p>
        </el-card>
      </div>
    </div>
    <AddRole :visible.sync="addVisible" @success="handleRoleSuccess" />
    <EditRole :visible.sync="editVisible" :edit-data="currentRole" @success="handleRoleSuccess" />
    <DeleteRole :visible.sync="deleteVisible" :del-data="currentRole" @success="handleRoleSuccess" />
  </div>
</template>

<script>
import { listPrivilegeGroup } from '@/api/frame/roleApi'
import AddRole from '@/components/frame/role/addRole.vue'
import EditRole from '@/components/frame/role/editRole.vue'
import DeleteRole from '@/components/frame/role/deleteRole.vue'
import PrivilegeTree from '@/components/frame/role/privilegeTree.vue'
import RoleCommunity from '@/components/frame/role/roleCommunity.vue'
import RoleStaff from '@/components/frame/role/roleStaff.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoleManage',
  components: { AddRole, EditRole, DeleteRole, PrivilegeTree, RoleCommunity, RoleStaff },
  data() {
    return {
      roleList: [],
      currentRole: null,
      activeTab: 'privilege',
      addVisible: false,
      editVisible: false,
      deleteVisible: false
    }
  },
  mounted() {
    this.fetchRoleList()
  },
  methods: {
    async fetchRoleList() {
      try {
        const community = getCurrentCommunity() || {}
        const res = await listPrivilegeGroup({ communityId: community.communityId })
        this.roleList = Array.isArray(res) ? res : (res.data || [])
        if (this.roleList.length > 0 && !this.currentRole) {
          this.handleSwitchRole(this.roleList[0])
        }
      } catch (e) {
        console.error('加载角色列表失败', e)
      }
    },
    handleSwitchRole(role) {
      this.currentRole = role
      this.activeTab = 'privilege'
      this.$nextTick(() => { this.loadTabContent() })
    },
    handleTabChange(tab) {
      this.activeTab = tab
      this.$nextTick(() => { this.loadTabContent() })
    },
    loadTabContent() {
      const pgId = this.currentRole ? this.currentRole.pgId : ''
      if (this.activeTab === 'privilege') {
        this.$refs.privilegeTreeRef && this.$refs.privilegeTreeRef.loadPrivilege(pgId)
      } else if (this.activeTab === 'community') {
        this.$refs.roleCommunityRef && this.$refs.roleCommunityRef.open(pgId)
      } else if (this.activeTab === 'staff') {
        this.$refs.roleStaffRef && this.$refs.roleStaffRef.open(pgId)
      }
    },
    handleAddRole() {
      this.addVisible = true
    },
    handleEditRole() {
      if (!this.currentRole) { this.$message.warning('请先选择角色'); return }
      this.editVisible = true
    },
    handleDeleteRoleClick() {
      if (!this.currentRole) { this.$message.warning('请先选择角色'); return }
      this.deleteVisible = true
    },
    handleRoleSuccess() {
      this.fetchRoleList()
    }
  },
  watch: {}
}
</script>

<style scoped>
.page-container { display: flex; gap: 16px; height: calc(100vh - 100px); }
.left-panel { width: 220px; min-width: 220px; }
.right-panel { flex: 1; min-width: 0; overflow-y: auto; }
.role-list-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.role-actions { display: flex; gap: 4px; margin-bottom: 12px; }
.role-actions .el-button { flex: 1; padding: 7px 4px; font-size: 12px; }
.role-list { list-style: none; padding: 0; margin: 0; }
.role-list li { padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #ebeef5; font-size: 14px; }
.role-list li:hover { background-color: #f5f7fa; }
.role-list li.active { background-color: #ecf5ff; color: #409eff; font-weight: bold; }
.detail-card { height: 100%; display: flex; flex-direction: column; }
.role-header { padding-bottom: 12px; border-bottom: 1px solid #ebeef5; }
.role-header h4 { margin: 0 0 8px 0; font-size: 16px; }
.role-desc { margin: 0; font-size: 13px; color: #909399; }
.tab-buttons { display: flex; gap: 0; margin: 12px 0; border-bottom: 2px solid #ebeef5; }
.tab-buttons span { padding: 8px 20px; cursor: pointer; font-size: 14px; color: #606266; border-bottom: 2px solid transparent; margin-bottom: -2px; }
.tab-buttons span.active { color: #409eff; border-bottom-color: #409eff; }
.tab-content { flex: 1; overflow-y: auto; padding-top: 12px; }
.empty-card { display: flex; align-items: center; justify-content: center; height: 100%; }
</style>
