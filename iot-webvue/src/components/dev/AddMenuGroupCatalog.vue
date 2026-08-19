<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加菜单组"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="菜单组" prop="gId">
        <el-select v-model="form.gId" placeholder="必填，请选择菜单组" style="width: 100%" filterable>
          <el-option :label="item.name" :value="item.gId" v-for="item in groups" :key="item.gId" />
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
import { saveMenuGroupCatalog, listMenuGroups } from '@/api/dev/devApi'

export default {
  name: 'AddMenuGroupCatalog',
  props: {
    visible: { type: Boolean, default: false },
    caId: { type: String, default: '' },
    storeType: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      groups: [],
      form: {
        gId: ''
      },
      rules: {
        gId: [
          { required: true, message: '菜单组不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) this.fetchGroups()
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchGroups() {
      try {
        const res = await listMenuGroups({
          page: 1,
          row: 500,
          storeType: this.storeType
        })
        this.groups = res.data || []
      } catch (e) {
        this.$message.error(e.msg || '查询菜单组失败')
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
        await saveMenuGroupCatalog({
          gId: this.form.gId,
          caId: this.caId,
          storeType: this.storeType
        })
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
