<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="传感器值报文"
    width="700px"
    @close="handleClose"
  >
    <el-form label-width="100px">
      <el-form-item label="传感器值">
        <el-input v-model="sensorData.sensorValue" type="textarea" :rows="15" readonly />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ShowInstrumentLogSensor',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      sensorData: {
        sensorValue: ''
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    open(data) {
      this.sensorData = {
        sensorValue: data.sensorValue || ''
      }
      this.dialogVisible = true
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>
