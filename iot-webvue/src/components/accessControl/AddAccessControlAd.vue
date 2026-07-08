<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加广告"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="广告名称" prop="adName">
        <el-input v-model="form.adName" placeholder="必填，请填写广告名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="form.state" placeholder="请选择状态" style="width: 100%">
          <el-option label="在用" value="Y" />
          <el-option label="停用" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="广告类型" prop="adType">
        <el-select v-model="form.adType" placeholder="请选择广告类型" style="width: 100%">
          <el-option label="图片" value="IMG" />
          <el-option label="视频" value="VIDEO" />
        </el-select>
      </el-form-item>
      <el-form-item label="广告地址" prop="url">
        <el-input v-model="form.url" placeholder="必填，请填写或上传广告地址" maxlength="512" />
      </el-form-item>
      <el-form-item label=" ">
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          accept="image/*,video/*"
          action=""
        >
          <el-button size="small" type="primary">上传文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveAccessControlAd } from '@/api/accessControl/accessControlApi'
import request from '@/utils/request'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddAccessControlAd',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        adName: '',
        state: '',
        adType: '',
        url: '',
        remark: ''
      },
      rules: {
        adName: [
          { required: true, message: '广告名称不能为空', trigger: 'blur' },
          { max: 64, message: '广告名称不能超过64', trigger: 'blur' }
        ],
        state: [{ required: true, message: '请选择状态', trigger: 'change' }],
        adType: [{ required: true, message: '请选择广告类型', trigger: 'change' }],
        url: [
          { required: true, message: '广告地址不能为空', trigger: 'blur' },
          { max: 512, message: '广告地址不能超过512', trigger: 'blur' }
        ],
        remark: [{ max: 512, message: '备注不能超过512', trigger: 'blur' }]
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
      formData.append('communityId', (getCurrentCommunity() || {}).communityId)
      try {
        const res = await request({
          url: '/upload/uploadFile',
          method: 'post',
          data: formData,
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.code === 0) {
          this.form.url = res.data && res.data.url
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
        const data = {
          ...this.form,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        await saveAccessControlAd(data)
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
