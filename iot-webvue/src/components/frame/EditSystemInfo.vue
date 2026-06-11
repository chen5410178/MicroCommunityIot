<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改系统配置"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="标题名称" prop="systemTitle">
        <el-input v-model="form.systemTitle" placeholder="必填，请填写标题名称" />
      </el-form-item>
      <el-form-item label="副标题" prop="subSystemTitle">
        <el-input v-model="form.subSystemTitle" placeholder="必填，请填写副标题" />
      </el-form-item>
      <el-form-item label="简写标题" prop="systemSimpleTitle">
        <el-input v-model="form.systemSimpleTitle" placeholder="必填，请填写简写标题" />
      </el-form-item>
      <el-form-item label="公司名称" prop="companyName">
        <el-input v-model="form.companyName" placeholder="必填，请填写公司名称" />
      </el-form-item>
      <el-form-item label="logo地址" prop="logoUrl">
        <el-input v-model="form.logoUrl" placeholder="必填，请填写logo地址" />
      </el-form-item>
      <el-form-item label="静态url" prop="imgUrl">
        <el-input v-model="form.imgUrl" placeholder="必填，请填写静态url" />
      </el-form-item>
      <el-form-item label="默认小区编号" prop="defaultCommunityId">
        <el-input v-model="form.defaultCommunityId" placeholder="必填，请填写默认小区编号" />
      </el-form-item>
      <el-form-item label="业主标题" prop="ownerTitle">
        <el-input v-model="form.ownerTitle" placeholder="必填，请填写业主标题" />
      </el-form-item>
      <el-form-item label="物业手机标题" prop="propertyTitle">
        <el-input v-model="form.propertyTitle" placeholder="必填，请填写物业手机标题" />
      </el-form-item>
      <el-form-item label="qq地图key" prop="qqMapKey">
        <el-input v-model="form.qqMapKey" placeholder="必填，请填写qq地图key" />
      </el-form-item>
      <el-form-item label="商城地址" prop="mallUrl">
        <el-input v-model="form.mallUrl" placeholder="必填，请填写商城地址" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateSystemInfo } from '@/api/login/systemApi'

export default {
  name: 'EditSystemInfo',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        systemId: '',
        systemTitle: '',
        subSystemTitle: '',
        systemSimpleTitle: '',
        companyName: '',
        logoUrl: '',
        imgUrl: '',
        defaultCommunityId: '',
        ownerTitle: '',
        propertyTitle: '',
        qqMapKey: '',
        mallUrl: ''
      },
      rules: {
        systemTitle: [
          { required: true, message: '标题名称不能为空', trigger: 'blur' },
          { max: 64, message: '标题名称不能超过64', trigger: 'blur' }
        ],
        subSystemTitle: [
          { required: true, message: '副标题不能为空', trigger: 'blur' },
          { max: 64, message: '副标题不能超过64', trigger: 'blur' }
        ],
        companyName: [
          { required: true, message: '公司名称不能为空', trigger: 'blur' },
          { max: 128, message: '公司名称不能超过128', trigger: 'blur' }
        ],
        logoUrl: [
          { required: true, message: 'logo地址不能为空', trigger: 'blur' },
          { max: 200, message: 'logo地址不能超过200', trigger: 'blur' }
        ],
        imgUrl: [
          { required: true, message: '静态url不能为空', trigger: 'blur' },
          { max: 512, message: '静态url不能超过512', trigger: 'blur' }
        ],
        defaultCommunityId: [
          { required: true, message: '默认小区编号不能为空', trigger: 'blur' }
        ],
        ownerTitle: [
          { required: true, message: '业主标题不能为空', trigger: 'blur' },
          { max: 64, message: '业主标题不能超过64', trigger: 'blur' }
        ],
        propertyTitle: [
          { required: true, message: '物业手机标题不能为空', trigger: 'blur' },
          { max: 64, message: '物业手机标题不能超过64', trigger: 'blur' }
        ],
        qqMapKey: [
          { required: true, message: 'qq地图key不能为空', trigger: 'blur' },
          { max: 64, message: 'qq地图key不能超过64', trigger: 'blur' }
        ],
        mallUrl: [
          { required: true, message: '商城地址不能为空', trigger: 'blur' },
          { max: 128, message: '商城地址不能超过128', trigger: 'blur' }
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
        await updateSystemInfo(this.form)
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
