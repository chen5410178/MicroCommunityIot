<template>
  <el-dialog :visible.sync="dialogVisible" title="修改小区功能" width="600px" @close="handleClose">
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="开通小区">
        <el-input v-model="form.name" disabled />
      </el-form-item>
      <el-form-item label="功能权限">
        <el-checkbox v-model="form.isAll" @change="changeAll">全选</el-checkbox>
        <el-checkbox-group v-model="form.groupIds" @change="changeItem">
          <el-checkbox v-for="item in menuGroups" :key="item.gId" :label="item.gId">{{ item.name }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMenuGroupCommunity, listMenuGroup, listMenuGroupCommunity } from '@/api/admin/propertyCommunityApi'

export default {
  name: 'EditPropertyCommunity',
  props: {
    editData: { type: Object, default: null },
    storeId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      menuGroups: [],
      form: {
        memberId: '',
        communityId: '',
        name: '',
        groupIds: [],
        isAll: false
      }
    }
  },
  watch: {
    dialogVisible(val) {
      if (val && this.editData) {
        this.form.communityId = this.editData.communityId
        this.form.name = this.editData.name
        this.form.memberId = this.storeId
        this.loadMenuGroups()
        this.loadCurrentMenuGroups()
      }
    }
  },
  methods: {
    open() {
      this.dialogVisible = true
    },
    handleClose() {
      this.dialogVisible = false
      this.form = { memberId: '', communityId: '', name: '', groupIds: [], isAll: false }
      this.menuGroups = []
    },
    changeAll(val) {
      this.form.groupIds = []
      if (val) {
        this.menuGroups.forEach(item => {
          this.form.groupIds.push(item.gId)
        })
      }
    },
    changeItem() {
      this.form.isAll = this.form.groupIds.length === this.menuGroups.length
    },
    async loadMenuGroups() {
      try {
        const res = await listMenuGroup({ page: 1, row: 100, storeType: '800900000003' })
        this.menuGroups = res.data || []
      } catch { /* ignore */ }
    },
    async loadCurrentMenuGroups() {
      try {
        const res = await listMenuGroupCommunity({ page: 1, row: 100, communityId: this.form.communityId })
        const data = res.data || []
        this.form.groupIds = data.map(item => item.gId)
        this.changeItem()
      } catch { /* ignore */ }
    },
    async handleSubmit() {
      this.loading = true
      try {
        await updateMenuGroupCommunity(this.form)
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
