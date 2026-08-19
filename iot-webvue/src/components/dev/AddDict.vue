<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加字典"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="类型" prop="specId">
        <el-select
          v-model="form.specId"
          placeholder="必填，请选择类型"
          style="width: 100%"
          :disabled="isSpecLocked"
          @visible-change="loadSpecs"
        >
          <el-option
            v-for="item in innerSpecList"
            :key="item.specId"
            :label="item.specName"
            :value="item.specId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="值" prop="statusCd">
        <el-input v-model="form.statusCd" placeholder="必填，请填写值" maxlength="64" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" placeholder="必填，请填写描述" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveDict, listDictSpecs } from '@/api/dev/devApi'

export default {
  name: 'AddDict',
  props: {
    visible: { type: Boolean, default: false },
    specList: { type: Array, default: () => [] },
    defaultSpecId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      innerSpecList: [],
      form: {
        specId: '',
        name: '',
        statusCd: '',
        description: ''
      },
      rules: {
        specId: [
          { required: true, message: '类型不能为空', trigger: 'change' }
        ],
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 50, message: '名称不能超过50', trigger: 'blur' }
        ],
        statusCd: [
          { required: true, message: '值不能为空', trigger: 'blur' },
          { max: 64, message: '值不能超过64', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '描述不能为空', trigger: 'blur' },
          { max: 200, message: '描述不能超过200', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    isSpecLocked() {
      return !!this.defaultSpecId
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.innerSpecList = (this.specList && this.specList.length > 0) ? this.specList : []
        if (this.defaultSpecId) {
          this.form.specId = this.defaultSpecId
        }
      } else {
        this.form.specId = ''
      }
      this.dialogVisible = val
    },
    dialogVisible(val) { this.$emit('update:visible', val) },
    specList: {
      handler(val) {
        if (val && val.length > 0) {
          this.innerSpecList = val
        }
      },
      deep: true
    }
  },
  methods: {
    async loadSpecs(show) {
      if (show && this.innerSpecList.length === 0) {
        try {
          const res = await listDictSpecs({ page: 1, row: 1000, statusCd: '0' })
          this.innerSpecList = res.data || []
        } catch { /* ignore */ }
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
        await saveDict(this.form)
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
