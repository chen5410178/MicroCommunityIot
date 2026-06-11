<template>
  <el-dialog :visible.sync="dialogVisible" title="添加物业公司" width="700px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写名称" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" placeholder="必填，请填写地址" />
      </el-form-item>
      <el-form-item label="电话" prop="tel">
        <el-input v-model="form.tel" placeholder="必填，请填写电话" />
      </el-form-item>
      <el-form-item label="公司法人" prop="corporation">
        <el-input v-model="form.corporation" placeholder="必填，请填写公司法人" />
      </el-form-item>
      <el-form-item label="成立日期" prop="foundingTime">
        <el-date-picker v-model="form.foundingTime" type="date" placeholder="必填，请选择成立日期" value-format="yyyy-MM-dd" style="width: 100%" />
      </el-form-item>
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
      <span style="float: left; color: #909399; line-height: 32px;">温馨提示：默认账号为手机号；密码为手机号</span>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveProperty, listMenuGroup, listFreeCommunity } from '@/api/admin/propertyCompanyApi'

export default {
  name: 'AddPropertyCompany',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      menuGroups: [],
      communitys: [],
      form: {
        name: '',
        address: '',
        tel: '',
        corporation: '',
        foundingTime: '',
        communityIds: [],
        groupIds: [],
        isAll: true
      },
      rules: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 100, message: '名称不能超过100字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '地址不能为空', trigger: 'blur' },
          { max: 200, message: '地址不能超过200字符', trigger: 'blur' }
        ],
        tel: [
          { required: true, message: '电话不能为空', trigger: 'blur' },
          { max: 11, message: '电话不能超过11字符', trigger: 'blur' }
        ],
        corporation: [
          { required: true, message: '公司法人不能为空', trigger: 'blur' },
          { max: 64, message: '公司法人不能超过64字符', trigger: 'blur' }
        ],
        foundingTime: [
          { required: true, message: '成立日期不能为空', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async open() {
      this.dialogVisible = true
      await this.loadMenuGroups()
      await this.loadFreeCommunitys()
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.form = {
        name: '',
        address: '',
        tel: '',
        corporation: '',
        foundingTime: '',
        communityIds: [],
        groupIds: [],
        isAll: true
      }
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
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        await saveProperty(this.form)
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
