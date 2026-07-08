<template>
  <div class="floor-unit-tree">
    <el-card shadow="never" class="tree-card">
      <el-tree
        ref="tree"
        :data="treeData"
        node-key="id"
        default-expand-all
        highlight-current
        :expand-on-click-node="true"
        @node-click="handleNodeClick"
      >
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span>{{ data.label }}</span>
        </span>
      </el-tree>
    </el-card>
  </div>
</template>

<script>
import { queryFloorAndUnits } from '@/api/property/floorApi'

export default {
  name: 'FloorUnitTree',
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
      } catch (e) {
        /* 加载失败 */
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
}
.tree-card {
  height: 100%;
}

</style>
