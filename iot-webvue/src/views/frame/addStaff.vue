<template>
  <div class="add-staff-page">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加员工</span>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px;">
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
        <el-form-item label="关联组织" prop="orgId">
          <el-select v-model="form.orgId" placeholder="请选择组织" clearable style="width: 100%" @change="handleOrgChange">
            <el-option
              v-for="org in orgList"
              :key="org.orgId"
              :label="org.orgName"
              :value="org.orgId"
            />
          </el-select>
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
        <el-form-item>
          <el-button @click="handleCancel">返回</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { addStaff } from '@/api/frame/staffApi'
import { listOrgs } from '@/api/frame/orgApi'

export default {
  name: 'AddStaff',
  data() {
    return {
      loading: false,
      uploadUrl: '/iot/api/upload/uploadFile',
      photoList: [],
      orgList: [],
      form: {
        username: '',
        sex: '',
        tel: '',
        email: '',
        address: '',
        orgId: '',
        orgName: '',
        photo: ''
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
        ],
        orgId: [
          { required: true, message: '关联组织不能为空', trigger: 'change' }
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
  mounted() {
    this.loadOrgs()
  },
  methods: {
    async loadOrgs() {
      try {
        const res = await listOrgs({ page: 1, row: 100 })
        this.orgList = res.data || []
      } catch { /* ignore */ }
    },
    handleOrgChange(val) {
      if (val) {
        const org = this.orgList.find(o => o.orgId === val)
        this.form.orgName = org ? org.orgName : ''
      } else {
        this.form.orgName = ''
      }
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
    handleCancel() {
      this.$router.push('/pages/frame/staff')
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const data = {
          name: this.form.username,
          username: this.form.username,
          sex: this.form.sex,
          tel: this.form.tel,
          email: this.form.email,
          address: this.form.address,
          orgId: this.form.orgId,
          orgName: this.form.orgName,
          photo: this.form.photo,
          adminFlag: 'N'
        }
        await addStaff(data)
        this.$message.success('添加成功')
        this.$router.push('/pages/frame/staff')
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.add-staff-page { padding: 16px; }
.card-header { font-size: 16px; font-weight: 600; }
</style>
