<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加小区"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="小区名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，1-20字符" maxlength="20" />
      </el-form-item>
      <el-form-item label="小区地区" prop="cityCode">
        <AreaSelect ref="areaSelectRef" v-model="form.cityCode" @change="handleAreaChange" />
      </el-form-item>
      <el-form-item label="小区地址" prop="tmpAddress">
        <el-input v-model="form.tmpAddress" placeholder="必填，最大200字符" maxlength="200" />
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
import { saveCommunity, listCommunitys } from '@/api/community/communityApi'
import { listAttrSpecs, listAttrValues } from '@/api/dev/devApi'
import AreaSelect from './AreaSelect.vue'

export default {
  name: 'AddCommunity',
  components: { AreaSelect },
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      areaAddress: '',
      attrSpecs: [],
      attrValueMap: {},
      form: {
        name: '',
        cityCode: '',
        tmpAddress: '',
        tel: '',
        extCommunityId: '',
        attrs: {}
      },
      rules: {
        name: [
          { required: true, message: '小区名称不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '1-20字符', trigger: 'blur' }
        ],
        cityCode: [
          { required: true, message: '小区地区不能为空', trigger: 'change' }
        ],
        tmpAddress: [
          { required: true, message: '小区地址不能为空', trigger: 'blur' },
          { max: 200, message: '不超过200字符', trigger: 'blur' }
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
      if (val) {
        this.loadAttrSpecs()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleAreaChange(data) {
      if (data && data.areaName) {
        this.areaAddress = data.provName + data.cityName + data.areaName
        this.form.cityCode = data.areaCode
      } else {
        this.areaAddress = ''
        this.form.cityCode = ''
      }
    },
    async loadAttrSpecs() {
      try {
        const res = await listAttrSpecs({ tableName: 'building_community_attr', page: 1, row: 100 })
        const specs = (res.data || []).filter(s => s.specShow === 'Y')
        this.attrSpecs = specs
        for (const spec of specs) {
          if (spec.specType === '3344') {
            await this.loadAttrValues(spec.specCd)
          }
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
      this.areaAddress = ''
      this.attrSpecs = []
      this.attrValueMap = {}
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }

      if (!this.form.cityCode) {
        this.$message.warning('请选择小区地区')
        return
      }

      this.loading = true
      try {
        const address = this.areaAddress + this.form.tmpAddress
        const data = {
          name: this.form.name,
          address: address,
          cityCode: this.form.cityCode,
          tel: this.form.tel,
          extCommunityId: this.form.extCommunityId,
          attrs: Object.keys(this.form.attrs).map(k => ({ specCd: k, value: this.form.attrs[k] })),
          nearbyLandmarks: '无',
          mapX: '',
          mapY: '',
          lat: '',
          lng: ''
        }
        await saveCommunity(data)
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
