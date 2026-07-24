<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="无牌车进场二维码"
    width="350px"
    @close="handleClose"
  >
    <div class="qr-code-content">
      <div ref="qrCodeDom" style="width:200px; height:200px; margin: 0 auto"></div>
      <p class="qr-tip">请无牌车车主扫码登记后入场</p>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import QRCode from 'qrcodejs2'
import { getUnlicensedCarMachineQrCodeUrl } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'UnlicensedCarQrCode',
  data() {
    return {
      dialogVisible: false,
      qrCode: null
    }
  },
  methods: {
    open(data) {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.loadQrCode(data)
      })
    },
    handleClose() {
      this.dialogVisible = false
    },
    async loadQrCode(data) {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await getUnlicensedCarMachineQrCodeUrl({
          communityId,
          machineId: data.machineId || ''
        })
        if (res.code === 0 && res.data && res.data.url) {
          this.renderQrCode(res.data.url)
        } else {
          this.$message.error(res.msg || '获取二维码失败')
        }
      } catch (e) {
        this.$message.error('获取二维码失败')
      }
    },
    renderQrCode(url) {
      const dom = this.$refs.qrCodeDom
      if (!dom) return
      dom.innerHTML = ''
      if (this.qrCode) {
        this.qrCode.clear()
      }
      this.qrCode = new QRCode(dom, {
        text: url,
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

<style scoped>
.qr-code-content {
  text-align: center;
}
.qr-tip {
  margin-top: 16px;
  font-size: 13px;
  color: #909399;
}
</style>
