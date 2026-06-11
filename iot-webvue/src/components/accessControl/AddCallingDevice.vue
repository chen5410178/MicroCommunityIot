<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加呼叫设备"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="form.deviceName" placeholder="必填，请填写设备名称" maxlength="30" />
      </el-form-item>
      <el-form-item label="呼叫号码" prop="callingNum">
        <el-input v-model="form.callingNum" placeholder="必填，请填写呼叫号码" maxlength="32" />
      </el-form-item>
      <el-form-item label="云呼平台" prop="platformId">
        <el-select v-model="form.platformId" placeholder="请选择云呼平台" style="width: 100%">
          <el-option v-for="item in platformList" :key="item.platformId" :label="item.platformName" :value="item.platformId" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="必填，请填写备注" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveCallingDevice, listCallingPlatform } from '@/api/accessControl/callingDeviceApi'

export default {
  name: 'AddCallingDevice',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      platformList: [],
      form: {
        deviceName: '',
        callingNum: '',
        platformId: '',
        remark: ''
      },
      rules: {
        deviceName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' },
          { max: 30, message: '设备名称不能超过30', trigger: 'blur' }
        ],
        callingNum: [
          { required: true, message: '呼叫号码不能为空', trigger: 'blur' },
          { max: 32, message: '呼叫号码不能超过32', trigger: 'blur' }
        ],
        platformId: [{ required: true, message: '请选择云呼平台', trigger: 'change' }],
        remark: [
          { required: true, message: '备注不能为空', trigger: 'blur' },
          { max: 200, message: '备注不能超过200', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  mounted() {
    this.loadPlatformList()
  },
  methods: {
    async loadPlatformList() {
      try {
        const res = await listCallingPlatform({ page: 1, row: 100 })
        this.platformList = res.data || []
      } catch (e) {
        console.error('加载云呼平台列表失败', e)
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
        await saveCallingDevice(this.form)
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
