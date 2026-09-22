<template>
  <div class="monitor-view" v-if="slots.length > 0">
    <div class="view-top">
      <div class="view-top-main">
        <div class="slot-item" :class="slots[0].active" @click="changeSelectSlot(1)">
          <div :id="slots[0].id" class="slot-inner">通道1</div>
        </div>
      </div>
      <div class="view-top-side">
        <div class="side-slot" v-for="(slot, index) in slots.slice(1, 4)" :key="slot.id">
          <div class="slot-item" :class="slot.active" @click="changeSelectSlot(index + 2)">
            <div :id="slot.id" class="slot-inner">通道{{ index + 2 }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="view-bottom">
      <div class="bottom-slot" v-for="(slot, index) in slots.slice(4, 8)" :key="slot.id">
        <div class="slot-item" :class="slot.active" @click="changeSelectSlot(index + 5)">
          <div :id="slot.id" class="slot-inner">通道{{ index + 5 }}</div>
        </div>
      </div>
    </div>
    <div class="view-toolbar">
      <div class="toolbar-left">
        <img src="/img/1-camera.png" @click="toScreen(1)" />
        <img src="/img/4-camera.png" @click="toScreen(4)" />
        <img src="/img/8s-camera.png" @click="toScreen(8)" />
        <img src="/img/9-camera.png" @click="toScreen(9)" />
      </div>
      <div>数据来源于MVideo监控平台</div>
    </div>
  </div>
</template>

<script>
import { getPlayVideoUrl, saveVideoPhoto } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'
import { loadScriptOnce, JESSIBUCA_URL } from '@/utils/asyncScript'

export default {
  name: 'MonitorView',
  data() {
    return {
      slots: [],
      currentIndex: 1
    }
  },
  mounted() {
    this.initSlots()
  },
  beforeDestroy() {
    this.destroyAllPlayers()
  },
  methods: {
    initSlots() {
      this.slots = []
      for (let i = 1; i <= 8; i++) {
        this.slots.push({
          id: 'monitor-channel-' + i,
          active: '',
          machineId: '',
          machineName: '',
          url: '',
          jessibuca: null
        })
      }
      this.changeSelectSlot(1)
    },
    changeSelectSlot(index) {
      this.slots.forEach(s => { s.active = '' })
      this.slots[index - 1].active = 'camera-view-select'
      this.currentIndex = index
    },
    getActiveSlot() {
      return this.slots.find(s => s.active === 'camera-view-select') || {}
    },
    playCameraVideo(machineId) {
      const slot = this.getActiveSlot()
      if (!slot || !slot.id) return
      this.fetchAndPlay(slot, machineId)
      this.advanceSlot()
    },
    async fetchAndPlay(slot, machineId) {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await getPlayVideoUrl({ communityId, machineId })
        if (res.code === 0 && res.data) {
          slot.url = res.data
          slot.machineId = machineId
          this.playVideo(slot)
        }
      } catch (e) { /* ignore */ }
    },
    async playVideo(slot) {
      try {
        await loadScriptOnce(JESSIBUCA_URL)
      } catch (e) {
        return
      }
      if (typeof Jessibuca === 'undefined') return
      if (slot.jessibuca) {
        try { slot.jessibuca.destroy() } catch (e) { /* ignore */ }
      }
      const container = document.getElementById(slot.id)
      const jessibuca = new Jessibuca({
        container: container,
        videoBuffer: 0.2,
        isResize: false,
        text: '',
        loadingText: '加载中...',
        useMSE: false,
        debug: false,
        hasAudio: false,
        isNotMute: false,
        supportDblclickFullscreen: true
      })
      slot.jessibuca = jessibuca
      jessibuca.play(slot.url)
      jessibuca.on('start', () => {
        setTimeout(() => {
          try {
            const photo = jessibuca.screenshot('test', 'jpeg', 0.5, 'base64')
            this.savePhoto(slot.machineId, photo)
          } catch (e) { /* ignore */ }
        }, 1000)
      })
    },
    async savePhoto(machineId, photo) {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveVideoPhoto({ communityId, machineId, photo })
      } catch (e) { /* ignore */ }
    },
    advanceSlot() {
      const idx = this.currentIndex
      if (idx >= 8) {
        this.changeSelectSlot(1)
      } else {
        this.changeSelectSlot(idx + 1)
      }
    },
    toScreen(count) {
      this.$router.push({ path: '/pages/index/monitorVideo', query: { screenCount: count } })
    },
    destroyAllPlayers() {
      this.slots.forEach(slot => {
        if (slot.jessibuca) {
          try { slot.jessibuca.destroy() } catch (e) { /* ignore */ }
          slot.jessibuca = null
        }
      })
    }
  }
}
</script>

<style scoped>
.monitor-view { width: 100%; height: 100%; font-size: 24px; display: flex; flex-direction: column; }
.view-top { display: flex; width: 100%; height: 70vh; padding-bottom: 5px; }
.view-top-main { width: 75%; height: 100%; }
.view-top-main .slot-item { background-color: #43484E; height: 100%; display: flex; flex-direction: column; justify-content: center; text-align: center; }
.view-top-side { width: 25%; height: 100%; padding-left: 5px; display: flex; flex-direction: column; }
.side-slot { flex: 1; padding-bottom: 5px; }
.side-slot:last-child { padding-bottom: 0; }
.side-slot .slot-item { background-color: #43484E; height: 100%; display: flex; flex-direction: column; justify-content: center; text-align: center; }
.view-bottom { display: flex; width: 100%; height: 25vh; padding-bottom: 5px; }
.bottom-slot { width: 25%; padding-right: 5px; }
.bottom-slot:last-child { padding-right: 0; padding-left: 5px; }
.bottom-slot .slot-item { background-color: #43484E; height: 100%; display: flex; flex-direction: column; justify-content: center; text-align: center; }
.slot-inner { height: 100%; min-height: 80px; display: flex; align-items: center; justify-content: center; }
.view-toolbar { width: 100%; height: 5vh; background-color: #43484E; display: flex; align-items: center; justify-content: space-between; padding: 0 10px; font-size: 14px; flex-shrink: 0; }
.toolbar-left { display: flex; align-items: center; }
.toolbar-left img { width: 25px; height: 25px; margin-right: 10px; cursor: pointer; }
.camera-view-select { border: 2px solid #1296DB; }
</style>
