<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="门锁日志"
    width="700px"
    @close="handleClose"
  >
    <el-form label-width="80px">
      <el-form-item label="请求报文">
        <el-input
          type="textarea"
          :rows="10"
          :value="logData.reqParam || ''"
          readonly
        />
      </el-form-item>
      <el-form-item label="返回报文">
        <el-input
          type="textarea"
          :rows="10"
          :value="logData.resParam || ''"
          readonly
        />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ShowLockLog',
  props: {
    visible: { type: Boolean, default: false },
    logData: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>
