<template>
  <div class="parking-area-region-tree">
    <el-tree
      v-if="treeData.length > 0"
      :data="treeData"
      :props="defaultProps"
      node-key="id"
      :expand-on-click-node="false"
      default-expand-all
      highlight-current
      @node-click="handleNodeClick"
    >
      <template slot-scope="{ data }">
        <span class="custom-tree-node">
          <i :class="data.icon" style="margin-right: 5px"></i>
          <span>{{ data.text }}</span>
        </span>
      </template>
    </el-tree>
    <div v-else class="no-data">暂无停车场数据</div>
  </div>
</template>

<script>
import { queryParkingAreaAndRegions } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaRegionTree',
  data() {
    return {
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'text'
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    refreshTree() {
      this.loadData()
    },
    async loadData() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await queryParkingAreaAndRegions({ communityId })
        this.treeData = this.transformData(res.data || [])
      } catch (e) {
        console.error('加载停车场区域树失败', e)
      }
    },
    transformData(list) {
      const treeData = []
      const paMap = {}
      list.forEach(item => {
        if (!paMap[item.paId]) {
          paMap[item.paId] = {
            id: `pa_${item.paId}`,
            paId: item.paId,
            num: item.num,
            icon: 'el-icon-office-building',
            text: item.num,
            children: []
          }
          treeData.push(paMap[item.paId])
        }
        if (item.prId) {
          paMap[item.paId].children.push({
            id: `pr_${item.prId}`,
            prId: item.prId,
            paId: item.paId,
            regionCode: item.regionCode,
            num: item.num,
            text: item.regionCode || item.prId,
            icon: 'el-icon-map-location'
          })
        }
      })
      return treeData
    },
    handleNodeClick(data) {
      if (data.id.startsWith('pa_')) {
        this.$emit('switch', {
          paId: data.paId,
          prId: '',
          regionCode: '',
          num: data.num,
          type: 'pa'
        })
      } else {
        this.$emit('switch', {
          paId: data.paId,
          prId: data.prId,
          regionCode: data.regionCode,
          num: data.num,
          type: 'region'
        })
      }
    }
  }
}
</script>

<style scoped>
.parking-area-region-tree {
  min-height: 300px;
  padding-bottom: 20px;
}
.custom-tree-node {
  display: flex;
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}
.no-data {
  text-align: center;
  padding: 20px;
  color: #909399;
}
</style>
