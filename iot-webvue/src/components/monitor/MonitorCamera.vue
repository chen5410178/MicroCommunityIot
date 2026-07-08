<template>
  <div class="monitor-camera">
    <div class="panel-title">设备列表</div>
    <div class="panel-content">
      <VideoTree @select-video="onSelectVideo" />
    </div>
    <div class="panel-title">收藏视频</div>
    <div class="panel-fav">
      <div class="fav-item" v-for="(item, index) in collects" :key="index" @click="onSelectCollect(item)">
        <i class="el-icon-video-camera"></i>
        <span>{{ item.machineName }}</span>
        <i class="el-icon-delete del-icon" @click.stop="handleDelete(item)"></i>
      </div>
      <div v-if="collects.length === 0" class="fav-empty">暂无收藏</div>
    </div>
    <DeleteMachineCollect
      :visible.sync="delVisible"
      :del-data="delData"
      @success="loadCollects"
    />
  </div>
</template>

<script>
import VideoTree from './VideoTree.vue'
import DeleteMachineCollect from './DeleteMachineCollect.vue'
import { listMonitorCollects } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MonitorCamera',
  components: { VideoTree, DeleteMachineCollect },
  data() {
    return {
      collects: [],
      delVisible: false,
      delData: null
    }
  },
  mounted() {
    this.loadCollects()
  },
  methods: {
    onSelectVideo(machine) {
      this.$emit('select-video', machine)
    },
    onSelectCollect(item) {
      this.$emit('select-video', {
        machineId: item.machineId,
        machineName: item.machineName
      })
    },
    async loadCollects() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorCollects({ page: 1, row: 1000, communityId })
        if (res.code === 0) {
          this.collects = res.data || []
        }
      } catch (e) { /* ignore */ }
    },
    handleDelete(item) {
      this.delData = item
      this.delVisible = true
    }
  }
}
</script>

<style scoped>
.monitor-camera { height: 100%; background-color: #43484E; display: flex; flex-direction: column; }
.panel-title { height: 50px; padding-left: 10px; font-size: 16px; line-height: 50px; flex-shrink: 0; }
.panel-content { flex: 1; overflow-y: auto; margin-top: 1px; background-color: #43484E; }
.panel-content::-webkit-scrollbar { width: 4px; }
.panel-content::-webkit-scrollbar-thumb { background: #333; border-radius: 2px; }
.panel-fav { flex: 1; overflow-y: auto; padding: 10px 10px; background-color: #43484E; }
.panel-fav::-webkit-scrollbar { width: 4px; }
.panel-fav::-webkit-scrollbar-thumb { background: #333; border-radius: 2px; }
.fav-item { padding: 8px 10px; cursor: pointer; font-size: 14px; display: flex; align-items: center; }
.fav-item:hover { background-color: #5b6068; }
.fav-item .el-icon-video-camera { margin-right: 6px; color: #409eff; }
.fav-item .del-icon { margin-left: auto; color: #f56c6c; display: none; }
.fav-item:hover .del-icon { display: inline; }
.fav-empty { color: #909399; text-align: center; padding: 20px 0; font-size: 14px; }
</style>
