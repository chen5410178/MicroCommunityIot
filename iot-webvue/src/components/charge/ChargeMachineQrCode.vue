<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="充电桩二维码"
    :width="isDualCode ? '760px' : '400px'"
    @close="handleClose"
  >
    <!-- 双码场景：小程序二维码 + 公众号二维码 -->
    <div v-if="isDualCode" class="dual-code-content">
      <div class="qr-col">
        <div ref="miniQrcodeRef" class="qr-box" />
        <p class="qr-name">小程序二维码</p>
        <p class="qr-tip">微信扫码打开小程序充电</p>
      </div>
      <div class="qr-col">
        <div ref="publicQrcodeRef" class="qr-box" />
        <p class="qr-name">公众号二维码</p>
        <p class="qr-tip">微信扫码打开公众号充电</p>
      </div>
    </div>
    <!-- 单码兼容场景：端口管理页复用 -->
    <div v-else style="text-align: center; padding: 20px 0;">
      <div ref="qrcodeRef" style="display: inline-block" />
      <p style="margin-top: 16px; font-size: 14px; color: #303133;">{{ (qrData && qrData.machineName) || '' }}</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'
import { getChargeMachineQrCodeUrl } from '@/api/charge/chargeApi'

export default {
  name: 'ChargeMachineQrCode',
  props: {
    visible: { type: Boolean, default: false },
    qrData: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false,
      miniQrCode: null,
      publicQrCode: null
    }
  },
  computed: {
    isDualCode() {
      return !!(this.qrData && this.qrData.machineCode)
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => {
          if (this.isDualCode) {
            this.generateDualQRCode()
          } else {
            this.generateQRCode()
          }
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async generateDualQRCode() {
      const miniEl = this.$refs.miniQrcodeRef
      const publicEl = this.$refs.publicQrcodeRef
      if (!miniEl || !publicEl) return
      miniEl.innerHTML = ''
      publicEl.innerHTML = ''
      try {
        const res = await getChargeMachineQrCodeUrl({ machineCode: this.qrData.machineCode })
        if (res.code === 0 && res.data) {
          this.renderQRCode(miniEl, res.data.miniUrl || '')
          this.renderQRCode(publicEl, res.data.url || '')
          if (!res.data.miniUrl) {
            this.$message.error('获取小程序二维码失败')
          }
          if (!res.data.url) {
            this.$message.error('获取公众号二维码失败')
          }
        } else {
          throw new Error(res.msg || '获取二维码失败')
        }
      } catch (e) {
        this.$message.error(e.message || '获取二维码失败')
        publicEl.innerHTML = '<span style="color: #909399; font-size: 14px;">获取二维码失败</span>'
        this.renderQRCode(miniEl, this.qrData.qrCode || this.qrData.machineCode || '')
      }
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
    generateQRCode() {
      const el = this.$refs.qrcodeRef
      if (!el || !this.qrData) return
      el.innerHTML = ''
      new QRCode(el, {
        text: this.qrData.qrCode || this.qrData.machineCode || '',
        width: 200,
        height: 200,
        colorDark: '#000000',
        colorLight: '#ffffff'
      })
    },
    handleClose() {
      this.dialogVisible = false
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
