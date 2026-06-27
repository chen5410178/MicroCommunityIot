<template>
  <div class="slider-verify">
    <div class="slider-bg-wrap" ref="bgWrap">
      <img :src="bgImage" class="slider-bg" />
      <img :src="sliderImage" class="slider-block" :style="blockStyle" />
    </div>
    <div class="slider-bar" ref="sliderBar">
      <div class="slider-btn" ref="sliderBtn"
           @mousedown="onDragStart" @touchstart.prevent="onDragStart">
      </div>
      <span class="slider-text">{{ sliderText }}</span>
    </div>
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
      sliderText: '按住滑块，拖动到最右边',
      dragging: false,
      startX: 0,
      currentX: 0,
      verified: false
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
    document.addEventListener('mousemove', this.onDragMove)
    document.addEventListener('mouseup', this.onDragEnd)
    document.addEventListener('touchmove', this.onDragMove, { passive: false })
    document.addEventListener('touchend', this.onDragEnd)
  },
  beforeDestroy() {
    document.removeEventListener('mousemove', this.onDragMove)
    document.removeEventListener('mouseup', this.onDragEnd)
    document.removeEventListener('touchmove', this.onDragMove)
    document.removeEventListener('touchend', this.onDragEnd)
  },
  methods: {
    loadSliderCode() {
      generatorSliderCode({ _uId: '123' }).then(res => {
        if (typeof res === 'string') {
          try { res = JSON.parse(res) } catch (e) {}
        }
        this.bgImage = res.bgImage
        this.sliderImage = res.sliderImage
        this.sliderY = res.y
        this.sliderWidth = res.sliderWidth
        this.sliderHeight = res.sliderHeight
        this.bgWidth = 300
        this.currentX = 0
        this.verified = false
        this.sliderText = '按住滑块，拖动到最右边'
      }).catch(() => {
        this.$emit('refresh')
      })
    },
    refresh() {
      this.loadSliderCode()
    },
    onDragStart(e) {
      if (this.verified) return
      this.dragging = true
      const clientX = e.clientX || (e.touches && e.touches[0].clientX)
      this.startX = clientX
    },
    onDragMove(e) {
      if (!this.dragging || this.verified) return
      const clientX = e.clientX || (e.touches && e.touches[0].clientX)
      const dx = clientX - this.startX
      const maxX = this.bgWidth - this.sliderWidth
      this.currentX = Math.max(0, Math.min(dx, maxX))
      const btn = this.$refs.sliderBtn
      if (btn) {
        btn.style.left = this.currentX + 'px'
      }
    },
    onDragEnd() {
      if (!this.dragging || this.verified) return
      this.dragging = false
      const btn = this.$refs.sliderBtn
      if (btn) {
        btn.style.left = '0px'
      }
      if (this.currentX > 0) {
        const sliderX = Math.round(this.currentX)
        this.$emit('success', sliderX)
      }
    }
  }
}
</script>

<style scoped>
.slider-verify {
  width: 300px;
  margin: 0 auto;
}
.slider-bg-wrap {
  position: relative;
  width: 300px;
  height: 150px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 10px;
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
.slider-bar {
  position: relative;
  width: 300px;
  height: 40px;
  background: #e8e8e8;
  border-radius: 20px;
  overflow: hidden;
  user-select: none;
}
.slider-text {
  position: absolute;
  width: 100%;
  height: 100%;
  line-height: 40px;
  text-align: center;
  color: #999;
  font-size: 14px;
  pointer-events: none;
}
.slider-btn {
  position: absolute;
  top: 0;
  left: 0;
  width: 40px;
  height: 40px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 20px;
  cursor: pointer;
  z-index: 3;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}
.slider-btn:hover {
  background: #3158e5;
  border-color: #3158e5;
}
</style>
