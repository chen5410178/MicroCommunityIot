<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加属性配置"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="属性表" prop="tableName">
        <el-select v-model="form.tableName" placeholder="请选择属性表" clearable style="width: 100%">
            <el-option label="房屋属性" value="building_room_attr" />
            <el-option label="业主属性" value="building_owner_attr" />
            <el-option label="小区属性" value="building_community_attr" />
            <el-option label="设备属性" value="machine_attr" />
            <el-option label="停车场属性" value="parking_area_attr" />
            <el-option label="车辆属性" value="owner_car_attr" />
        </el-select>
      </el-form-item>
      <el-form-item label="域" prop="domain">
        <el-input v-model="form.domain" placeholder="请填写域" />
      </el-form-item>
      <el-form-item label="规格名称" prop="specName">
        <el-input v-model="form.specName" placeholder="请填写规格名称" />
      </el-form-item>
      <el-form-item label="规格编码" prop="specCd">
        <el-input v-model="form.specCd" placeholder="请填写规格编码" />
      </el-form-item>
      <el-form-item label="说明" prop="specHoldplace">
        <el-input v-model="form.specHoldplace" placeholder="请填写说明" />
      </el-form-item>
      <el-form-item label="是否必填" prop="required">
        <el-select v-model="form.required" placeholder="请选择是否必填" clearable style="width: 100%">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="展示" prop="specShow">
        <el-select v-model="form.specShow" placeholder="请选择展示" clearable style="width: 100%">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="值类型" prop="specValueType">
        <el-select v-model="form.specValueType" placeholder="请选择值类型" clearable style="width: 100%">
            <el-option label="字符串" value="1001" />
            <el-option label="整数" value="2002" />
            <el-option label="金额" value="3003" />
        </el-select>
      </el-form-item>
      <el-form-item label="规格类型" prop="specType">
        <el-select v-model="form.specType" placeholder="请选择规格类型" clearable style="width: 100%">
            <el-option label="input" value="2233" />
            <el-option label="select" value="3344" />
        </el-select>
      </el-form-item>
      <el-form-item label="查询显示" prop="listShow">
        <el-select v-model="form.listShow" placeholder="请选择查询显示" clearable style="width: 100%">
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveAttrSpec } from '@/api/dev/devApi'

export default {
  name: 'AddAttrSpec',
  props: { visible: { type: Boolean, default: false } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        tableName: '',
        domain: '',
        specName: '',
        specCd: '',
        specHoldplace: '',
        required: '',
        specShow: '',
        specValueType: '',
        specType: '',
        listShow: ''
      },
      rules: {
        tableName: [{ required: true, message: '属性表不能为空', trigger: 'blur' }],
        domain: [{ required: true, message: '域不能为空', trigger: 'blur' }, { max: 100, message: '域不能超过100字符', trigger: 'blur' }],
        specName: [{ required: true, message: '规格名称不能为空', trigger: 'blur' }, { max: 100, message: '规格名称不能超过100字符', trigger: 'blur' }],
        specCd: [{ required: true, message: '规格编码不能为空', trigger: 'blur' }, { max: 100, message: '规格编码不能超过100字符', trigger: 'blur' }],
        specHoldplace: [{ required: true, message: '说明不能为空', trigger: 'blur' }, { max: 200, message: '说明不能超过200字符', trigger: 'blur' }],
        required: [{ required: true, message: '是否必填不能为空', trigger: 'blur' }],
        specShow: [{ required: true, message: '展示不能为空', trigger: 'blur' }],
        specValueType: [{ required: true, message: '值类型不能为空', trigger: 'blur' }],
        specType: [{ required: true, message: '规格类型不能为空', trigger: 'blur' }],
        listShow: [{ required: true, message: '查询显示不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        await saveAttrSpec(this.form)
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>
