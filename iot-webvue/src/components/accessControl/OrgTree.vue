<template>
  <div class="org-tree">
    <el-card shadow="never" class="tree-card">
      <div slot="header" class="card-header">
        <span>组织架构</span>
      </div>
      <el-tree
        ref="tree"
        :data="treeData"
        node-key="id"
        default-expand-all
        highlight-current
        :expand-on-click-node="true"
        @node-click="handleNodeClick"
      >
        <span class="custom-tree-node" slot-scope="{ data }">
          <span>{{ data.label }}</span>
        </span>
      </el-tree>
    </el-card>
  </div>
</template>

<script>
import { listOrgTree } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OrgTree',
  data() {
    return {
      treeData: []
    }
  },
  mounted() {
    this.loadTree()
  },
  methods: {
    async loadTree() {
      try {
        const community = getCurrentCommunity() || {}
        const res = await listOrgTree({ communityId: community.communityId })
        const data = res.data || res
        const list = Array.isArray(data) ? data : [data]
        this.treeData = this.convertTree(list)
      } catch (e) {
        console.error('加载组织树失败', e)
      }
    },
    convertTree(list) {
      return list.map(item => ({
        id: item.id,
        label: item.text || item.name || item.label || '',
        children: item.children ? this.convertTree(item.children) : []
      }))
    },
    handleNodeClick(data) {
      this.$emit('switchOrg', { orgId: data.id, orgName: data.label })
    },
    refresh() {
      this.loadTree()
    }
  }
}
</script>

<style scoped>
.org-tree {
  width: 220px;
  min-width: 220px;
}
.tree-card {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
