<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="报文详情"
    width="700px"
    @close="handleClose"
  >
    <el-form label-width="100px">
      <el-form-item label="请求报文">
        <el-input v-model="logData.reqParam" type="textarea" :rows="10" readonly />
      </el-form-item>
      <el-form-item label="返回报文">
        <el-input v-model="logData.resParam" type="textarea" :rows="10" readonly />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ShowMeterMachineLog',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      logData: {
        reqParam: '',
        resParam: ''
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    open(data) {
      this.logData = {
        reqParam: data.reqParam || '',
        resParam: data.resParam || ''
      }
      this.dialogVisible = true
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>
