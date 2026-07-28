<template>
  <div class="evasion-config-manage">
    <el-card shadow="never" class="form-card">
      <div slot="header" class="card-header">
        <span>逃费检测设置</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="160px" style="max-width: 600px">
        <el-form-item label="开启逃费检测" prop="evasionOpen">
          <el-switch v-model="form.evasionOpen" active-value="Y" inactive-value="N" />
        </el-form-item>
        <el-form-item label="查询天数" prop="queryDays">
          <el-input-number v-model="form.queryDays" :min="1" :max="365" :step="1" style="width: 180px" />
          <span class="form-tip">查找多少天内的出场记录</span>
        </el-form-item>
        <el-form-item label="停车时长阈值(分钟)" prop="queryMinutes">
          <el-input-number v-model="form.queryMinutes" :min="1" :max="1440" :step="1" style="width: 180px" />
          <span class="form-tip">停车时长在此范围内的记录作为参考</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { listCarInoutEvasionConfig, saveCarInoutEvasionConfig } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListCarInoutEvasionConfig',
  data() {
    return {
      saving: false,
      form: {
        evasionOpen: 'N',
        queryDays: 1,
        queryMinutes: 30
      },
      rules: {
        queryDays: [{ required: true, message: '请输入查询天数', trigger: 'blur' }],
        queryMinutes: [{ required: true, message: '请输入停车时长阈值', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.fetchConfig()
  },
  methods: {
    async fetchConfig() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarInoutEvasionConfig({ communityId, page: 1, row: 1 })
        const data = res.data
        if (data && data.length > 0) {
          this.form = {
            configId: data[0].configId,
            evasionOpen: data[0].evasionOpen || 'N',
            queryDays: parseInt(data[0].queryDays) || 1,
            queryMinutes: parseInt(data[0].queryMinutes) || 30
          }
        }
      } catch (e) {
        console.error('加载逃费配置失败', e)
      }
    },
    async handleSave() {
      try {
        await this.$refs.form.validate()
      } catch {
        return
      }
      this.saving = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveCarInoutEvasionConfig({ ...this.form, communityId })
        this.$message.success('保存成功')
        this.fetchConfig()
      } catch (e) {
        this.$message.error(e.message || '保存失败')
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.form-card { max-width: 800px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.form-tip { margin-left: 12px; color: #909399; font-size: 13px; }
</style>
