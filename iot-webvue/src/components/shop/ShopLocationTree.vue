<template>
  <div class="shop-location-tree">
    <el-card shadow="never" class="tree-card">
      <div slot="header" class="card-header">
        <span>商铺位置</span>
        <el-button type="text" size="small" @click="loadTree">刷新</el-button>
      </div>
      <el-tree
        ref="tree"
        :data="treeData"
        node-key="locationId"
        default-expand-all
        highlight-current
        :expand-on-click-node="true"
        :props="{ label: 'locationName', children: 'children' }"
        @node-click="handleNodeClick"
      >
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span>{{ data.locationName }}</span>
          <span class="tree-node-op" v-if="data.locationId !== 'all'">
            <el-button type="text" size="mini" @click.stop="handleAddChild(data)">+</el-button>
          </span>
        </span>
      </el-tree>
    </el-card>
  </div>
</template>

<script>
import { listShopLocations } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ShopLocationTree',
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listShopLocations({ communityId })
        this.treeData = [{
          locationId: 'all',
          locationName: '全部位置',
          children: res.data || []
        }]
        this.$nextTick(() => {
          this.$refs.tree && this.$refs.tree.setCurrentKey('all')
        })
      } catch (e) {
        /* 加载失败 */
      }
    },
    handleNodeClick(data) {
      if (data.locationId === 'all') {
        this.$emit('select', { locationId: '', locationName: '' })
      } else {
        this.$emit('select', {
          locationId: data.locationId,
          locationName: data.locationName,
          level: data.level
        })
      }
    },
    handleAddChild(data) {
      this.$emit('addChild', data)
    },
    refresh() {
      this.loadTree()
    }
  }
}
</script>

<style scoped>
.shop-location-tree {
  width: 100%;
}
.tree-card {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tree-node-op {
  float: right;
  margin-right: 6px;
}
</style>
