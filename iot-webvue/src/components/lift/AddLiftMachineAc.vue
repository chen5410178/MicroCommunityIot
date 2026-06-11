<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加电梯门禁"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="电梯名称">
        <el-input v-model="form.machineName" disabled />
      </el-form-item>
      <el-form-item label="门禁设备" prop="acMachineId">
        <el-select v-model="form.acMachineId" placeholder="必填，请选择门禁设备" style="width: 100%">
          <el-option
            v-for="item in acMachineList"
            :key="item.machineId"
            :label="item.machineName"
            :value="item.machineId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="门禁类型" prop="acType">
        <el-select v-model="form.acType" placeholder="必填，请选择门禁类型" style="width: 100%">
          <el-option label="人脸机" value="1001" />
          <el-option label="刷卡机" value="2002" />
          <el-option label="二维码机" value="3003" />
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
import { saveLiftMachineAc, listAccessControls } from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddLiftMachineAc',
  props: {
    visible: { type: Boolean, default: false },
    machineData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      acMachineList: [],
      form: {
        machineId: '',
        machineName: '',
        acMachineId: '',
        acType: ''
      },
      rules: {
        acMachineId: [
          { required: true, message: '门禁设备不能为空', trigger: 'change' }
        ],
        acType: [
          { required: true, message: '门禁类型不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.machineData) {
        this.form.machineId = this.machineData.machineId
        this.form.machineName = this.machineData.machineName
        this.fetchAccessControls()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchAccessControls() {
      const community = getCurrentCommunity()
      try {
        const res = await listAccessControls({ page: 1, row: 100, communityId: community ? community.communityId : '' })
        this.acMachineList = res.data || []
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
        const community = getCurrentCommunity()
        await saveLiftMachineAc({ ...this.form, communityId: community ? community.communityId : '' })
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
