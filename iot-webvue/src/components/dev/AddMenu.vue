<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="新增菜单"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
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
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { configMenu } from '@/api/dev/devApi'

export default {
  name: 'AddMenu',
  props: {
    visible: { type: Boolean, default: false },
    gId: { type: String, default: '' },
    storeType: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        name: '',
        url: '',
        seq: '',
        isShow: 'Y',
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
          { max: 200, message: '描述不能超过200', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
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
        const resource = (this.form.url.indexOf('/flow') === 0 && this.form.url.length > 5) ? this.form.url.substring(5) : this.form.url
        await configMenu({
          data: [
            { flowComponent: 'viewMenuGroupInfo', gId: this.gId },
            {
              flowComponent: 'addMenuView',
              name: this.form.name,
              url: this.form.url,
              seq: this.form.seq,
              isShow: this.form.isShow,
              description: this.form.description
            },
            {
              flowComponent: 'addPrivilegeView',
              name: this.form.name,
              resource: resource,
              domain: this.storeType,
              description: this.form.description
            }
          ]
        })
        this.$message.success('新增成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '新增失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
