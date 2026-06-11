<template>
  <div class="community-selector">
    <ul class="community-list">
      <li
        v-for="(item, index) in communities"
        :key="index"
        :class="{ 'community-item-selected': currentCommunityId === item.communityId }"
        class="community-item"
        @click="handleSelect(item)"
      >
        {{ item.name }}
      </li>
    </ul>
  </div>
</template>

<script>
import { listCommunitys } from '@/api/community/communityApi'

export default {
  name: 'CommunitySelector',
  data() {
    return {
      communities: [{ name: '全部小区', communityId: '' }],
      currentCommunityId: ''
    }
  },
  mounted() {
    this.loadCommunities()
  },
  methods: {
    async loadCommunities() {
      try {
        const res = await listCommunitys({ page: 1, row: 100 })
        if (res.data) {
          this.communities = [{ name: '全部小区', communityId: '' }, ...res.data]
        }
        this.handleSelect(this.communities[0])
      } catch (e) {
        console.error('加载小区列表失败', e)
      }
    },
    handleSelect(item) {
      this.currentCommunityId = item.communityId
      this.$emit('change', item)
    }
  }
}
</script>

<style scoped>
.community-selector {
  height: 100%;
  min-height: 400px;
  overflow-y: auto;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  background: #fff;
}
.community-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.community-item {
  padding: 10px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #303133;
  transition: background-color 0.2s;
}
.community-item:hover {
  background-color: #f5f7fa;
}
.community-item-selected {
  background-color: #ecf5ff;
  color: #409EFF;
}
</style>
