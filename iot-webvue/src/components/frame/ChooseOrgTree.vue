<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择组织"
    width="500px"
    append-to-body
    @close="handleClose"
  >
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
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedOrg">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listOrgTree } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChooseOrgTree',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      treeData: [],
      selectedOrg: null
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.selectedOrg = null
        this.loadTree()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
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
      this.selectedOrg = { orgId: data.id, orgName: data.label }
    },
    handleConfirm() {
      if (this.selectedOrg) {
        this.$emit('switchOrg', this.selectedOrg)
        this.dialogVisible = false
      }
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>
