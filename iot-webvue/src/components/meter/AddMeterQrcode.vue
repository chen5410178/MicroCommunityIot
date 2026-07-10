<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加水电二维码"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="qrcodeName">
        <el-input v-model="form.qrcodeName" placeholder="必填，请填写名称" maxlength="128" />
      </el-form-item>
      <el-form-item label="查询方式" prop="queryWay">
        <el-select v-model="form.queryWay" placeholder="必填，请选择查询方式" style="width: 100%">
          <el-option
            v-for="item in queryWayList"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择状态" style="width: 100%">
          <el-option label="启用" value="ON" />
          <el-option label="停用" value="OFF" />
        </el-select>
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写说明" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveMeterQrcode } from '@/api/meter/meterApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMeterQrcode',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      queryWayList: [],
      form: {
        qrcodeName: '',
        communityId: '',
        queryWay: '',
        state: 'ON',
        remark: ''
      },
      rules: {
        qrcodeName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 128, message: '名称不能超过128', trigger: 'blur' }
        ],
        queryWay: [
          { required: true, message: '查询方式不能为空', trigger: 'change' }
        ],
        state: [
          { required: true, message: '状态不能为空', trigger: 'change' }
        ],
        remark: [
          { max: 512, message: '说明不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadQueryWays()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadQueryWays() {
      if (this.queryWayList.length > 0) return
      try {
        const res = await listDicts({ tableName: 'meter_qrcode', tableColumns: 'query_way' })
        this.queryWayList = res || []
      } catch (e) {
        console.error('加载查询方式字典失败', e)
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
        const community = getCurrentCommunity()
        this.form.communityId = community ? community.communityId : ''
        await saveMeterQrcode(this.form)
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
