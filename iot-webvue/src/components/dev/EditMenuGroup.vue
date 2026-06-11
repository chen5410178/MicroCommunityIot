<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改菜单组"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="组名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写组名称" />
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <el-input v-model="form.icon" placeholder="必填，请填写图标" />
      </el-form-item>
      <el-form-item label="标签" prop="label">
        <el-select v-model="form.label" placeholder="可填，请选择标签" clearable style="width: 100%">
          <el-option label="HOT" value="HOT" />
          <el-option label="TOP" value="TOP" />
        </el-select>
      </el-form-item>
      <el-form-item label="序列" prop="seq">
        <el-input v-model="form.seq" placeholder="必填，请填写序列" />
      </el-form-item>
      <el-form-item label="组类型" prop="groupType">
        <el-select v-model="form.groupType" placeholder="选填，请选择组类型" clearable style="width: 100%">
          <el-option label="WEB端" value="P_WEB" />
          <el-option label="APP端" value="P_APP" />
        </el-select>
      </el-form-item>
      <el-form-item label="归属商户" prop="storeType">
        <el-select v-model="form.storeType" placeholder="请选择商户类型" clearable style="width: 100%">
          <el-option label="运营团队" value="800900000001" />
          <el-option label="代理商" value="800900000002" />
          <el-option label="物业" value="800900000003" />
          <el-option label="物流公司" value="800900000004" />
          <el-option label="商家" value="800900000005" />
          <el-option label="开发团队" value="800900000000" />
          <el-option label="跑腿" value="800900000006" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="选填，请填写描述" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMenuGroup } from '@/api/dev/devApi'

export default {
  name: 'EditMenuGroup',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        gId: '',
        name: '',
        icon: '',
        label: '',
        seq: '',
        groupType: '',
        storeType: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '组名称不能为空', trigger: 'blur' },
          { min: 2, max: 10, message: '组名称必须在2至10字符之间', trigger: 'blur' }
        ],
        icon: [
          { required: true, message: '图标不能为空', trigger: 'blur' },
          { min: 2, max: 20, message: '图标必须在2至20字符之间', trigger: 'blur' }
        ],
        label: [{ max: 20, message: '标签错误', trigger: 'blur' }],
        seq: [
          { required: true, message: '序列不能为空', trigger: 'blur' },
          { pattern: /^\d+$/, message: '序列必须为整数', trigger: 'blur' }
        ],
        description: [{ max: 200, message: '备注内容不能超过200', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
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
        await updateMenuGroup(this.form)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
