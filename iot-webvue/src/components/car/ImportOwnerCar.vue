<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="车辆导入"
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
        <span>请先下载 <a href="/import/importOwnerCar.xlsx" target="_blank">导入模板</a> 准备数据后，上传导入</span>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">导入</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { assetImport } from '@/api/car/carApi'

export default {
  name: 'ImportOwnerCar',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      file: null,
      fileList: []
    }
  },
  methods: {
    open() {
      this.dialogVisible = true
      this.file = null
      this.fileList = []
    },
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
        formData.append('communityId', this.$store.state.app.currentCommunity.communityId || '')
        formData.append('importAdapt', 'importOwnerCar')
        const res = await assetImport(formData)
        if (res.code === 0) {
          this.$message.success('导入成功')
          this.handleClose()
          this.$router.push('/pages/log/assetImportLogDetail?logId=' + res.data.logId + '&logType=importOwnerCar')
        } else {
          this.$message.error(res.msg || '导入失败')
        }
      } catch {
        this.$message.error('导入失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
