<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改支付配置"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="名称" prop="paymentName">
        <el-input v-model="form.paymentName" placeholder="必填，请填写名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="支付厂家" prop="paymentType">
        <el-select v-model="form.paymentType" placeholder="必填，请选择支付厂家" style="width: 100%" disabled>
          <el-option
            v-for="item in paymentTypes"
            :key="item.paymentType"
            :label="item.name"
            :value="item.paymentType"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        v-for="(item, index) in form.paymentKeys"
        :key="index"
        :label="item.name"
      >
        <el-input
          v-model="item.columnValue"
          type="textarea"
          :placeholder="item.remark || ''"
        />
      </el-form-item>
      <el-form-item v-if="form.paymentType === 'WECHAT'" label="商户证书(.p12)">
        <el-upload
          action="#"
          :http-request="handleUpload"
          :file-list="certFileList"
          :limit="1"
          :on-remove="handleCertRemove"
          :before-upload="beforeCertUpload"
          accept=".p12"
        >
          <el-button size="small" type="primary">点击上传</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="支付范围" prop="payType">
        <el-select v-model="form.payType" placeholder="必填，请选择支付范围" style="width: 100%" disabled>
          <el-option value="1001" label="小区费用" />
          <el-option value="2002" label="临时停车费" />
          <el-option value="3003" label="费用项" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.payType === '3003'" label="费用项">
        <el-checkbox-group v-model="form.configIds">
          <el-checkbox
            v-for="item in feeConfigs"
            :key="item.configId"
            :label="item.configId"
          >{{ item.feeName }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择状态" style="width: 100%">
          <el-option value="Y" label="启用" />
          <el-option value="N" label="停用" />
        </el-select>
      </el-form-item>
      <el-form-item label="使用说明">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写使用说明" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updatePaymentPool, listPaymentAdapt, listPaymentKey, listFeeConfig, listPaymentPool } from '@/api/property/paymentPoolApi'
import request from '@/utils/request'

export default {
  name: 'EditPaymentPool',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      paymentTypes: [],
      feeConfigs: [],
      certFileList: [],
      form: {
        ppId: '',
        paymentName: '',
        paymentType: '',
        paymentKeys: [],
        certPath: '',
        payType: '',
        state: '',
        remark: '',
        configIds: []
      },
      rules: {
        paymentName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 64, message: '名称不超过64字符', trigger: 'blur' }
        ],
        paymentType: [
          { required: true, message: '请选择支付厂家', trigger: 'change' }
        ],
        payType: [
          { required: true, message: '请选择支付范围', trigger: 'change' }
        ],
        state: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.initEdit()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async initEdit() {
      try {
        const adaptRes = await listPaymentAdapt({ page: 1, row: 100 })
        this.paymentTypes = adaptRes.data || []

        const keyRes = await listPaymentKey({ paymentType: this.editData.paymentType, page: 1, row: 100 })
        const keys = (keyRes.data || []).map(item => ({ ...item, columnValue: '' }))

        const poolRes = await listPaymentPool({
          ppId: this.editData.ppId,
          communityId: this.$store.state.app.currentCommunity.communityId,
          page: 1,
          row: 1
        })
        const poolData = (poolRes.data || [])[0] || {}

        if (poolData.values) {
          poolData.values.forEach(value => {
            keys.forEach(key => {
              if (value.columnKey === key.columnKey) {
                key.columnValue = value.columnValue
              }
            })
          })
        }

        const configIds = []
        if (poolData.configs) {
          poolData.configs.forEach(config => {
            configIds.push(config.configId)
          })
        }

        if (this.editData.payType === '3003') {
          try {
            const feeRes = await listFeeConfig({ page: 1, row: 100, communityId: this.$store.state.app.currentCommunity.communityId })
            this.feeConfigs = feeRes.data || []
          } catch { /* ignore */ }
        }

        this.form = {
          ppId: this.editData.ppId || '',
          paymentName: this.editData.paymentName || '',
          paymentType: this.editData.paymentType || '',
          paymentKeys: keys,
          certPath: this.editData.certPath || '',
          payType: this.editData.payType || '',
          state: this.editData.state || '',
          remark: this.editData.remark || '',
          configIds: configIds
        }

        if (this.editData.certPath) {
          this.certFileList = [{ name: this.editData.certPath, url: '' }]
        }
      } catch { /* ignore */ }
    },
    beforeCertUpload(file) {
      const isP12 = file.name.endsWith('.p12')
      if (!isP12) {
        this.$message.error('只能上传 .p12 格式的证书文件')
        return false
      }
      return true
    },
    handleUpload(param) {
      const formData = new FormData()
      formData.append('uploadFile', param.file)
      formData.append('communityId', this.$store.state.app.currentCommunity.communityId)
      formData.append('fileType', 'PAYMENT_CERT')
      request({
        url: '/file.uploadFile',
        method: 'post',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
      }).then(res => {
        if (res.code === 0) {
          this.form.certPath = res.data
          param.onSuccess()
        } else {
          this.$message.error(res.msg || '上传失败')
          param.onError()
        }
      }).catch(() => {
        param.onError()
      })
    },
    handleCertRemove() {
      this.form.certPath = ''
      this.certFileList = []
    },
    handleClose() {
      this.dialogVisible = false
      this.certFileList = []
      this.feeConfigs = []
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const data = {
          ppId: this.form.ppId,
          paymentName: this.form.paymentName,
          paymentType: this.form.paymentType,
          paymentKeys: this.form.paymentKeys.map(k => ({ columnKey: k.columnKey, columnValue: k.columnValue })),
          certPath: this.form.certPath,
          payType: this.form.payType,
          state: this.form.state,
          remark: this.form.remark,
          configIds: this.form.configIds,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        await updatePaymentPool(data)
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
