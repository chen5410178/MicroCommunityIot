<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="房产导入"
    width="550px"
    @close="handleClose"
  >
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="选择文件" prop="excelTemplate" :rules="[{ required: true, message: '文件不能为空', trigger: 'change' }]">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          accept=".xls,.xlsx"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
          action=""
        >
          <el-button size="small" type="primary">选择文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="下载模板">
        <span>请先下载 <a href="/import/importRoom.xlsx" target="_blank">导入模板</a> 准备数据后，上传导入</span>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">导入</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { importOwnerRoom } from '@/api/property/roomApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ImportOwnerRoom',
  props: { visible: { type: Boolean, default: false } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      file: null,
      fileList: [],
      form: {}
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleFileChange(file) {
      if (!file || !file.raw) return
      const acceptTypes = ['xlsx', 'xls']
      const ext = file.name.split('.').pop()
      if (!acceptTypes.includes(ext)) {
        this.$message.error('不是有效的Excel格式')
        this.fileList = []
        return
      }
      if (file.size > 2 * 1024 * 1024) {
        this.$message.error('Excel文件大小不能超过2M')
        this.fileList = []
        return
      }
      this.file = file.raw
    },
    handleFileRemove() {
      this.file = null
    },
    handleClose() {
      this.dialogVisible = false
      this.file = null
      this.fileList = []
    },
    async handleSubmit() {
      if (!this.file) {
        this.$message.warning('请选择文件')
        return
      }
      this.loading = true
      try {
        const formData = new FormData()
        formData.append('uploadFile', this.file)
        formData.append('communityId', (getCurrentCommunity() || {}).communityId || '')
        formData.append('importAdapt', 'importRoomOwner')
        const res = await importOwnerRoom(formData)
        this.$message.success('导入任务提交成功')
        this.$emit('success')
        this.handleClose()
        this.$router.push('/pages/log/assetImportLogDetail?logId=' + res.data.logId + '&logType=importRoomOwner')
      } catch (e) {
        if (typeof e === 'string') {
          this.$message.error(e)
        } else {
          this.$message.error((e && e.message) || '导入失败')
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
