<template>
  <el-dialog :visible.sync="dialogVisible" title="查看图片" width="600px" @close="handleClose">
    <el-image :src="imageUrl" style="width: 100%" fit="contain">
      <div slot="error" class="image-slot">
        <i class="el-icon-picture-outline" />
      </div>
    </el-image>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ViewImage',
  props: {
    visible: { type: Boolean, default: false },
    imageUrl: { type: String, default: '' }
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
