<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加布控人员"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="人员名称" prop="personName">
        <el-input v-model="form.personName" placeholder="必填，请填写人员名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="人脸图片" prop="faceUrl">
        <el-input v-model="form.faceUrl" placeholder="必填，请上传人脸图片" maxlength="256" />
      </el-form-item>
      <el-form-item label=" ">
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          accept="image/*"
          action=""
        >
          <el-button size="small" type="primary">上传图片</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="必填，请选择开始时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="必填，请选择结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveDispatchFace } from '@/api/ai/dispatchFaceApi'
import request from '@/utils/request'

export default {
  name: 'AddDispatchFace',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        personName: '',
        faceUrl: '',
        startTime: '',
        endTime: ''
      },
      rules: {
        personName: [
          { required: true, message: '人员名称不能为空', trigger: 'blur' },
          { max: 64, message: '人员名称不能超过64', trigger: 'blur' }
        ],
        faceUrl: [
          { required: true, message: '请上传人脸图片', trigger: 'blur' },
          { max: 256, message: '图片地址不能超过256', trigger: 'blur' }
        ],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleUpload(param) {
      const formData = new FormData()
      formData.append('uploadFile', param.file)
      formData.append('communityId', this.$store.state.app.currentCommunity.communityId)
      try {
        const res = await request({
          url: '/upload/uploadFile',
          method: 'post',
          data: formData,
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.code === 0) {
          this.form.faceUrl = res.data && res.data.url
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveDispatchFace({
          ...this.form,
          personType: 'dispatch',
          communityId
        })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
