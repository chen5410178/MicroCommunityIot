<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="月卡购买二维码"
    width="400px"
    @close="handleClose"
  >
    <div style="text-align: center; padding: 20px 0;">
      <div ref="qrCodeDiv" style="width: 200px; height: 200px; margin: 0 auto;" />
      <p style="margin-top: 20px; color: #909399; font-size: 14px;">请截图贴在岗亭门口</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'
import { getBuyCarMonthCardQrCodeUrl } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'BuyMonthCardQrCode',
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await getBuyCarMonthCardQrCodeUrl({ communityId })
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
