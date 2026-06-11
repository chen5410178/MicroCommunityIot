<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改小区"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="小区名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，1-20字符" maxlength="20" />
      </el-form-item>
      <el-form-item label="小区地址" prop="address">
        <el-input v-model="form.address" placeholder="必填" maxlength="200" />
      </el-form-item>
      <el-form-item label="客服电话" prop="tel">
        <el-input v-model="form.tel" placeholder="必填" maxlength="30" />
      </el-form-item>
      <el-form-item label="外部编码">
        <el-input v-model="form.extCommunityId" placeholder="选填" maxlength="64" />
      </el-form-item>
      <el-form-item v-for="spec in attrSpecs" :key="spec.specCd" :label="spec.specName">
        <el-input v-if="spec.specType === '2233'" v-model="form.attrs[spec.specCd]" :placeholder="'请输入' + spec.specName" maxlength="200" />
        <el-select v-else-if="spec.specType === '3344'" v-model="form.attrs[spec.specCd]" :placeholder="'请选择' + spec.specName" clearable style="width: 100%">
          <el-option
            v-for="opt in attrValueMap[spec.specCd]"
            :key="opt.value"
            :label="opt.name"
            :value="opt.value"
          />
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
import { updateCommunity } from '@/api/community/communityApi'
import { listAttrSpecs, listAttrValues } from '@/api/dev/devApi'

export default {
  name: 'EditCommunity',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      attrSpecs: [],
      attrValueMap: {},
      form: {
        communityId: '',
        name: '',
        address: '',
        tel: '',
        extCommunityId: '',
        attrs: {}
      },
      rules: {
        name: [
          { required: true, message: '小区名称不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '1-20字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '小区地址不能为空', trigger: 'blur' }
        ],
        tel: [
          { required: true, message: '客服电话不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.loadAttrSpecs()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadAttrSpecs() {
      try {
        const res = await listAttrSpecs({ tableName: 'building_community_attr', page: 1, row: 100 })
        const specs = (res.data || []).filter(s => s.specShow === 'Y')
        this.attrSpecs = specs

        const attrs = {}
        if (this.editData.communityAttrDtos) {
          for (const attr of this.editData.communityAttrDtos) {
            attrs[attr.specCd] = attr.value
          }
        }
        for (const spec of specs) {
          if (!(spec.specCd in attrs)) {
            attrs[spec.specCd] = ''
          }
          if (spec.specType === '3344') {
            await this.loadAttrValues(spec.specCd)
          }
        }

        this.form = {
          communityId: this.editData.communityId || '',
          name: this.editData.name || '',
          address: this.editData.address || '',
          tel: this.editData.tel || '',
          extCommunityId: this.editData.extCommunityId || '',
          attrs: attrs
        }
      } catch { /* ignore */ }
    },
    async loadAttrValues(specCd) {
      try {
        const res = await listAttrValues({ specCd, page: 1, row: 1000 })
        this.$set(this.attrValueMap, specCd, res.data || [])
      } catch { /* ignore */ }
    },
    handleClose() {
      this.dialogVisible = false
      this.attrSpecs = []
      this.attrValueMap = {}
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const data = {
          communityId: this.form.communityId,
          name: this.form.name,
          address: this.form.address,
          tel: this.form.tel,
          extCommunityId: this.form.extCommunityId,
          attrs: Object.keys(this.form.attrs).map(k => ({ specCd: k, value: this.form.attrs[k] })),
          nearbyLandmarks: '无',
          mapX: '',
          mapY: '',
          lat: '',
          lng: ''
        }
        await updateCommunity(data)
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
