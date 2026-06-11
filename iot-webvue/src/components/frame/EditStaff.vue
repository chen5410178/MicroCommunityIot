<template>
  <el-dialog :visible.sync="dialogVisible" title="修改员工" width="600px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="员工名称" prop="username">
        <el-input v-model="form.username" placeholder="必填，2-10字符" maxlength="10" />
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio :label="0">男</el-radio>
          <el-radio :label="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="手机号" prop="tel">
        <el-input v-model="form.tel" placeholder="必填" maxlength="11" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="选填" maxlength="64" />
      </el-form-item>
      <el-form-item label="家庭住址" prop="address">
        <el-input v-model="form.address" placeholder="必填，不超过200位" maxlength="200" />
      </el-form-item>
      <el-form-item label="员工照片">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :file-list="photoList"
          list-type="picture-card"
          :limit="1"
          :on-success="handleUploadSuccess"
          :on-remove="handleUploadRemove"
          :before-upload="beforeUpload"
        >
          <i class="el-icon-plus" />
        </el-upload>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateStaff } from '@/api/frame/staffApi'

export default {
  name: 'EditStaff',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      uploadUrl: '/iot/api/upload/uploadFile',
      photoList: [],
      form: {
        userId: '',
        username: '',
        sex: '',
        tel: '',
        email: '',
        address: '',
        photo: '',
        orgId: '',
        orgName: ''
      },
      rules: {
        username: [
          { required: true, message: '员工名称不能为空', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在2-10个字符', trigger: 'blur' }
        ],
        sex: [
          { required: true, message: '性别不能为空', trigger: 'change' }
        ],
        tel: [
          { required: true, message: '手机号不能为空', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '家庭住址不能为空', trigger: 'blur' },
          { max: 200, message: '不超过200位', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    uploadHeaders() {
      const userInfo = sessionStorage.getItem('iot_webvue_user')
      const userId = userInfo ? (JSON.parse(userInfo).userId || '-1') : '-1'
      const communityStr = sessionStorage.getItem('iot_webvue_current_community')
      const communityId = communityStr ? (JSON.parse(communityStr).communityId || '') : ''
      return {
        'APP-ID': '992023010100000001',
        'USER-ID': userId,
        'JAVA110-LANG': sessionStorage.getItem('iot_webvue_lang') || 'zh-cn',
        'COMMUNITY-ID': communityId
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          userId: this.editData.userId || '',
          username: this.editData.name || this.editData.username || '',
          sex: this.editData.sex !== undefined ? this.editData.sex : '',
          tel: this.editData.tel || '',
          email: this.editData.email || '',
          address: this.editData.address || '',
          photo: this.editData.faceUrl || this.editData.photo || '',
          orgId: this.editData.orgId || '',
          orgName: this.editData.orgName || ''
        }
        if (this.editData.faceUrl) {
          this.photoList = [{ url: this.editData.faceUrl, name: 'photo' }]
        } else {
          this.photoList = []
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.photoList = []
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    handleUploadSuccess(res) {
      if (res.code === 0) {
        this.form.photo = res.data.fileId
      }
    },
    handleUploadRemove() {
      this.form.photo = ''
    },
    beforeUpload(file) {
      const isImage = /^image\//.test(file.type)
      if (!isImage) {
        this.$message.error('只能上传图片文件')
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        this.$message.error('图片大小不能超过2MB')
        return false
      }
      return true
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const data = {
          userId: this.form.userId,
          name: this.form.username,
          username: this.form.username,
          sex: this.form.sex,
          tel: this.form.tel,
          email: this.form.email,
          address: this.form.address,
          photo: this.form.photo,
          orgId: this.form.orgId,
          orgName: this.form.orgName,
          staffId: this.form.userId
        }
        await updateStaff(data)
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
