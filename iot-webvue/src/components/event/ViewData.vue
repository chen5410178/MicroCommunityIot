<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="title"
    width="600px"
    @close="handleClose"
  >
    <el-input v-model="content" type="textarea" :rows="5" disabled />
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ViewData',
  props: {
    visible: { type: Boolean, default: false },
    title: { type: String, default: '' },
    content: { type: String, default: '' }
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
