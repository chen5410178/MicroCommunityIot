<template>
  <div class="add-monitor-machine-owner">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>新增业主授权</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="授权类型" prop="authType">
          <el-radio-group v-model="form.authType" @change="handleAuthTypeChange">
            <el-radio label="1">单个业主</el-radio>
            <el-radio label="2">全部业主</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.authType === '1'" label="业主名称" prop="ownerName">
          <el-input
            v-model="form.ownerName"
            placeholder="请点击选择按钮选择业主"
            readonly
            style="width: 400px"
          />
          <el-button type="primary" size="small" style="margin-left: 8px" @click="searchVisible = true">选择业主</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="$router.back()">返回</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading" style="margin-left: 16px">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <SearchOwner :visible.sync="searchVisible" @selected="handleSelected" />
  </div>
</template>

<script>
import { saveMonitorMachinePerson } from '@/api/monitor/monitorApi'
import SearchOwner from '@/components/monitor/SearchOwner.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMonitorMachineOwner',
  components: { SearchOwner },
  data() {
    return {
      loading: false,
      searchVisible: false,
      machineId: '',
      form: {
        authType: '1',
        ownerId: '',
        ownerName: ''
      },
      rules: {
        ownerName: [
          { required: true, message: '请选择业主', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
  },
  methods: {
    handleAuthTypeChange(val) {
      if (val === '2') {
        this.form.ownerId = ''
        this.form.ownerName = ''
      }
    },
    handleSelected(row) {
      this.form.ownerId = row.memberId
      this.form.ownerName = row.name
    },
    async handleSubmit() {
      if (this.form.authType === '1') {
        try {
          await this.$refs.formRef.validate()
        } catch { return }
      }
      if (!this.machineId) {
        this.$message.warning('设备ID不能为空')
        return
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const data = {
          machineId: this.machineId,
          personType: '1001',
          communityId
        }
        if (this.form.authType === '2') {
          data.personId = '9999'
          data.personName = '全部业主'
        } else {
          data.personId = this.form.ownerId
          data.personName = this.form.ownerName
        }
        await saveMonitorMachinePerson(data)
        this.$message.success('添加成功')
        this.$router.back()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
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
