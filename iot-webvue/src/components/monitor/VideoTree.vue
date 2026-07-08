<template>
  <div class="video-tree">
    <el-tree
      ref="tree"
      :data="treeData"
      :props="treeProps"
      node-key="id"
      highlight-current
      :expand-on-click-node="false"
      @node-click="handleNodeClick"
      @node-expand="handleNodeExpand"
    />
  </div>
</template>

<script>
import { listMonitorAreas, listMonitorMachines } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'VideoTree',
  data() {
    return {
      treeData: [],
      treeProps: {
        label: 'name',
        children: 'children',
        isLeaf: 'leaf'
      },
      communityId: ''
    }
  },
  mounted() {
    this.communityId = (getCurrentCommunity() || {}).communityId || ''
    this.loadAreas()
  },
  methods: {
    async loadAreas() {
      try {
        const res = await listMonitorAreas({ page: 1, row: 1000, communityId: this.communityId })
        if (res.code === 0) {
          const areas = res.data || []
          this.treeData = areas.map(area => ({
            id: 'a_' + area.maId,
            name: area.name || area.maName,
            maId: area.maId,
            children: [],
            leaf: false
          }))
        }
      } catch (e) { /* ignore */ }
    },
    async handleNodeExpand(data) {
      if (data.leaf || data.children.length > 0) return
      try {
        const res = await listMonitorMachines({ page: 1, row: 1000, communityId: this.communityId, maId: data.maId })
        if (res.code === 0) {
          const machines = res.data || []
          const children = machines.map(m => ({
            id: 'm_' + m.machineId,
            name: m.machineName,
            machineId: m.machineId,
            machineName: m.machineName,
            leaf: true
          }))
          this.$set(data, 'children', children)
        }
      } catch (e) { /* ignore */ }
    },
    handleNodeClick(data) {
      if (!data.leaf) return
      this.$emit('select-video', {
        machineId: data.machineId,
        machineName: data.machineName
      })
    }
  }
}
</script>

<style scoped>
.video-tree { padding: 10px 0; }
.video-tree >>> .el-tree { background: transparent; color: #f0f0f0; }
.video-tree >>> .el-tree-node__content { height: 36px; padding-left: 8px; }
.video-tree >>> .el-tree-node__content:hover { background: #5b6068; }
.video-tree >>> .el-tree-node.is-current > .el-tree-node__content { background: #5b6068; }
.video-tree >>> .el-tree-node__expand-icon { color: #aaa; }
.video-tree >>> .el-tree-node__label { font-size: 14px; }
</style>
