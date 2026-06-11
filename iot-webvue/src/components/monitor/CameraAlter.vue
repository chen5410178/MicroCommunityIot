<template>
  <div class="camera-alter">
    <div class="panel-title">AI告警</div>
    <div class="panel-content">
      <div class="alter-item" v-for="(item, index) in videoAlters" :key="index">
        <div class="alter-img">
          <img :src="item.imgUrl" />
        </div>
        <div class="alter-info">
          <div class="alter-title">{{ item.alterTitle }}【{{ item.stateMsg }}】</div>
          <div>{{ item.createTime }}</div>
          <div>{{ item.remark }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAiAlter } from '@/api/index/monitorVideoApi'

export default {
  name: 'CameraAlter',
  data() {
    return {
      videoAlters: [],
      timer: null
    }
  },
  mounted() {
    this.loadAlters()
    this.timer = setInterval(() => {
      this.loadAlters()
    }, 30000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
  },
  methods: {
    async loadAlters() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await getAiAlter({ page: 1, row: 10, communityId })
        if (res.code === 0) {
          this.videoAlters = res.data || []
        }
      } catch (e) { /* ignore */ }
    }
  }
}
</script>

<style scoped>
.camera-alter { height: 100%; background-color: #43484E; }
.panel-title { height: 50px; padding-left: 10px; font-size: 16px; line-height: 50px; }
.panel-content { height: calc(100% - 51px); padding: 10px; overflow-y: auto; }
.panel-content::-webkit-scrollbar { width: 4px; }
.panel-content::-webkit-scrollbar-thumb { background: #333; border-radius: 2px; }
.alter-item { display: flex; padding: 10px; background-color: #5b6068; margin-bottom: 10px; font-size: 14px; border-radius: 4px; }
.alter-item img { width: 80px; height: 80px; border-radius: 4px; object-fit: cover; }
.alter-info { margin-left: 10px; }
.alter-title { font-size: 16px; margin-bottom: 4px; }
</style>
