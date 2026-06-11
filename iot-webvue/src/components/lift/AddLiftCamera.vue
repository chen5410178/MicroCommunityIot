<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加电梯相机"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="相机名称" prop="cameraName">
        <el-input v-model="form.cameraName" placeholder="必填，请填写相机名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="相机编号" prop="cameraCode">
        <el-input v-model="form.cameraCode" placeholder="必填，请填写相机编号" maxlength="64" />
      </el-form-item>
      <el-form-item label="相机厂家" prop="factoryId">
        <el-select v-model="form.factoryId" placeholder="必填，请选择相机厂家" style="width: 100%">
          <el-option label="松饼相机" value="sbLiftCamera" />
        </el-select>
      </el-form-item>
      <el-form-item label="电梯名称" prop="liftMachineId">
        <el-select v-model="form.liftMachineId" placeholder="必填，请选择电梯名称" style="width: 100%">
          <el-option
            v-for="item in liftList"
            :key="item.machineId"
            :label="item.machineName"
            :value="item.machineId"
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
import { saveLiftCamera, listLiftMachines } from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddLiftCamera',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      liftList: [],
      form: {
        cameraName: '',
        cameraCode: '',
        factoryId: '',
        liftMachineId: ''
      },
      rules: {
        cameraName: [
          { required: true, message: '相机名称不能为空', trigger: 'blur' },
          { max: 64, message: '相机名称不能超过64', trigger: 'blur' }
        ],
        cameraCode: [
          { required: true, message: '相机编号不能为空', trigger: 'blur' },
          { max: 64, message: '相机编号不能超过64', trigger: 'blur' }
        ],
        factoryId: [
          { required: true, message: '相机厂家不能为空', trigger: 'change' }
        ],
        liftMachineId: [
          { required: true, message: '电梯名称不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadLifts()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadLifts() {
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        const res = await listLiftMachines({ page: 1, row: 100, communityId })
        this.liftList = res.data || []
      } catch (e) {
        console.error('获取电梯列表失败', e)
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
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        await saveLiftCamera({ ...this.form, communityId })
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
