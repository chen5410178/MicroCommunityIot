<template>
  <div class="floor-unit-tree">
    <el-card shadow="never" class="tree-card">
      <div class="tree-scroll">
        <el-tree
          ref="tree"
          :data="treeData"
          node-key="id"
          :default-expand-all="defaultExpandAll"
          highlight-current
          :expand-on-click-node="true"
          @node-click="handleNodeClick"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <span>{{ data.label }}</span>
          </span>
        </el-tree>
      </div>
    </el-card>
  </div>
</template>

<script>
import { queryFloorAndUnits } from '@/api/property/floorApi'

export default {
  name: 'FloorUnitTree',
  props: {
    defaultExpandAll: { type: Boolean, default: false }
  },
  data() {
    return {
      treeData: [],
      rawUnits: []
    }
  },
  mounted() {
    this.loadTree()
  },
  methods: {
    async loadTree(floorId) {
      try {
        const res = await queryFloorAndUnits({})
        this.rawUnits = res.data || []
        this.buildTreeData()
        if (floorId && this.rawUnits.length > 0) {
          const firstUnit = this.rawUnits.find(u => u.unitId && u.floorId === floorId)
          if (firstUnit) {
            this.$nextTick(() => {
              this.$refs.tree && this.$refs.tree.setCurrentKey('u_' + firstUnit.unitId)
            })
          }
        }
        this.$emit('loaded', this.rawUnits)
      } catch (e) {
        /* 加载失败 */
        this.$emit('loaded', null)
      }
    },
    buildTreeData() {
      const floorMap = {}
      this.rawUnits.forEach(item => {
        if (!floorMap[item.floorId]) {
          floorMap[item.floorId] = {
            id: 'f_' + item.floorId,
            floorId: item.floorId,
            label: item.floorNum + '栋',
            children: []
          }
        }
        if (item.unitId) {
          floorMap[item.floorId].children.push({
            id: 'u_' + item.unitId,
            unitId: item.unitId,
            floorId: item.floorId,
            label: item.unitNum + '单元'
          })
        }
      })
      this.treeData = Object.values(floorMap)
    },
    handleNodeClick(data) {
      if (data.id.startsWith('f_')) {
        this.$emit('switchFloor', { floorId: data.floorId })
      } else {
        this.$emit('switchUnit', { unitId: data.unitId })
      }
    },
    refresh(floorId) {
      this.loadTree(floorId)
    }
  }
}
</script>

<style scoped>
.floor-unit-tree {
  width: 220px;
  min-width: 220px;
  height: 100%;
}
.tree-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.tree-card >>> .el-card__body {
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 8px 2px 8px 8px;
}
.tree-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}
.tree-scroll::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.tree-scroll::-webkit-scrollbar-track {
  background: transparent;
}
.tree-scroll::-webkit-scrollbar-thumb {
  background: #c8ccd4;
  border-radius: 3px;
}
.tree-scroll::-webkit-scrollbar-thumb:hover {
  background: #a3a8b3;
}
</style>
