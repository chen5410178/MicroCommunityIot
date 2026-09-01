<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="访客二维码"
    width="760px"
    append-to-body
    @close="handleClose"
  >
    <div class="dual-code-content">
      <div class="qr-col">
        <div ref="miniQrcodeRef" class="qr-box">
          <span v-if="!qrData.miniUrl" class="qr-empty">暂无二维码</span>
        </div>
        <p class="qr-name">小程序二维码</p>
        <p class="qr-tip">微信扫码打开小程序登记访客</p>
      </div>
      <div class="qr-col">
        <div ref="publicQrcodeRef" class="qr-box">
          <span v-if="!qrData.url" class="qr-empty">暂无二维码</span>
        </div>
        <p class="qr-name">公众号二维码</p>
        <p class="qr-tip">微信扫码打开公众号登记访客</p>
      </div>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'

export default {
  name: 'BarrierVisitQrCode',
  props: {
    visible: { type: Boolean, default: false },
    qrData: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => {
          this.generateQRCode()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    generateQRCode() {
      const miniEl = this.$refs.miniQrcodeRef
      const publicEl = this.$refs.publicQrcodeRef
      if (!miniEl || !publicEl) return
      miniEl.innerHTML = ''
      publicEl.innerHTML = ''
      this.renderQRCode(miniEl, this.qrData.miniUrl || '')
      this.renderQRCode(publicEl, this.qrData.url || '')
    },
    renderQRCode(el, text) {
      if (!el || !text) return
      new QRCode(el, {
        text,
        width: 200,
        height: 200,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.L
      })
    },
    handleClose() {
      this.dialogVisible = false
      if (this.$refs.miniQrcodeRef) this.$refs.miniQrcodeRef.innerHTML = ''
      if (this.$refs.publicQrcodeRef) this.$refs.publicQrcodeRef.innerHTML = ''
    }
  }
}
</script>

<style scoped>
.dual-code-content {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding: 20px 0;
}
.qr-col {
  text-align: center;
}
.qr-box {
  width: 200px;
  height: 200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.qr-empty {
  font-size: 14px;
  color: #909399;
}
.qr-name {
  margin-top: 12px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}
.qr-tip {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
