import { getCurrentCommunity } from '@/utils/auth'
<template>
  <el-dialog :visible.sync="dialogVisible" title="房产导入" width="500px" @close="handleClose">
    <el-upload
      ref="uploadRef"
      drag
      action="/iot/api/room.importOwnerRoom"
      :headers="uploadHeaders"
      :data="uploadData"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      accept=".xls,.xlsx"
    >
      <i class="el-icon-upload" />
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传 xls/xlsx 文件</div>
    </el-upload>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ImportOwnerRoom',
  props: { visible: { type: Boolean, default: false } },
  data() {
    return { dialogVisible: false }
  },
  computed: {
    uploadHeaders() {
      return {
        'APP-ID': '992023010100000001',
        'USER-ID': (JSON.parse(sessionStorage.getItem('iot_webvue_user') || '{}')).userId || '-1'
      }
    },
    uploadData() {
      const communityInfo = getCurrentCommunity() || {}
      return { communityId: communityInfo.communityId }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() { this.dialogVisible = false },
    handleUploadSuccess() {
      this.$message.success('导入成功')
      this.$emit('success')
      this.handleClose()
    },
    handleUploadError(err) {
      this.$message.error('导入失败')
    }
  }
}
</script>
