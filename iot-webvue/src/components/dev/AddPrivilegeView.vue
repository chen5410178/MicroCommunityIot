<template>
  <div class="add-privilege-view">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px;">
      <el-form-item label="权限ID" prop="pId">
        <el-input v-model="form.pId" placeholder="必填，请填写权限ID" maxlength="30" />
      </el-form-item>
      <el-form-item label="权限名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写权限名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="资源路径" prop="resource">
        <el-input v-model="form.resource" placeholder="必填，请填写资源路径" maxlength="200" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="选填，最大200字符" maxlength="200" :rows="3" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'AddPrivilegeView',
  data() {
    return {
      form: {
        flowComponent: 'addPrivilegeView',
        pId: '',
        name: '',
        resource: '',
        description: ''
      },
      rules: {
        pId: [
          { required: true, message: '权限ID不能为空', trigger: 'blur' },
          { max: 30, message: '权限ID不能超过30', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '权限名称不能为空', trigger: 'blur' },
          { max: 50, message: '权限名称不能超过50', trigger: 'blur' }
        ],
        resource: [
          { required: true, message: '资源路径不能为空', trigger: 'blur' },
          { max: 200, message: '资源路径不能超过200', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '备注内容不能超过200', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    form: {
      deep: true,
      handler() {
        this.notifyParent()
      }
    }
  },
  methods: {
    notifyParent() {
      this.$refs.formRef && this.$refs.formRef.validate().then(() => {
        this.$emit('notify', { ...this.form })
      }).catch(() => {})
    }
  }
}
</script>
