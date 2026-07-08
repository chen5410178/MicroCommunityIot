<template>
  <el-dialog
    :visible.sync="visible"
    title="工牌日志"
    width="700px"
    @close="handleClose"
  >
    <el-form label-width="90px">
      <el-form-item label="请求报文">
        <el-input v-model="reqParam" type="textarea" :rows="10" readonly />
      </el-form-item>
      <el-form-item label="返回报文">
        <el-input v-model="resParam" type="textarea" :rows="10" readonly />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listWorkLicenseLogs } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ShowWorkLicenseLog',
  data() {
    return {
      visible: false,
      reqParam: '',
      resParam: ''
    }
  },
  methods: {
    open(logData) {
      this.visible = true
      this.reqParam = ''
      this.resParam = ''
      if (logData && logData.logId) {
        this.fetchDetail(logData.logId)
      }
    },
    async fetchDetail(logId) {
      try {
        const res = await listWorkLicenseLogs({
          logId,
          communityId: (getCurrentCommunity() || {}).communityId,
          page: 1,
          row: 1
        })
        if (res.data && res.data.length > 0) {
          this.reqParam = res.data[0].reqParam || ''
          this.resParam = res.data[0].resParam || ''
        }
      } catch (e) {
        // ignore
      }
    },
    handleClose() {
      this.visible = false
      this.reqParam = ''
      this.resParam = ''
    }
  }
}
</script>
