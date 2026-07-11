<template>
  <div class="edit-work-license-machine">
    <el-card shadow="never">
      <div slot="header">
        <span>修改工牌</span>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width: 900px;">
        <el-row>
          <el-col :span="12">
            <el-form-item label="名称" prop="machineName">
              <el-input v-model="form.machineName" placeholder="必填，请填写名称" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工牌编号" prop="machineCode">
              <el-input v-model="form.machineCode" placeholder="必填，请填写工牌编号" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="员工" prop="staffName">
              <el-input v-model="form.staffName" placeholder="请选择员工" readonly :disabled="true" style="width: calc(100% - 80px)" />
              <el-button style="margin-left: 8px" @click="chooseStaffVisible = true">选择</el-button>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上报间隔(秒)" prop="posUploadSec">
              <el-input v-model="form.posUploadSec" type="number" placeholder="必填，请填写位置上报间隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="工牌厂家" prop="implBean">
              <el-select v-model="form.implBean" placeholder="必填，请选择工牌厂家" style="width: 100%" clearable>
                <el-option v-for="item in factoryList" :key="item.factoryId" :value="item.factoryId" :label="item.factoryName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工牌版本" prop="version">
              <el-select v-model="form.version" placeholder="必填，请选择工牌版本" style="width: 100%" clearable>
                <el-option value="GBK" label="2.0" />
                <el-option value="UTF-8" label="1.0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <ChooseStaff :visible.sync="chooseStaffVisible" @selected="onStaffSelected" />
  </div>
</template>

<script>
import { updateWorkLicenseMachine, listWorkLicenseMachines } from '@/api/charge/chargeApi'
import { listWorkLicenseFactorys } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'
import ChooseStaff from '@/components/monitor/ChooseStaff.vue'

export default {
  name: 'EditWorkLicenseMachine',
  components: { ChooseStaff },
  data() {
    return {
      loading: false,
      factoryList: [],
      chooseStaffVisible: false,
      form: {
        machineId: '',
        machineName: '',
        machineCode: '',
        staffId: '',
        staffName: '',
        posUploadSec: '',
        implBean: '',
        version: ''
      },
      rules: {
        machineName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 200, message: '名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '工牌编号不能为空', trigger: 'blur' },
          { max: 64, message: '工牌编号不能超过64', trigger: 'blur' }
        ],
        staffName: [
          { required: true, message: '员工不能为空', trigger: 'change' }
        ],
        posUploadSec: [
          { required: true, message: '位置上报间隔不能为空', trigger: 'blur' }
        ],
        implBean: [
          { required: true, message: '工牌厂家不能为空', trigger: 'change' }
        ],
        version: [
          { required: true, message: '工牌版本不能为空', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadFactoryList()
    this.loadMachineDetail()
  },
  methods: {
    async loadFactoryList() {
      try {
        const res = await listWorkLicenseFactorys({ page: 1, row: 100 })
        this.factoryList = res.data || []
      } catch { /* ignore */ }
    },
    async loadMachineDetail() {
      const machineId = this.$route.query.machineId
      if (!machineId) return
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listWorkLicenseMachines({ page: 1, row: 1, machineId, communityId })
        if (res.data && res.data.length > 0) {
          const item = res.data[0]
          this.form.machineId = item.machineId || ''
          this.form.machineName = item.machineName || ''
          this.form.machineCode = item.machineCode || ''
          this.form.staffId = item.staffId || ''
          this.form.staffName = item.staffName || ''
          this.form.posUploadSec = item.posUploadSec || ''
          this.form.implBean = item.implBean || ''
          this.form.version = item.version || ''
        }
      } catch { /* ignore */ }
    },
    handleCancel() {
      this.$router.go(-1)
    },
    onStaffSelected(row) {
      this.form.staffId = row.userId
      this.form.staffName = row.userName
      this.$refs.formRef && this.$refs.formRef.validateField('staffName')
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const data = { ...this.form, communityId }
        await updateWorkLicenseMachine(data)
        this.$message.success('修改成功')
        this.$router.go(-1)
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
