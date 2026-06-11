<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="qrcodeTitle"
    width="400px"
  >
    <div style="text-align: center; padding: 10px 0;">
      <div ref="qrcodeRef" style="display: inline-block;" />
      <p style="margin-top: 16px; color: #606266;">{{ remark }}</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcode'

export default {
  name: 'ViewQrCode',
  props: {
    visible: { type: Boolean, default: false },
    qrcodeTitle: { type: String, default: '二维码' },
    url: { type: String, default: '' },
    remark: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.url) {
        this.$nextTick(() => { this.generateQrCode() })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    generateQrCode() {
      const el = this.$refs.qrcodeRef
      if (!el) return
      el.innerHTML = ''
      const canvas = document.createElement('canvas')
      QRCode.toCanvas(canvas, this.url, { width: 200 }, (err) => {
        if (err) { console.error(err); return }
        el.appendChild(canvas)
      })
    }
  }
}
</script>
