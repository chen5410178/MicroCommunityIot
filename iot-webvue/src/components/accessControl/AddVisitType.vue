<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加访客类型"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="类型名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写类型名称" maxlength="30" />
      </el-form-item>
      <el-form-item label="通行方式" prop="visitWay">
        <el-select v-model="form.visitWay" placeholder="请选择通行方式" style="width: 100%">
          <el-option v-for="item in visitWayList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核方式" prop="auditWay">
        <el-select v-model="form.auditWay" placeholder="请选择审核方式" style="width: 100%">
          <el-option v-for="item in auditWayList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="拜访天数" prop="visitDay">
        <el-input v-model="form.visitDay" type="number" placeholder="选填，大于0的整数" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveVisitType } from '@/api/accessControl/accessControlApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddVisitType',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      visitWayList: [],
      auditWayList: [],
      form: {
        name: '',
        visitWay: '',
        auditWay: '',
        visitDay: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '类型名称不能为空', trigger: 'blur' },
          { max: 30, message: '类型名称不能超过30', trigger: 'blur' }
        ],
        visitWay: [{ required: true, message: '请选择通行方式', trigger: 'change' }],
        auditWay: [{ required: true, message: '请选择审核方式', trigger: 'change' }],
        visitDay: [
          {
            validator: (_rule, value, callback) => {
              if (value === '' || value === null || value === undefined) {
                callback()
              } else if (!/^\d+$/.test(String(value)) || Number(value) <= 0) {
                callback(new Error('拜访天数必须为大于0的整数'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        remark: [{ max: 512, message: '备注不能超过512', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  mounted() {
    this.loadDicts()
  },
  methods: {
    async loadDicts() {
      try {
        const [wayRes, auditRes] = await Promise.all([
          listDicts({ tableName: 'visit_type', tableColumns: 'visit_way' }),
          listDicts({ tableName: 'visit_type', tableColumns: 'audit_way' })
        ])
        this.visitWayList = wayRes || []
        this.auditWayList = auditRes || []
      } catch (e) {
        console.error('加载字典失败', e)
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
        const data = {
          ...this.form,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        await saveVisitType(data)
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
