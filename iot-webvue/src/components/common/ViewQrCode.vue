<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="title"
    width="400px"
    append-to-body
    @close="handleClose"
  >
    <div style="text-align: center;">
      <div ref="qrCodeDiv" style="display: inline-block; width: 200px; height: 200px;" />
      <p style="margin-top: 16px; color: #606266;">{{ remark }}</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'

export default {
  name: 'ViewQrCode',
  props: {
    visible: { type: Boolean, default: false },
    title: { type: String, default: '' },
    url: { type: String, default: '' },
    remark: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      qrcodeInstance: null
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => {
          this.generateQrCode()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  beforeDestroy() {
    this.clearQrCode()
  },
  methods: {
    generateQrCode() {
      this.clearQrCode()
      const el = this.$refs.qrCodeDiv
      if (!el || !this.url) return
      this.qrcodeInstance = new QRCode(el, {
        text: this.url,
        width: 200,
        height: 200,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.L
      })
    },
    clearQrCode() {
      if (this.$refs.qrCodeDiv) {
        this.$refs.qrCodeDiv.innerHTML = ''
      }
      this.qrcodeInstance = null
    },
    handleClose() {
      this.dialogVisible = false
      this.clearQrCode()
    }
  }
}
</script>
