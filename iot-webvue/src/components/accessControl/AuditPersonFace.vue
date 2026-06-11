<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="人脸审核"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="房屋名称">
            <el-input v-model="form.roomName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="人员名称">
            <el-input v-model="form.personName" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="人员电话">
            <el-input v-model="form.personTel" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="人员类型">
            <el-input :value="form.ownerTypeCd == '1001' ? '业主本人' : '家庭成员'" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="人脸">
        <el-image v-if="form.faceUrl" :src="form.faceUrl" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" />
        <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
      </el-form-item>
      <el-form-item label="审核" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择审核状态" style="width: 100%">
          <el-option label="同意" value="C" />
          <el-option label="拒绝" value="F" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核说明" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="必填，请填写审核说明" maxlength="256" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { authPersonFace } from '@/api/accessControl/personFaceApi'

export default {
  name: 'AuditPersonFace',
  props: {
    visible: { type: Boolean, default: false },
    auditData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        pfId: '',
        roomName: '',
        personName: '',
        personTel: '',
        ownerTypeCd: '',
        faceUrl: '',
        state: '',
        remark: ''
      },
      rules: {
        state: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
        remark: [{ required: true, message: '审核说明不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.auditData) {
        this.form = {
          pfId: this.auditData.pfId || '',
          roomName: this.auditData.roomName || '',
          personName: this.auditData.personName || '',
          personTel: this.auditData.personTel || '',
          ownerTypeCd: this.auditData.ownerTypeCd || '',
          faceUrl: this.auditData.faceUrl || '',
          state: '',
          remark: ''
        }
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
        await authPersonFace({
          pfId: this.form.pfId,
          state: this.form.state,
          remark: this.form.remark,
          communityId: this.$store.state.app.currentCommunity.communityId
        })
        this.$message.success('审核成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '审核失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
