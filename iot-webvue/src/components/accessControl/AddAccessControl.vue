<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加门禁"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="门禁编码" prop="machineCode">
            <el-input v-model="form.machineCode" placeholder="必填，请填写门禁编码" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="门禁名称" prop="machineName">
            <el-input v-model="form.machineName" placeholder="必填，请填写门禁名称" maxlength="200" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="设备IP" prop="machineIp">
            <el-input v-model="form.machineIp" placeholder="请填写设备IP" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备Mac" prop="machineMac">
            <el-input v-model="form.machineMac" placeholder="请填写设备Mac" maxlength="64" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="方向" prop="direction">
            <el-select v-model="form.direction" placeholder="请选择方向" style="width: 100%">
              <el-option label="进" value="3306" />
              <el-option label="出" value="3307" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="位置" prop="locationId">
            <el-select v-model="form.locationId" placeholder="请选择位置" style="width: 100%">
              <el-option v-for="item in locationList" :key="item.locationId" :label="item.locationName" :value="item.locationId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="门禁厂家" prop="implBean">
            <el-select v-model="form.implBean" placeholder="请选择厂家" style="width: 100%">
              <el-option v-for="item in hmList" :key="item.hmId" :label="item.hmName" :value="item.hmId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="监控视频" prop="monitorId">
            <el-select v-model="form.monitorId" placeholder="请选择监控视频" style="width: 100%">
              <el-option label="没有监控" :value="-1" />
              <el-option v-for="item in monitorList" :key="item.monitorId" :label="item.monitorName" :value="item.monitorId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开门提示" prop="promptSwitch">
            <el-select v-model="form.promptSwitch" placeholder="请选择" style="width: 100%">
              <el-option label="ON" value="ON" />
              <el-option label="OFF" value="OFF" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="呼叫平台" prop="platformId">
            <el-input v-model="form.platformId" placeholder="请填写呼叫平台" maxlength="64" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="呼叫号码" prop="callingNum">
            <el-input v-model="form.callingNum" placeholder="请填写呼叫号码" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" placeholder="请填写备注" maxlength="200" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="form.promptSwitch === 'ON'">
        <el-col :span="12">
          <el-form-item label="正常提示" prop="normalPrompt">
            <el-input v-model="form.normalPrompt" type="textarea" :rows="2" placeholder="请输入正常提示" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="欠费提示" prop="owePrompt">
            <el-input v-model="form.owePrompt" type="textarea" :rows="2" placeholder="请输入欠费提示" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveAccessControl, listMachineLocation, listHardwareManufacturer, listMonitorMachine } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddAccessControl',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      locationList: [],
      hmList: [],
      monitorList: [],
      form: {
        machineCode: '',
        machineName: '',
        machineIp: '',
        machineMac: '',
        direction: '3306',
        locationId: '',
        implBean: '',
        monitorId: -1,
        promptSwitch: 'ON',
        normalPrompt: '',
        owePrompt: '',
        platformId: '',
        callingNum: '',
        remark: ''
      },
      rules: {
        machineCode: [
          { required: true, message: '门禁编码不能为空', trigger: 'blur' },
          { max: 64, message: '门禁编码不能超过64', trigger: 'blur' }
        ],
        machineName: [
          { required: true, message: '门禁名称不能为空', trigger: 'blur' },
          { max: 200, message: '门禁名称不能超过200', trigger: 'blur' }
        ],
        direction: [{ required: true, message: '请选择方向', trigger: 'change' }],
        locationId: [{ required: true, message: '请选择位置', trigger: 'change' }],
        implBean: [{ required: true, message: '请选择门禁厂家', trigger: 'change' }],
        promptSwitch: [{ required: true, message: '请选择开门提示', trigger: 'change' }],
        normalPrompt: [{ required: true, message: '正常提示不能为空', trigger: 'blur' }],
        owePrompt: [{ required: true, message: '欠费提示不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    'form.promptSwitch'(val) {
      if (val === 'OFF') {
        this.form.normalPrompt = ''
        this.form.owePrompt = ''
        this.$refs.formRef && this.$refs.formRef.clearValidate(['normalPrompt', 'owePrompt'])
      }
    }
  },
  methods: {
    open() {
      this.dialogVisible = true
      this.loadOptions()
    },
    async loadOptions() {
      const community = getCurrentCommunity()
      const communityId = community ? community.communityId : ''
      try {
        const [locRes, hmRes, monitorRes] = await Promise.all([
          listMachineLocation({ page: 1, row: 100, communityId }),
          listHardwareManufacturer({ hmType: '1001', page: 1, row: 100 }),
          listMonitorMachine({ page: 1, row: 100, communityId })
        ])
        this.locationList = locRes.data || []
        this.hmList = hmRes.data || []
        this.monitorList = monitorRes.data || []
      } catch (e) {
        console.error('加载下拉选项失败', e)
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.form.monitorId = -1
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        await saveAccessControl({ ...this.form, communityId, lat: '0', lng: '0' })
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
