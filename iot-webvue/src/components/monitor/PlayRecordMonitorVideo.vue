<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="videoInfo.machineName || '录像播放'"
    width="800px"
    top="5vh"
    @close="handleClose"
  >
    <div id="recordVideoContainer" style="width: 100%; min-height: 400px; background: #000; display: flex; align-items: center; justify-content: center;">
      <img v-if="!started" src="/img/init.jpg" style="width: 100%; height: 300px; object-fit: contain;" />
    </div>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getRecordPlayVideoUrl } from '@/api/monitor/monitorApi'

export default {
  name: 'PlayRecordMonitorVideo',
  props: {
    visible: { type: Boolean, default: false },
    videoInfo: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false,
      started: false,
      jessibuca: null
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => { this.fetchVideoUrl() })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchVideoUrl() {
      try {
        const communityId = this.videoInfo.communityId || this.$store.state.app.currentCommunity.communityId || ''
        const res = await getRecordPlayVideoUrl({ communityId, machineId: this.videoInfo.machineId, rvId: this.videoInfo.rvId })
        if (res.code === 0 && res.data) {
          this.playVideo(res.data)
        } else {
          this.$message.error(res.msg || '获取录像地址失败')
        }
      } catch (e) {
        this.$message.error(e.msg || '获取录像地址失败')
      }
    },
    playVideo(url) {
      try {
        if (typeof Jessibuca === 'undefined') {
          this.$message.warning('视频播放库未加载')
          return
        }
        if (this.jessibuca) {
          try { this.jessibuca.destroy() } catch (e) { /* ignore */ }
        }
        const container = document.getElementById('recordVideoContainer')
        const jessibuca = new Jessibuca({
          container: container,
          videoBuffer: 0.2,
          isResize: false,
          text: '',
          loadingText: '加载中...',
          useMSE: false,
          debug: false,
          isNotMute: false,
          supportDblclickFullscreen: true,
          operateBtns: {
            fullscreen: true,
            screenshot: false,
            play: true,
            audio: false,
            recorder: false
          }
        })
        this.jessibuca = jessibuca
        jessibuca.play(url)
        jessibuca.on('start', () => {
          this.started = true
        })
      } catch (e) {
        this.$message.error('视频播放失败')
      }
    },
    handleClose() {
      if (this.jessibuca) {
        try { this.jessibuca.destroy() } catch (e) { /* ignore */ }
        this.jessibuca = null
      }
      this.started = false
      this.dialogVisible = false
    }
  }
}
</script>
