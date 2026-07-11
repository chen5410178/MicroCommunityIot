<template>
  <div class="privilege-tree" v-loading="loading">
    <el-tree
      ref="treeRef"
      :data="treeData"
      node-key="id"
      show-checkbox
      :props="treeProps"
      :default-expanded-keys="expandedKeys"
      :default-checked-keys="checkedKeys"
      @check="handleCheck"
    />
  </div>
</template>

<script>
import { listPrivilegeTree, addPrivilegeToGroup, deletePrivilegeFromGroup } from '@/api/frame/roleApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'PrivilegeTree',
  data() {
    return {
      loading: false,
      treeData: [],
      treeProps: { children: 'children', label: 'label' },
      expandedKeys: [],
      checkedKeys: [],
      pgId: ''
    }
  },
  methods: {
    async loadPrivilege(pgId) {
      this.pgId = pgId
      this.loading = true
      try {
        const community = getCurrentCommunity() || {}
        const res = await listPrivilegeTree({ pgId, communityId: community.communityId })
        const raw = Array.isArray(res.data) ? res.data : res || []
        this.treeData = this.buildTree(raw)
        this.expandedKeys = this.collectAllKeys(this.treeData)
        this.checkedKeys = this.collectCheckedKeys(raw)
      } finally {
        this.loading = false
      }
    },
    buildTree(list) {
      const groupMap = {}
      list.forEach(item => {
        const gKey = item.gId
        if (!groupMap[gKey]) {
          groupMap[gKey] = {
            id: 'g_' + item.gId,
            label: item.gName,
            gId: item.gId,
            children: [],
            menuMap: {}
          }
        }
        const group = groupMap[gKey]
        const mKey = item.mId
        if (!group.menuMap[mKey]) {
          const menuNode = {
            id: 'm_' + item.mId,
            label: item.mName,
            mId: item.mId,
            children: []
          }
          group.menuMap[mKey] = menuNode
          group.children.push(menuNode)
        }
        const menu = group.menuMap[mKey]
        const pNode = {
          id: 'p_' + item.pId,
          pId: item.pId,
          label: item.pName
        }
        menu.children.push(pNode)
      })
      return Object.values(groupMap).map(g => {
        delete g.menuMap
        return g
      })
    },
    collectCheckedKeys(raw) {
      return raw.filter(item => item.pgId).map(item => 'p_' + item.pId)
    },
    collectAllKeys(nodes) {
      let keys = []
      nodes.forEach(n => {
        keys.push(n.id)
        if (n.children && n.children.length) {
          keys = keys.concat(this.collectAllKeys(n.children))
        }
      })
      return keys
    },
    async handleCheck(node, checkedInfo) {
      const pIds = []
      this.collectLeafPIds(node, pIds)
      if (pIds.length === 0) return
      const isChecked = checkedInfo.checkedKeys.includes(node.id)
      if (isChecked) {
        await this.doAddPrivilege(pIds)
      } else {
        await this.doDeletePrivilege(pIds)
      }
    },
    collectLeafPIds(node, result) {
      if (node.pId) {
        result.push(node.pId)
        return
      }
      if (node.children && node.children.length) {
        node.children.forEach(child => this.collectLeafPIds(child, result))
      }
    },
    async doAddPrivilege(pIds) {
      try {
        await addPrivilegeToGroup({ pgId: this.pgId, pIds: pIds.map(pId => ({ pId })) })
      } catch (e) {
        this.$message.error(e.msg || '授权失败')
        pIds.forEach(pId => {
          this.$refs.treeRef.setChecked('p_' + pId, false, false)
        })
      }
    },
    async doDeletePrivilege(pIds) {
      try {
        await deletePrivilegeFromGroup({ pgId: this.pgId, pIds: pIds.map(pId => ({ pId })) })
      } catch (e) {
        this.$message.error(e.msg || '取消授权失败')
        pIds.forEach(pId => {
          this.$refs.treeRef.setChecked('p_' + pId, true, false)
        })
      }
    }
  }
}
</script>
