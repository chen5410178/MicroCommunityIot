<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="设置开关灯时间"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="类型" prop="typeCd">
        <el-select v-model="form.typeCd" placeholder="请选择类型" style="width: 100%" clearable>
          <el-option
            v-for="item in typeList"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="小时" prop="hours">
        <el-input-number v-model="form.hours" :min="0" :max="23" style="width: 100%" />
      </el-form-item>
      <el-form-item label="分钟" prop="min">
        <el-input-number v-model="form.min" :min="0" :max="59" style="width: 100%" />
      </el-form-item>
      <el-form-item label="路灯设备" prop="machineIdList">
        <div style="margin-bottom: 8px;">
          <el-button size="small" type="primary" @click="chooseVisible = true">选择设备</el-button>
        </div>
        <el-tag
          v-for="item in selectedMachines"
          :key="item.machineId"
          closable
          style="margin-right: 8px; margin-bottom: 4px;"
          @close="handleRemoveTag(item)"
        >
          {{ item.machineName }}({{ item.machineCode }})
        </el-tag>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
    <ChooseLampMachine :visible.sync="chooseVisible" @confirm="handleChooseConfirm" />
  </el-dialog>
</template>

<script>
import { saveLampMachineOpenClose } from '@/api/lamp/lampApi'
import { listDicts } from '@/api/dev/devApi'
import ChooseLampMachine from '@/components/lamp/ChooseLampMachine.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddLampMachineOpenClose',
  components: { ChooseLampMachine },
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      chooseVisible: false,
      typeList: [],
      selectedMachines: [],
      form: {
        typeCd: '',
        hours: 0,
        min: 0,
        machineIdList: ''
      },
      rules: {
        typeCd: [
          { required: true, message: '类型不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadTypes()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) },
    selectedMachines: {
      deep: true,
      handler(val) {
        this.form.machineIdList = val.map(m => m.machineId).join(',')
      }
    }
  },
  methods: {
    async loadTypes() {
      try {
        const res = await listDicts({ tableName: 'lamp_machine_operation', columnName: 'type' })
        if (res.data) {
          this.typeList = res.data
        }
      } catch { /* ignore */ }
    },
    handleChooseConfirm(rows) {
      const existing = new Set(this.selectedMachines.map(m => m.machineId))
      rows.forEach(r => { if (!existing.has(r.machineId)) this.selectedMachines.push(r) })
      this.chooseVisible = false
    },
    handleRemoveTag(item) {
      this.selectedMachines = this.selectedMachines.filter(m => m.machineId !== item.machineId)
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.selectedMachines = []
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      if (this.selectedMachines.length === 0) {
        this.$message.warning('请选择路灯设备')
        return
      }
      if (this.form.hours < 0 || this.form.hours > 23) {
        this.$message.warning('小时必须在0-23之间')
        return
      }
      if (this.form.min < 0 || this.form.min > 59) {
        this.$message.warning('分钟必须在0-59之间')
        return
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveLampMachineOpenClose({ ...this.form, communityId })
        this.$message.success('设置成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '设置失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
