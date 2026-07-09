<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改事件模板"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="form.templateName" placeholder="必填，请填写模板名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="通知方式" prop="eventWay">
        <el-select v-model="form.eventWay" placeholder="必填，请选择通知方式" style="width: 100%" disabled>
          <el-option v-for="item in eventWayList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item v-for="(item, index) in form.eventTemplateParamKetList" :key="index" :label="item.specName" :prop="'eventTemplateParamKetList.' + index + '.paramValue'" :rules="[{ required: true, message: item.specName + '不能为空', trigger: 'blur' }]">
        <el-input v-model="item.paramValue" type="textarea" :rows="3" :placeholder="item.remark || '请输入' + item.specName" />
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
import { updateEventTemplate, listEventTemplateParamKeys } from '@/api/event/eventApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditEventTemplate',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      eventWayList: [],
      form: {
        templateId: '',
        templateName: '',
        eventWay: '',
        remark: '',
        eventTemplateParamKetList: []
      },
      rules: {
        templateName: [
          { required: true, message: '模板名称不能为空', trigger: 'blur' },
          { max: 64, message: '模板名称不能超过64', trigger: 'blur' }
        ],
        eventWay: [
          { required: true, message: '请选择通知方式', trigger: 'change' }
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
        this.form.templateId = this.editData.templateId || ''
        this.form.templateName = this.editData.templateName || ''
        this.form.eventWay = this.editData.eventWay || ''
        this.form.remark = this.editData.remark || ''
        this.loadEventWayDict()
        this.loadParamKeys()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadEventWayDict() {
      try {
        const res = await listDicts({ tableName: 'event_template', tableColumns: 'event_way' })
        this.eventWayList = res || []
      } catch { /* ignore */ }
    },
    async loadParamKeys() {
      const eventWay = this.form.eventWay
      if (!eventWay) return
      try {
        const res = await listEventTemplateParamKeys({ eventWay, page: -1, row: 100 })
        const paramKeyList = res.data || []
        const existingDtoList = this.editData.eventTemplateParamDtoList || []
        for (let i = 0; i < paramKeyList.length; i++) {
          for (let j = 0; j < existingDtoList.length; j++) {
            if (paramKeyList[i].specCd === existingDtoList[j].specCd) {
              existingDtoList[j].specName = paramKeyList[i].specName
              break
            }
          }
        }
        this.form.eventTemplateParamKetList = existingDtoList.map(item => ({
          specCd: item.specCd,
          specName: item.specName || '',
          paramValue: item.paramValue || '',
          remark: item.remark || ''
        }))
      } catch { /* ignore */ }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.form.eventTemplateParamKetList = []
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
        const hasEmptyParam = this.form.eventTemplateParamKetList.some(item => !item.paramValue)
        if (hasEmptyParam) {
          this.$message.warning('请填写所有参数值')
          return
        }
      } catch { return }
      this.loading = true
      try {
        const data = {
          ...this.form,
          communityId: this.editData.communityId || (getCurrentCommunity() || {}).communityId
        }
        await updateEventTemplate(data)
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
