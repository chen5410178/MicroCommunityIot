<template>
  <el-card shadow="never" class="tree-card">
    <div slot="header" class="card-header">
      <span>地区树</span>
    </div>
    <div class="tree-actions">
      <el-button v-if="!isAreaLevel303" type="primary" size="small" @click="handleAdd">添加</el-button>
      <el-button v-if="!isRoot" type="primary" size="small" @click="handleEdit">修改</el-button>
      <el-button v-if="!isRoot" type="danger" size="small" @click="handleDelete">删除</el-button>
    </div>
    <el-tree
      ref="treeRef"
      :data="treeData"
      node-key="areaCode"
      :props="treeProps"
      highlight-current
      :expand-on-click-node="false"
      class="margin-top"
      @node-click="handleNodeClick"
    />
  </el-card>
</template>

<script>
import { queryAreaTree } from '@/api/common/cityAreaApi'

export default {
  name: 'CityAreaTree',
  data() {
    return {
      treeData: [],
      treeProps: { label: 'text', children: 'children' },
      currentNode: null
    }
  },
  computed: {
    isRoot() {
      return !this.currentNode || this.currentNode.areaCode === '0'
    },
    isAreaLevel303() {
      return this.currentNode && this.currentNode.areaLevel === '303'
    }
  },
  mounted() {
    this.loadTree()
  },
  methods: {
    async loadTree() {
      try {
        const res = await queryAreaTree({ hc: 1.8 })
        this.treeData = res.data || []
      } catch { /* ignore */ }
    },
    handleNodeClick(data) {
      this.currentNode = data
      this.$emit('node-click', data)
    },
    handleAdd() {
      if (!this.currentNode || !this.currentNode.areaCode) {
        this.$message.warning('请先选择地区')
        return
      }
      const areaLevel = this.currentNode.areaLevel
      let nextLevel
      if (areaLevel === '101') {
        nextLevel = '202'
      } else if (areaLevel === '202') {
        nextLevel = '303'
      } else if (areaLevel === '303') {
        this.$message.warning('不能添加子节点')
        return
      } else {
        nextLevel = '101'
      }
      this.$emit('open-add', {
        parentAreaCode: this.currentNode.areaCode,
        parentAreaName: this.currentNode.text,
        areaLevel: nextLevel
      })
    },
    handleEdit() {
      if (!this.currentNode || !this.currentNode.areaCode) {
        this.$message.warning('请先选择地区')
        return
      }
      if (this.currentNode.areaCode === '0') {
        this.$message.warning('根节点不能修改')
        return
      }
      this.$emit('open-edit', this.currentNode)
    },
    handleDelete() {
      if (!this.currentNode || !this.currentNode.areaCode) {
        this.$message.warning('请先选择地区')
        return
      }
      if (this.currentNode.areaCode === '0') {
        this.$message.warning('根节点不能删除')
        return
      }
      this.$emit('open-delete', this.currentNode)
    },
    refresh() {
      this.loadTree()
    }
  }
}
</script>

<style scoped>
.tree-card {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tree-actions {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}
.margin-top {
  margin-top: 0;
}
</style>
