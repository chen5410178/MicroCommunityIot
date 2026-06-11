<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="访客二维码"
    width="400px"
    append-to-body
    @close="handleClose"
  >
    <div style="text-align: center;">
      <div ref="qrCodeDiv" style="display: inline-block; width: 200px; height: 200px;" />
      <p style="margin-top: 16px; color: #606266;">扫描二维码进行访客登记</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'
import { getVisitQrCodeUrl } from '@/api/accessControl/accessControlApi'

export default {
  name: 'VisitQrCode',
  props: {
    visible: { type: Boolean, default: false }
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
          this.loadQrCodeUrl()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  beforeDestroy() {
    this.clearQrCode()
  },
  methods: {
    async loadQrCodeUrl() {
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        const res = await getVisitQrCodeUrl({ communityId: communityInfo.communityId })
        const url = res.data && res.data.url ? res.data.url : res.url || ''
        if (url) {
          this.generateQrCode(url)
        }
      } catch (e) {
        console.error('加载二维码失败', e)
      }
    },
    generateQrCode(url) {
      this.clearQrCode()
      const el = this.$refs.qrCodeDiv
      if (!el) return
      this.qrcodeInstance = new QRCode(el, {
        text: url,
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
