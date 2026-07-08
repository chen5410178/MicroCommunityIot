<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加路灯"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="必填，请填写路灯名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="设备编号" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="必填，请填写设备编号" maxlength="30" />
      </el-form-item>
      <el-form-item label="位置" prop="locationName">
        <el-input v-model="form.locationName" placeholder="必填，请填写位置" maxlength="64" />
      </el-form-item>
      <el-form-item label="厂家" prop="factoryId">
        <el-select v-model="form.factoryId" placeholder="请选择厂家" style="width: 100%" clearable>
          <el-option
            v-for="item in factoryList"
            :key="item.factoryId"
            :label="item.factoryName"
            :value="item.factoryId"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveLampMachine, listLampMachineFactory } from '@/api/lamp/lampApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddLampMachine',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      factoryList: [],
      form: {
        machineName: '',
        machineCode: '',
        locationName: '',
        factoryId: ''
      },
      rules: {
        machineName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 200, message: '名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '设备编号不能为空', trigger: 'blur' },
          { max: 30, message: '设备编号不能超过30', trigger: 'blur' }
        ],
        locationName: [
          { required: true, message: '位置不能为空', trigger: 'blur' },
          { max: 64, message: '位置不能超过64', trigger: 'blur' }
        ],
        factoryId: [
          { required: true, message: '厂家不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadFactoryList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadFactoryList() {
      try {
        const res = await listLampMachineFactory({ page: 1, row: 100 })
        this.factoryList = res.data || []
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
        await saveLampMachine({ ...this.form, communityId })
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
