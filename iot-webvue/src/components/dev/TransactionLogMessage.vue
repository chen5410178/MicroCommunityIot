<template>
  <el-dialog
    :visible.sync="visible"
    title="日志报文"
    width="700px"
    @close="handleClose"
  >
    <el-form label-width="100px">
      <el-form-item label="请求头">
        <el-input v-model="messageInfo.requestHeader" type="textarea" :rows="3" readonly />
      </el-form-item>
      <el-form-item label="请求报文">
        <el-input v-model="messageInfo.requestMessage" type="textarea" :rows="4" readonly />
      </el-form-item>
      <el-form-item label="返回头">
        <el-input v-model="messageInfo.responseHeader" type="textarea" :rows="3" readonly />
      </el-form-item>
      <el-form-item label="返回报文">
        <el-input v-model="messageInfo.responseMessage" type="textarea" :rows="4" readonly />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="visible = false">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { queryTransactionLogMessage } from '@/api/dev/devApi'

export default {
  name: 'TransactionLogMessage',
  data() {
    return {
      visible: false,
      messageInfo: {
        logId: '',
        requestHeader: '',
        requestMessage: '',
        responseHeader: '',
        responseMessage: ''
      }
    }
  },
  methods: {
    open(row) {
      this.visible = true
      this.messageInfo = {
        logId: '',
        requestHeader: '',
        requestMessage: '',
        responseHeader: '',
        responseMessage: ''
      }
      this.loadMessage(row.logId)
    },
    async loadMessage(logId) {
      try {
        const res = await queryTransactionLogMessage({ logId })
        const data = res.data || []
        if (data.length > 0) {
          this.messageInfo = { ...this.messageInfo, ...data[0] }
        }
      } catch (e) {
        this.$message.error(e.msg || '查询报文失败')
      }
    },
    handleClose() {
      this.visible = false
    }
  }
}
</script>
