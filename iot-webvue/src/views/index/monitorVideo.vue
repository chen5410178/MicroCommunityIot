<template>
  <div class="monitor-video">
    <div class="left-panel">
      <MonitorCamera @select-video="onSelectVideo" />
    </div>
    <div class="center-panel">
      <component :is="currentViewComponent" ref="videoView" />
    </div>
    <div class="right-panel">
      <CameraAlter />
    </div>
  </div>
</template>

<script>
import MonitorCamera from '@/components/monitor/MonitorCamera.vue'
import MonitorView from '@/components/monitor/MonitorView.vue'
import MonitorViewOne from '@/components/monitor/MonitorViewOne.vue'
import MonitorViewFour from '@/components/monitor/MonitorViewFour.vue'
import MonitorViewNine from '@/components/monitor/MonitorViewNine.vue'
import CameraAlter from '@/components/monitor/CameraAlter.vue'

export default {
  name: 'MonitorVideo',
  components: {
    MonitorCamera,
    MonitorView,
    MonitorViewOne,
    MonitorViewFour,
    MonitorViewNine,
    CameraAlter
  },
  data() {
    return {
      screenCount: 8
    }
  },
  computed: {
    currentViewComponent() {
      const map = {
        1: 'MonitorViewOne',
        4: 'MonitorViewFour',
        8: 'MonitorView',
        9: 'MonitorViewNine'
      }
      return map[this.screenCount] || 'MonitorView'
    }
  },
  watch: {
    '$route.query.screenCount': {
      immediate: true,
      handler(val) {
        const count = parseInt(val) || 8
        if ([1, 4, 8, 9].indexOf(count) >= 0) {
          this.screenCount = count
        } else {
          this.screenCount = 8
        }
      }
    }
  },
  methods: {
    onSelectVideo(machine) {
      if (this.$refs.videoView && this.$refs.videoView.playCameraVideo) {
        this.$refs.videoView.playCameraVideo(machine.machineId)
      }
    }
  }
}
</script>

<style scoped>
.monitor-video { background-color: #333; width: calc(100vw - 200px); height: calc(100vh - 60px); position: fixed; top: 60px; left: 170px; display: flex; color: #f0f0f0; z-index: 100; }
.left-panel { width: 250px; height: 100%; flex-shrink: 0; }
.center-panel { flex: 1; height: 100%; padding: 0 5px; overflow: hidden; }
.right-panel { width: 300px; height: 100%; flex-shrink: 0; }
</style>
