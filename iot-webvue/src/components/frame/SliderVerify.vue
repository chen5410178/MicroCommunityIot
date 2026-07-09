<template>
  <div class="slider-verify-compact">
    <div class="slider-bar" :class="{ verified: verified, failed: loadFailed }" @mousedown="openDialog" @touchstart.prevent="openDialog">
      <div class="slider-btn"></div>
      <span class="slider-text">{{ loadFailed ? '验证码加载失败，请刷新页面' : (verified ? '验证通过' : '请按住滑块拖动') }}</span>
    </div>

    <el-dialog
      title="请完成安全验证"
      :visible.sync="showDialog"
      width="370px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      append-to-body
      @close="closeDialog"
    >
      <div class="slider-bg-wrap" ref="dialogBgWrap">
        <img :src="bgImage" class="slider-bg" />
        <img :src="sliderImage" class="slider-block" :style="blockStyle" />
      </div>
      <div class="slider-dialog-bar" ref="dialogSliderBar">
        <div class="slider-dialog-btn" ref="dialogSliderBtn"
             @mousedown="onDialogDragStart" @touchstart.prevent="onDialogDragStart">
        </div>
        <span class="slider-text">{{ dialogSliderText }}</span>
      </div>
      <div class="slider-dialog-tip">拖动滑块完成拼图</div>
    </el-dialog>
  </div>
</template>

<script>
import { generatorSliderCode } from '@/api/login/loginApi'

export default {
  name: 'SliderVerify',
  data() {
    return {
      bgImage: '',
      sliderImage: '',
      sliderY: 0,
      sliderWidth: 50,
      sliderHeight: 50,
      bgWidth: 300,
      showDialog: false,
      dialogSliderText: '向右拖动滑块完成验证',
      dragging: false,
      startX: 0,
      currentX: 0,
      verified: false,
      loadFailed: false,
      sliderKey: null
    }
  },
  computed: {
    blockStyle() {
      return {
        top: this.sliderY + 'px',
        left: this.currentX + 'px'
      }
    }
  },
  mounted() {
    this.loadSliderCode()
  },
  methods: {
    loadSliderCode() {
      this.loadFailed = false
      generatorSliderCode({ _uId: '123' }).then(res => {
        if (typeof res === 'string') {
          try { res = JSON.parse(res) } catch (e) {}
        }
        this.bgImage = res.bgImage
        this.sliderImage = res.sliderImage
        this.sliderY = res.y
        this.sliderWidth = res.sliderWidth
        this.sliderHeight = res.sliderHeight
        this.sliderKey = res.sliderKey
        this.bgWidth = 300
        this.currentX = 0
        this.verified = false
        this.loadFailed = false
        this.dialogSliderText = '向右拖动滑块完成验证'
      }).catch(() => {
        this.loadFailed = true
        this.$emit('refresh')
      })
    },
    refresh() {
      this.loadSliderCode()
    },
    openDialog() {
      if (this.verified || this.loadFailed) return
      this.showDialog = true
      this.currentX = 0
      this.$nextTick(() => {
        const btn = this.$refs.dialogSliderBtn
        if (btn) btn.style.left = '0px'
      })
    },
    closeDialog() {
      if (this.dragging) return
      this.showDialog = false
    },
    onDialogDragStart(e) {
      if (this.verified) return
      this.dragging = true
      const clientX = e.clientX || (e.touches && e.touches[0].clientX)
      this.startX = clientX
      document.addEventListener('mousemove', this.onDialogDragMove)
      document.addEventListener('mouseup', this.onDialogDragEnd)
      document.addEventListener('touchmove', this.onDialogDragMove, { passive: false })
      document.addEventListener('touchend', this.onDialogDragEnd)
    },
    onDialogDragMove(e) {
      if (!this.dragging || this.verified) return
      e.preventDefault()
      const clientX = e.clientX || (e.touches && e.touches[0].clientX)
      const dx = clientX - this.startX
      const maxX = this.bgWidth - this.sliderWidth
      this.currentX = Math.max(0, Math.min(dx, maxX))
      const btn = this.$refs.dialogSliderBtn
      if (btn) {
        btn.style.left = this.currentX + 'px'
      }
    },
    onDialogDragEnd() {
      if (!this.dragging || this.verified) return
      this.dragging = false
      document.removeEventListener('mousemove', this.onDialogDragMove)
      document.removeEventListener('mouseup', this.onDialogDragEnd)
      document.removeEventListener('touchmove', this.onDialogDragMove)
      document.removeEventListener('touchend', this.onDialogDragEnd)

      const btn = this.$refs.dialogSliderBtn
      if (btn) {
        btn.style.left = '0px'
      }
      if (this.currentX > 0) {
        const sliderX = Math.round(this.currentX)
        this.verified = true
        this.showDialog = false
        this.$emit('success', { sliderX, sliderKey: this.sliderKey })
      } else {
        // 没拖动，重置位置
        this.currentX = 0
      }
    }
  }
}
</script>

<style scoped>
.slider-verify-compact {
  width: 100%;
  margin: 0 auto;
}
.slider-verify-compact .slider-bar {
  position: relative;
  width: 100%;
  height: 48px;
  background: #e8e8e8;
  border-radius: 5px;
  overflow: hidden;
  user-select: none;
  cursor: pointer;
  margin-bottom: 0;
}
.slider-verify-compact .slider-bar .slider-text {
  position: absolute;
  width: 100%;
  height: 100%;
  line-height: 48px;
  text-align: center;
  color: #999;
  font-size: 14px;
  pointer-events: none;
}
.slider-verify-compact .slider-bar .slider-btn {
  position: absolute;
  top: 0;
  left: 0;
  width: 48px;
  height: 48px;
  background: #fff;
  border: 1px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
  z-index: 3;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: background 0.2s;
  margin-bottom: 0;
}
.slider-verify-compact .slider-bar .slider-btn:hover {
  background: #f0f0f0;
  border-color: #aaa;
}
.slider-verify-compact .slider-bar.verified .slider-text {
  color: #3158e5;
}
.slider-verify-compact .slider-bar.failed .slider-text {
  color: #f56c6c;
}
.slider-verify-compact .slider-bar.failed {
  background: #fef0f0;
  border: 1px solid #f56c6c;
  cursor: default;
}

/* dialog content */
.slider-bg-wrap {
  position: relative;
  width: 300px;
  height: 150px;
  overflow: hidden;
  border-radius: 4px;
  margin: 0 auto 10px auto;
  background: #eee;
}
.slider-bg {
  width: 300px;
  height: 150px;
  display: block;
}
.slider-block {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 2;
  pointer-events: none;
}
.slider-dialog-bar {
  position: relative;
  width: 300px;
  height: 48px;
  background: #e8e8e8;
  border-radius: 5px;
  overflow: hidden;
  user-select: none;
  margin: 0 auto;
}
.slider-dialog-bar .slider-text {
  position: absolute;
  width: 100%;
  height: 100%;
  line-height: 48px;
  text-align: center;
  color: #999;
  font-size: 14px;
  pointer-events: none;
}
.slider-dialog-btn {
  position: absolute;
  top: 0;
  left: 0;
  width: 48px;
  height: 48px;
  background: #fff;
  border: 1px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
  z-index: 3;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: background 0.2s;
}
.slider-dialog-btn:hover {
  background: #f0f0f0;
  border-color: #aaa;
}
.slider-dialog-tip {
  text-align: center;
  color: #bbb;
  font-size: 12px;
  margin-top: 8px;
}
</style>
