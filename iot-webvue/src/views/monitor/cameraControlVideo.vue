<template>
  <div class="camera-control-video">
    <el-row class="toolbar">
      <el-col :span="12">
        <h6 class="page-title">摄像头</h6>
      </el-col>
      <el-col :span="12" class="text-right">
        <el-button-group>
          <el-button
            size="small"
            :type="cameraCount == 4 ? 'primary' : 'default'"
            @click="changeCount(4)"
          >四路</el-button>
          <el-button
            size="small"
            :type="cameraCount == 6 ? 'primary' : 'default'"
            @click="changeCount(6)"
          >六路</el-button>
        </el-button-group>
      </el-col>
    </el-row>
    <el-row class="video-grid" :gutter="12">
      <el-col
        :span="cameraCount == 4 ? 12 : 8"
        v-for="(item, index) in machines"
        :key="item.id"
      >
        <div class="video-slot">
          <div :id="item.id" class="video-container">
            <img v-if="!item.playing" src="/img/init.jpg" style="width:100%;height:300px;object-fit:contain;" />
          </div>
          <div class="video-info">
            <span class="machine-name">{{ item.machineName }}</span>
            <el-button size="small" type="primary" @click="openSelectVideo(item)">选择</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <SelectVideoMachine
      :visible.sync="selectVisible"
      @selected="onMachineSelected"
    />
  </div>
</template>

<script>
import { getPlayVideoUrl, saveVideoPhoto } from '@/api/monitor/monitorApi'
import SelectVideoMachine from '@/components/monitor/SelectVideoMachine.vue'

export default {
  name: 'CameraControlVideo',
  components: { SelectVideoMachine },
  data() {
    return {
      cameraCount: 4,
      machines: [],
      selectVisible: false,
      currentSlot: null
    }
  },
  mounted() {
    const count = parseInt(this.$route.query.cameraCount) || 4
    this.cameraCount = count === 6 ? 6 : 4
    this.initSlots()
  },
  methods: {
    initSlots() {
      this.destroyAllPlayers()
      const slots = []
      for (let i = 0; i < this.cameraCount; i++) {
        slots.push({
          id: 'cameraVideo' + (i + 1) + 'Div',
          machineName: '',
          machineId: '',
          url: '',
          jessibuca: null,
          playing: false
        })
      }
      this.machines = slots
    },
    changeCount(count) {
      if (this.cameraCount === count) return
      this.cameraCount = count
      this.$router.push({ path: '/pages/monitor/cameraControlVideo', query: { cameraCount: count } })
      this.initSlots()
    },
    openSelectVideo(slot) {
      this.currentSlot = slot
      this.selectVisible = true
    },
    async onMachineSelected(machine) {
      if (!this.currentSlot) return
      const slot = this.currentSlot
      slot.machineName = machine.machineName
      slot.machineId = machine.machineId

      if (slot.jessibuca) {
        try { slot.jessibuca.destroy() } catch (e) { /* ignore */ }
        slot.jessibuca = null
      }
      slot.playing = false

      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await getPlayVideoUrl({ communityId, machineId: machine.machineId })
        if (res.code === 0 && res.data) {
          this.playVideo(slot, res.data)
        } else {
          this.$message.error(res.msg || '获取视频地址失败')
        }
      } catch (e) {
        this.$message.error(e.msg || '获取视频地址失败')
      }
    },
    playVideo(slot, url) {
      try {
        if (typeof Jessibuca === 'undefined') {
          this.$message.warning('视频播放库未加载')
          return
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
          supportDblclickFullscreen: true,
          operateBtns: {
            fullscreen: true,
            screenshot: true,
            play: true,
            audio: false,
            recorder: false
          }
        })
        slot.jessibuca = jessibuca
        jessibuca.play(url)
        jessibuca.on('start', () => {
          slot.playing = true
          setTimeout(() => {
            const photo = jessibuca.screenshot('test', 'jpeg', 0.5, 'base64')
            this.savePhoto(slot.machineId, photo)
          }, 1000)
        })
      } catch (e) {
        this.$message.error('视频播放失败')
      }
    },
    async savePhoto(machineId, photo) {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveVideoPhoto({ communityId, machineId, photo })
      } catch (e) { /* ignore */ }
    },
    destroyAllPlayers() {
      this.machines.forEach(slot => {
        if (slot.jessibuca) {
          try { slot.jessibuca.destroy() } catch (e) { /* ignore */ }
          slot.jessibuca = null
        }
      })
    }
  },
  beforeDestroy() {
    this.destroyAllPlayers()
  }
}
</script>

<style scoped>
.camera-control-video { padding: 0; }
.toolbar { margin-bottom: 16px; }
.page-title { margin: 0; font-size: 16px; line-height: 32px; }
.text-right { text-align: right; }
.video-grid { margin-bottom: 0; }
.video-slot { margin-bottom: 16px; }
.video-container { border: 1px solid #dee2e6; width: 100%; min-height: 300px; background: #000; }
.video-info { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; font-size: 14px; }
.machine-name { color: #333; }
</style>
