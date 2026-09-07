<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="月卡购买二维码"
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
        <p class="qr-tip">微信扫码打开小程序购买停车月卡</p>
      </div>
      <div class="qr-col">
        <div ref="publicQrcodeRef" class="qr-box">
          <span v-if="!qrData.url" class="qr-empty">暂无二维码</span>
        </div>
        <p class="qr-name">公众号二维码</p>
        <p class="qr-tip">微信扫码打开公众号购买停车月卡</p>
      </div>
    </div>
    <p class="qr-footer-tip">请截图贴在各岗亭门口，车主微信扫码即可自助购买停车月卡</p>
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
      qrData: {
        miniUrl: '',
        url: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => {
          this.loadQrCode()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  beforeDestroy() {
    this.clearQrCode()
  },
  methods: {
    async loadQrCode() {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await getBuyCarMonthCardQrCodeUrl({ communityId })
        if (res.code === 0 && res.data) {
          this.qrData = {
            miniUrl: res.data.miniUrl || '',
            url: res.data.url || ''
          }
          this.$nextTick(() => {
            this.generateQRCode()
          })
        } else {
          this.$message.error(res.msg || '获取二维码失败')
        }
      } catch (e) {
        this.$message.error('获取二维码失败')
      }
    },
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
    clearQrCode() {
      if (this.$refs.miniQrcodeRef) this.$refs.miniQrcodeRef.innerHTML = ''
      if (this.$refs.publicQrcodeRef) this.$refs.publicQrcodeRef.innerHTML = ''
    },
    handleClose() {
      this.dialogVisible = false
      this.clearQrCode()
    }
  }
}
</script>

<style scoped>
.dual-code-content {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding: 20px 0 0;
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
.qr-footer-tip {
  margin-top: 16px;
  text-align: center;
  font-size: 13px;
  color: #909399;
}
</style>
