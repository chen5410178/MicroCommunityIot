<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="title || '二维码'"
    width="400px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <div ref="qrcodeRef" style="width: 200px; height: 200px; margin: 0 auto;" />
      <p style="margin-top: 20px; color: #909399; font-size: 14px;" v-if="remark">{{ remark }}</p>
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
    qrUrl: { type: String, default: '' },
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
      if (val && this.qrUrl) {
        this.$nextTick(() => {
          this._genQrCode()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    _genQrCode() {
      const el = this.$refs.qrcodeRef
      if (!el) return
      el.innerHTML = ''
      const qrcode = new QRCode(el, {
        text: this.qrUrl,
        width: 200,
        height: 200,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.L
      })
    }
  }
}
</script>
