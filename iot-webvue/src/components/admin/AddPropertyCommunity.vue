<template>
  <el-dialog :visible.sync="dialogVisible" title="加入小区" width="700px" @close="handleClose">
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="开通小区">
        <el-select v-model="form.communityIds" multiple placeholder="选填，请选择开通小区" style="width: 100%">
          <el-option v-for="item in communitys" :key="item.communityId" :label="item.name" :value="item.communityId" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.communityIds && form.communityIds.length > 0" label="功能权限">
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
import { joinCommunity, listMenuGroup, listFreeCommunity } from '@/api/admin/propertyCommunityApi'

export default {
  name: 'AddPropertyCommunity',
  props: {
    storeId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      menuGroups: [],
      communitys: [],
      form: {
        memberId: '',
        communityIds: [],
        groupIds: [],
        isAll: true
      }
    }
  },
  methods: {
    async open() {
      this.dialogVisible = true
      this.form.memberId = this.storeId
      await this.loadMenuGroups()
      await this.loadFreeCommunitys()
    },
    handleClose() {
      this.dialogVisible = false
      this.form = { memberId: '', communityIds: [], groupIds: [], isAll: true }
      this.menuGroups = []
      this.communitys = []
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
        this.form.groupIds = this.menuGroups.map(item => item.gId)
        this.form.isAll = true
      } catch { /* ignore */ }
    },
    async loadFreeCommunitys() {
      try {
        const res = await listFreeCommunity({ communityName: '', page: 1, row: 100 })
        this.communitys = res.data || []
      } catch { /* ignore */ }
    },
    async handleSubmit() {
      this.loading = true
      try {
        await joinCommunity(this.form)
        this.$message.success('加入成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '加入失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
