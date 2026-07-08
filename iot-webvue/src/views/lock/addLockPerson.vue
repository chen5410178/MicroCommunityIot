<template>
  <div class="add-lock-person">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加授权</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
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
        <el-form-item label="业主" prop="name">
          <el-input
            v-model="form.name"
            placeholder="必填，请选择业主"
            readonly
            style="width: 320px"
          />
          <el-button type="primary" size="small" style="margin-left: 8px" @click="searchVisible = true">选择业主</el-button>
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
            placeholder="请填写密码"
            maxlength="64"
            show-word-limit
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="$router.back()">取消</el-button>
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

export default {
  name: 'AddLockPerson',
  components: { SearchOwner },
  data() {
    return {
      loading: false,
      searchVisible: false,
      lockMachineList: [],
      form: {
        machineId: '',
        personId: '',
        name: '',
        startTime: '',
        endTime: '',
        openModel: '',
        state: '1001',
        cardNumber: ''
      },
      rules: {
        machineId: [{ required: true, message: '门锁设备不能为空', trigger: 'change' }],
        personId: [{ required: true, message: '业主不能为空', trigger: 'change' }],
        name: [{ required: true, message: '业主不能为空', trigger: 'change' }],
        startTime: [{ required: true, message: '开始时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, message: '结束时间不能为空', trigger: 'change' }],
        openModel: [{ required: true, message: '开锁方式不能为空', trigger: 'change' }],
        cardNumber: [{ max: 64, message: '密码不能超过64', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.fetchLockMachines()
  },
  methods: {
    async fetchLockMachines() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listLockMachines({ page: -1, row: 1000, communityId })
        this.lockMachineList = res.data || []
      } catch { /* ignore */ }
    },
    handleSelected(row) {
      this.form.personId = row.memberId
      this.form.name = row.name
      this.$refs.formRef && this.$refs.formRef.validateField('personId')
      this.$refs.formRef && this.$refs.formRef.validateField('name')
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveLockPerson({ ...this.form, communityId })
        this.$message.success('授权成功')
        this.$router.back()
      } catch (e) {
        this.$message.error(e.msg || '授权失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
