<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改语音"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="类型" prop="ttsType">
        <el-select v-model="form.ttsType" placeholder="必填，请选择类型" style="width: 100%">
          <el-option label="欠费催缴" value="OWE_FEE" />
        </el-select>
      </el-form-item>
      <el-form-item label="语音名称" prop="ttsName">
        <el-input v-model="form.ttsName" placeholder="必填，请填写语音名称" maxlength="128" />
      </el-form-item>
      <el-form-item label="语音地址" prop="ttsPath">
        <el-input v-model="form.ttsPath" placeholder="必填，请填写或上传语音地址" maxlength="200" />
      </el-form-item>
      <el-form-item label=" ">
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          accept="audio/*"
          action=""
        >
          <el-button size="small" type="primary">上传文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写备注" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateTelTts } from '@/api/lamp/lampApi'
import request from '@/utils/request'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditTelTts',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        ttId: '',
        ttsType: '',
        ttsName: '',
        ttsPath: '',
        remark: ''
      },
      rules: {
        ttsType: [
          { required: true, message: '类型不能为空', trigger: 'change' }
        ],
        ttsName: [
          { required: true, message: '语音名称不能为空', trigger: 'blur' },
          { max: 128, message: '语音名称不能超过128', trigger: 'blur' }
        ],
        ttsPath: [
          { required: true, message: '语音地址不能为空', trigger: 'blur' },
          { max: 200, message: '语音地址不能超过200', trigger: 'blur' }
        ],
        remark: [
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleUpload(param) {
      const formData = new FormData()
      formData.append('uploadFile', param.file)
      formData.append('communityId', (getCurrentCommunity() || {}).communityId || '')
      try {
        const res = await request({
          url: '/upload/uploadFile',
          method: 'post',
          data: formData,
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.code === 0) {
          this.form.ttsPath = res.data && (res.data.url || res.data)
          this.$message.success('上传成功')
        } else {
          this.$message.error(res.msg || '上传失败')
        }
      } catch {
        this.$message.error('上传失败')
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateTelTts({ ...this.form, communityId })
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
