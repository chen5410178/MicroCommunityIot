<template>
  <div class="add-menu-view">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px;">
      <el-form-item label="菜单名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，2-10字符" maxlength="10" />
      </el-form-item>
      <el-form-item label="菜单地址" prop="url">
        <el-input v-model="form.url" placeholder="必填，最大200字符" maxlength="200" />
      </el-form-item>
      <el-form-item label="序列" prop="seq">
        <el-input v-model.number="form.seq" placeholder="必填，整数" />
      </el-form-item>
      <el-form-item label="菜单显示" prop="isShow">
        <el-select v-model="form.isShow" placeholder="必填，请选择菜单显示" style="width: 100%">
          <el-option label="显示菜单" value="Y" />
          <el-option label="不显示菜单" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="选填，最大200字符" maxlength="200" :rows="3" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'AddMenuView',
  data() {
    return {
      form: {
        flowComponent: 'addMenuView',
        name: '',
        url: '',
        seq: '',
        isShow: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '菜单名称不能为空', trigger: 'blur' },
          { min: 2, max: 10, message: '菜单名称必须在2至10字符之间', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '菜单地址不能为空', trigger: 'blur' },
          { max: 200, message: '菜单地址不能超过200', trigger: 'blur' }
        ],
        seq: [
          { required: true, message: '序列不能为空', trigger: 'blur' },
          { type: 'number', message: '序列必须为整数', trigger: 'blur' }
        ],
        isShow: [
          { required: true, message: '菜单显示不能为空', trigger: 'change' }
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
