<template>
  <div class="add-event-pool-manage">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加事件</span>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px" class="add-form">
        <el-form-item label="事件规则" prop="ruleId">
          <el-select v-model="form.ruleId" placeholder="必填，请选择事件规则" style="width: 100%">
            <el-option
              v-for="item in ruleList"
              :key="item.ruleId"
              :label="item.ruleName"
              :value="item.ruleId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="事件类型" prop="eventType">
          <el-select v-model="form.eventType" placeholder="必填，请选择事件类型" style="width: 100%">
            <el-option
              v-for="item in eventTypeList"
              :key="item.statusCd"
              :label="item.statusName || item.statusCd"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="事件描述" prop="eventMsg">
          <el-input v-model="form.eventMsg" type="textarea" :rows="4" placeholder="必填，请填写事件描述" maxlength="512" show-word-limit />
        </el-form-item>
        <el-form-item label="对象类型" prop="objType">
          <el-select v-model="form.objType" placeholder="必填，请选择对象类型" style="width: 100%" @change="handleObjTypeChange">
            <el-option
              v-for="item in objTypeList"
              :key="item.statusCd"
              :label="item.statusName || item.statusCd"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="设备对象" prop="objectName">
          <el-input v-model="form.objectName" placeholder="必填，请输入设备名称" maxlength="128" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写备注" maxlength="512" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { saveEventPool, listEventRules } from '@/api/event/eventApi'
import { listDicts } from '@/api/dev/devApi'

export default {
  name: 'AddEventPoolManage',
  data() {
    return {
      loading: false,
      ruleList: [],
      eventTypeList: [],
      objTypeList: [],
      form: {
        ruleId: '',
        eventType: '',
        eventMsg: '',
        objType: '',
        objectName: ''
      },
      rules: {
        ruleId: [{ required: true, message: '请选择事件规则', trigger: 'change' }],
        eventType: [{ required: true, message: '请选择事件类型', trigger: 'change' }],
        eventMsg: [{ required: true, message: '请输入事件描述', trigger: 'blur' }],
        objType: [{ required: true, message: '请选择对象类型', trigger: 'change' }],
        objectName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadDicts()
  },
  methods: {
    async loadDicts() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const [eventTypeRes, objTypeRes, ruleRes] = await Promise.all([
          listDicts({ tableName: 'event_pool', columnName: 'event_type' }),
          listDicts({ tableName: 'event_object', columnName: 'obj_type' }),
          listEventRules({ communityId, page: 1, row: 200 })
        ])
        this.eventTypeList = eventTypeRes.data || []
        this.objTypeList = objTypeRes.data || []
        this.ruleList = ruleRes.data || []
      } catch { /* ignore */ }
    },
    handleObjTypeChange() {
      this.form.objectName = ''
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveEventPool({
          ...this.form,
          communityId,
          fromType: 'PERSON',
          state: 'W'
        })
        this.$message.success('添加成功')
        this.$router.go(-1)
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    },
    handleBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.add-form { max-width: 600px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
