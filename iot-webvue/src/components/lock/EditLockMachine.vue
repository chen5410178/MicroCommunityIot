<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改门锁"
    width="650px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="门锁ID" prop="machineId">
        <el-input v-model="form.machineId" disabled maxlength="30" />
      </el-form-item>
      <el-form-item label="门锁名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="必填，请填写门锁名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="门锁编号" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="必填，请填写门锁编号" maxlength="64" />
      </el-form-item>
      <el-form-item label="房屋" prop="roomId">
        <el-input v-model="form.roomName" disabled placeholder="必填，请选择房屋" style="width: calc(100% - 80px)" />
        <el-button style="margin-left: 8px" @click="roomVisible = true">选择</el-button>
      </el-form-item>
      <el-form-item label="门禁厂家" prop="implBean">
        <el-select v-model="form.implBean" placeholder="必填，请选择门禁厂家" style="width: 100%" @change="handleFactoryChange">
          <el-option v-for="item in factoryList" :key="item.factoryId" :label="item.factoryName" :value="item.factoryId" />
        </el-select>
      </el-form-item>
      <el-form-item v-for="(item, index) in form.lockMachineFactorySpecList" :key="index" :label="item.specName">
        <el-input v-model="item.value" :placeholder="item.remark" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
    <RoomTree :visible.sync="roomVisible" @confirm="handleRoomConfirm" />
  </el-dialog>
</template>

<script>
import { updateLockMachine, listLockMachineFactorys } from '@/api/lock/lockApi'
import RoomTree from '@/components/accessControl/RoomTree.vue'

export default {
  name: 'EditLockMachine',
  components: { RoomTree },
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      roomVisible: false,
      factoryList: [],
      form: {
        machineId: '',
        machineName: '',
        machineCode: '',
        roomId: '',
        roomName: '',
        implBean: '',
        lockMachineFactorySpecList: [],
        lockMachineParamDtoList: []
      },
      rules: {
        machineId: [
          { required: true, message: '门锁ID不能为空', trigger: 'blur' },
          { max: 30, message: '门锁ID不能超过30', trigger: 'blur' }
        ],
        machineName: [
          { required: true, message: '门锁名称不能为空', trigger: 'blur' },
          { max: 200, message: '门锁名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '门锁编号不能为空', trigger: 'blur' },
          { max: 64, message: '门锁编号不能超过64', trigger: 'blur' }
        ],
        roomId: [
          { required: true, message: '房屋不能为空', trigger: 'change' }
        ],
        implBean: [
          { required: true, message: '门禁厂家不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.loadFactoryList().then(() => {
          this.initForm()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadFactoryList() {
      try {
        const res = await listLockMachineFactorys({ page: -1, row: 100 })
        this.factoryList = res.data || []
      } catch (e) {
        console.error('加载厂家列表失败', e)
      }
    },
    initForm() {
      const data = this.editData
      this.form = {
        machineId: data.machineId || '',
        machineName: data.machineName || '',
        machineCode: data.machineCode || '',
        roomId: data.roomId || '',
        roomName: data.roomName || '',
        implBean: data.implBean || '',
        lockMachineFactorySpecList: [],
        lockMachineParamDtoList: data.lockMachineParamDtoList || []
      }
      if (data.implBean) {
        this.loadFactorySpecs(data.implBean, data.lockMachineParamDtoList || [])
      }
    },
    loadFactorySpecs(factoryId, paramDtoList) {
      const factory = this.factoryList.find(f => f.factoryId === factoryId)
      if (factory && factory.lockMachineFactorySpecList) {
        const specList = factory.lockMachineFactorySpecList.map(s => {
          const matchedParam = paramDtoList.find(p => p.specCd === s.specCd)
          return { ...s, value: matchedParam ? matchedParam.value : '' }
        })
        this.form.lockMachineFactorySpecList = specList
      }
    },
    handleFactoryChange(val) {
      if (!val) {
        this.form.lockMachineFactorySpecList = []
        return
      }
      const factory = this.factoryList.find(f => f.factoryId === val)
      if (factory && factory.lockMachineFactorySpecList) {
        this.form.lockMachineFactorySpecList = factory.lockMachineFactorySpecList.map(s => ({ ...s, value: '' }))
      }
    },
    handleRoomConfirm({ roomId, roomName }) {
      this.form.roomId = roomId
      this.form.roomName = roomName
      this.$refs.formRef && this.$refs.formRef.validateField('roomId')
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.form.lockMachineFactorySpecList = []
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const data = { ...this.form }
        data.communityId = this.$store.state.app.currentCommunity.communityId
        await updateLockMachine(data)
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
