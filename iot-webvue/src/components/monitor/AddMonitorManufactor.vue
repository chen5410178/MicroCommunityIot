<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加监控平台"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="厂家名称" prop="mmName">
        <el-input v-model="form.mmName" placeholder="必填，请填写厂家名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="系统地址" prop="mmUrl">
        <el-input v-model="form.mmUrl" placeholder="必填，请填写厂家系统地址" maxlength="512" />
      </el-form-item>
      <el-form-item label="厂家类型" prop="mmBean">
        <el-select v-model="form.mmBean" placeholder="必填，请选择厂家类型" style="width: 100%">
          <el-option
            v-for="item in mmBeanOptions"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="APP_ID" prop="appId">
        <el-input v-model="form.appId" placeholder="必填，请填写APP_ID" maxlength="64" />
      </el-form-item>
      <el-form-item label="秘钥" prop="appSecure">
        <el-input v-model="form.appSecure" placeholder="必填，请填写秘钥" maxlength="64" />
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
import { saveMonitorManufactor } from '@/api/monitor/monitorApi'
import request from '@/utils/request'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMonitorManufactor',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      mmBeanOptions: [],
      form: {
        mmName: '',
        mmUrl: '',
        mmBean: '',
        appId: '',
        appSecure: '',
        remark: ''
      },
      rules: {
        mmName: [
          { required: true, message: '厂家名称不能为空', trigger: 'blur' },
          { max: 64, message: '厂家名称不能超过64', trigger: 'blur' }
        ],
        mmUrl: [
          { required: true, message: '系统地址不能为空', trigger: 'blur' },
          { max: 512, message: '系统地址不能超过512', trigger: 'blur' }
        ],
        mmBean: [
          { required: true, message: '厂家类型不能为空', trigger: 'change' }
        ],
        appId: [
          { required: true, message: 'APP_ID不能为空', trigger: 'blur' },
          { max: 64, message: 'APP_ID不能超过64', trigger: 'blur' }
        ],
        appSecure: [
          { required: true, message: '秘钥不能为空', trigger: 'blur' },
          { max: 64, message: '秘钥不能超过64', trigger: 'blur' }
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
      if (val) {
        this.fetchMmBeans()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchMmBeans() {
      if (this.mmBeanOptions.length) return
      try {
        const res = await request({ url: '/dict.listDict', method: 'get', params: { tableName: 'monitor_manufactor', tableKey: 'mm_bean' } })
        this.mmBeanOptions = res.data || []
      } catch { /* ignore */ }
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
        await saveMonitorManufactor({ ...this.form, communityId })
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
