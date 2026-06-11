<template>
  <el-dialog :visible.sync="dialogVisible" title="路灯日志报文" width="700px">
    <el-form label-width="100px">
      <el-form-item label="请求报文">
        <el-input v-model="logData.reqParam" type="textarea" :rows="10" readonly />
      </el-form-item>
      <el-form-item label="返回报文">
        <el-input v-model="logData.resParam" type="textarea" :rows="10" readonly />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="dialogVisible = false">返回</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ShowLampMachineLog',
  props: {
    visible: { type: Boolean, default: false },
    logData: { type: Object, default: () => ({ reqParam: '', resParam: '' }) }
  },
  data() {
    return {
      dialogVisible: false
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  }
}
</script>
