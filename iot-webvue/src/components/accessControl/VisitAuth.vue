<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="审核访客"
    width="700px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="姓名">
            <el-input v-model="form.name" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别">
            <el-select v-model="form.visitGender" disabled style="width: 100%">
              <el-option label="男" :value="0" />
              <el-option label="女" :value="1" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="电话号码">
            <el-input v-model="form.phoneNumber" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="访客类型">
            <el-select v-model="form.typeId" disabled style="width: 100%">
              <el-option v-for="item in visitTypeList" :key="item.typeId" :label="item.name" :value="item.typeId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="拜访房屋">
            <el-input v-model="form.roomName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="拜访业主">
            <el-input v-model="form.ownerName" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="拜访时间">
            <el-input v-model="form.visitTime" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="离开时间">
            <el-input v-model="form.departureTime" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="拜访事由">
        <el-input v-model="form.visitCase" type="textarea" rows="3" disabled />
      </el-form-item>
      <el-form-item label="审核状态" prop="state">
        <el-select v-model="form.state" placeholder="请选择审核状态" style="width: 100%">
          <el-option v-for="item in stateList" :key="item.statusCd" :label="item.statusName" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核备注" prop="msg">
        <el-input v-model="form.msg" type="textarea" rows="3" placeholder="选填，请填写审核备注" maxlength="255" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { propertyAuthVisit } from '@/api/accessControl/accessControlApi'
import { listVisitTypes } from '@/api/accessControl/accessControlApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'VisitAuth',
  props: {
    visible: { type: Boolean, default: false },
    authData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      visitTypeList: [],
      stateList: [],
      form: {
        visitId: '',
        name: '',
        visitGender: '',
        phoneNumber: '',
        typeId: '',
        roomName: '',
        ownerName: '',
        visitTime: '',
        departureTime: '',
        visitCase: '',
        state: '',
        msg: ''
      },
      rules: {
        state: [{ required: true, message: '请选择审核状态', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.authData) {
        this.form = { ...this.authData, state: this.authData.state || '', msg: this.authData.msg || '' }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  mounted() {
    this.loadDicts()
  },
  methods: {
    async loadDicts() {
      try {
        const [dictRes, typeRes] = await Promise.all([
          listDicts({ tableName: 'visit', tableColumns: 'state' }),
        ])
        this.stateList = dictRes || []
        this.visitTypeList = typeRes.data || []
      } catch (e) {
        console.error('加载字典失败', e)
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
        await propertyAuthVisit({
          ...this.form,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        this.$message.success('审核成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '审核失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
