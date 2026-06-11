<template>
  <el-dialog title="门禁日志报文" :visible.sync="visible" width="800px" :close-on-click-modal="false">
    <el-form label-width="80px">
      <el-form-item label="请求报文">
        <el-input type="textarea" :rows="10" v-model="reqParam" readonly />
      </el-form-item>
      <el-form-item label="返回报文">
        <el-input type="textarea" :rows="10" v-model="resParam" readonly />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listAccessControlLogParam } from '@/api/accessControl/accessControlApi'

export default {
  name: 'ShowAccessControlLog',
  data() {
    return {
      visible: false,
      reqParam: '',
      resParam: '',
      logId: ''
    }
  },
  methods: {
    async open(row) {
      this.logId = row.logId
      this.reqParam = ''
      this.resParam = ''
      this.visible = true
      try {
        const res = await listAccessControlLogParam({
          page: 1,
          row: 1,
          logId: this.logId,
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        if (res.data && res.data.length > 0) {
          this.reqParam = res.data[0].reqParam || ''
          this.resParam = res.data[0].resParam || ''
        }
      } catch (e) {
        console.error('获取日志报文失败', e)
      }
    }
  }
}
</script>
