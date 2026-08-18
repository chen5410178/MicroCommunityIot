<template>
  <div class="shop-location-tree">
    <el-card shadow="never" class="tree-card">
      <div slot="header" class="card-header" v-if="showActions">
        <div class="header-actions">
          <el-tooltip content="新增位置" placement="top">
            <el-button type="text" size="small" icon="el-icon-plus" @click="handleAdd" />
          </el-tooltip>
          <el-tooltip content="修改位置" placement="top">
            <el-button type="text" size="small" icon="el-icon-edit" @click="handleEdit" />
          </el-tooltip>
          <el-tooltip content="删除位置" placement="top">
            <el-button type="text" size="small" icon="el-icon-delete" style="color: #f56c6c" @click="handleDelete" />
          </el-tooltip>
        </div>
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
      />
    </el-card>
  </div>
</template>

<script>
import { listShopLocations } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ShopLocationTree',
  props: {
    showAll: { type: Boolean, default: true },
    showActions: { type: Boolean, default: true }
  },
  data() {
    return {
      treeData: [],
      currentNode: null
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
        const locations = res.data || []
        this.treeData = this.showAll ? [{ locationId: 'all', locationName: '全部' }, ...locations] : locations
        this.$nextTick(() => {
          if (this.$refs.tree) {
            this.$refs.tree.setCurrentKey(this.showAll ? 'all' : '')
            this.currentNode = this.showAll ? { locationId: 'all', locationName: '全部' } : null
          }
        })
      } catch (e) {
        /* 加载失败 */
      }
    },
    handleNodeClick(data) {
      this.currentNode = data
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
    handleAdd() {
      if (!this.currentNode || this.currentNode.locationId === 'all') {
        this.$emit('add', { addType: 'top' })
      } else {
        this.$emit('add', {
          addType: 'child',
          locationId: this.currentNode.locationId,
          locationName: this.currentNode.locationName,
          level: this.currentNode.level
        })
      }
    },
    handleEdit() {
      if (!this.currentNode || this.currentNode.locationId === 'all') {
        this.$message.warning('请先选择要修改的位置')
        return
      }
      this.$emit('edit', this.currentNode)
    },
    handleDelete() {
      if (!this.currentNode || this.currentNode.locationId === 'all') {
        this.$message.warning('请先选择要删除的位置')
        return
      }
      this.$emit('delete', this.currentNode)
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
.header-actions {
  display: flex;
  align-items: center;
}
</style>
