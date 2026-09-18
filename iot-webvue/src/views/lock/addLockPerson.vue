<template>
  <div class="add-lock-person">
    <el-card shadow="never" class="form-card">
      <div slot="header" class="card-header">
        <span>添加授权</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="门锁设备" prop="machineId">
          <el-select v-model="form.machineId" placeholder="必填，请选择门锁" style="width: 400px">
            <el-option
              v-for="item in lockMachineList"
              :key="item.machineId"
              :label="item.machineName"
              :value="item.machineId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="业主" prop="personId">
          <el-input v-model="form.name" placeholder="必填，请选择业主" readonly style="width: 320px" />
          <el-button type="primary" size="small" style="margin-left: 8px" @click="handleOpenSearchOwner">选择业主</el-button>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="必填，请选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="必填，请选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item label="开锁方式" prop="openModel">
          <el-select v-model="form.openModel" placeholder="必填，请选择开锁方式" style="width: 400px">
            <el-option label="密码开锁" value="1001" />
            <el-option label="蓝牙开锁" value="2002" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="cardNumber" v-if="form.openModel === '1001'">
          <el-input
            v-model="form.cardNumber"
            placeholder="必填，请填写密码"
            maxlength="64"
            show-word-limit
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleBack">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading" style="margin-left: 16px">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <SearchOwner :visible.sync="searchVisible" @selected="handleSelected" />
  </div>
</template>

<script>
import { saveLockPerson, listLockMachines } from '@/api/lock/lockApi'
import SearchOwner from '@/components/monitor/SearchOwner.vue'
import { getCurrentCommunity } from '@/utils/auth'

const OPEN_MODEL_PW = '1001'

export default {
  name: 'AddLockPerson',
  components: { SearchOwner },
  data() {
    const validateEndTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error('结束时间不能为空'))
      } else if (this.form.startTime && new Date(value) <= new Date(this.form.startTime)) {
        callback(new Error('结束时间不能早于开始时间'))
      } else {
        callback()
      }
    }
    const validateCardNumber = (rule, value, callback) => {
      if (this.form.openModel !== OPEN_MODEL_PW) {
        callback()
      } else if (!value) {
        callback(new Error('密码不能为空'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      searchVisible: false,
      lockMachineList: [],
      form: {
        lpId: '',
        machineId: '',
        personId: '',
        name: '',
        startTime: '',
        endTime: '',
        openModel: '',
        state: '1001',
        communityId: '',
        cardNumber: ''
      },
      rules: {
        machineId: [{ required: true, message: '门锁设备不能为空', trigger: 'change' }],
        personId: [{ required: true, message: '业主不能为空', trigger: 'change' }],
        startTime: [{ required: true, message: '开始时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, validator: validateEndTime, trigger: 'change' }],
        openModel: [{ required: true, message: '开锁方式不能为空', trigger: 'change' }],
        cardNumber: [
          { required: true, validator: validateCardNumber, trigger: 'blur' },
          { max: 64, message: '密码不能超过64', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    'form.openModel'(val) {
      if (val !== OPEN_MODEL_PW) {
        this.form.cardNumber = ''
        this.$refs.formRef && this.$refs.formRef.clearValidate('cardNumber')
      }
    }
  },
  mounted() {
    this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
    if (!this.form.communityId) {
      this.$message.error('缺少小区信息')
      return
    }
    this.fetchLockMachines()
  },
  methods: {
    async fetchLockMachines() {
      try {
        const res = await listLockMachines({ page: -1, row: 1000, communityId: this.form.communityId })
        this.lockMachineList = res.data || []
      } catch (e) {
        this.$message.error(e.message || '查询门锁列表失败')
      }
    },
    handleOpenSearchOwner() {
      this.searchVisible = true
    },
    handleSelected(row) {
      this.form.personId = row.memberId
      this.form.name = row.name
      this.$refs.formRef && this.$refs.formRef.validateField('personId')
    },
    async handleSubmit() {
      if (!this.form.communityId) {
        this.$message.error('缺少小区信息')
        return
      }
      try {
        await this.$refs.formRef.validate()
      } catch {
        return
      }
      this.loading = true
      try {
        await saveLockPerson({ ...this.form })
        this.$message.success('授权成功')
        this.$router.back()
      } catch (e) {
        this.$message.error(e.msg || e.message || '授权失败')
      } finally {
        this.loading = false
      }
    },
    handleBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.form-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
