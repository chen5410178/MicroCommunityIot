<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="充电桩二维码"
    width="400px"
    @close="handleClose"
  >
    <div style="text-align: center; padding: 20px 0;">
      <div ref="qrcodeRef" style="display: inline-block" />
      <p style="margin-top: 16px; font-size: 14px; color: #303133;">{{ qrData.machineName || '' }}</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'

export default {
  name: 'ChargeMachineQrCode',
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
      const el = this.$refs.qrcodeRef
      if (!el) return
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
