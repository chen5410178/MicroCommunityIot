<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改停车场"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="停车场编号" prop="num">
        <el-input v-model="form.num" placeholder="必填，请填写停车场编号" maxlength="12" />
      </el-form-item>
      <el-form-item label="类型" prop="typeCd">
        <el-select v-model="form.typeCd" placeholder="必填，请选择类型" style="width: 100%">
          <el-option label="地上停车场" value="1001" />
          <el-option label="地下停车场" value="2002" />
        </el-select>
      </el-form-item>
      <el-form-item label="三方ID" prop="extPaId">
        <el-input v-model="form.extPaId" placeholder="选填，请填写三方停车场ID" maxlength="64" />
      </el-form-item>
      <el-form-item label="默认省份" prop="defaultProv">
        <el-select v-model="form.defaultProv" placeholder="默认省份（车牌第1位）" style="width: 100%">
          <el-option v-for="prov in provList" :key="prov" :label="prov" :value="prov" />
        </el-select>
      </el-form-item>
      <el-form-item label="默认字母" prop="defaultChar">
        <el-select v-model="form.defaultChar" placeholder="默认字母（车牌第2位）" style="width: 100%">
          <el-option v-for="c in charList" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="300" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateParkingArea } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditParkingArea',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        paId: '',
        num: '',
        typeCd: '',
        extPaId: '',
        defaultProv: '粤',
        defaultChar: 'A',
        remark: ''
      },
      provList: ['京', '津', '沪', '渝', '冀', '豫', '云', '辽', '黑', '湘', '皖', '鲁', '新', '苏', '浙', '赣', '鄂', '桂', '甘', '晋', '蒙', '陕', '吉', '闽', '贵', '粤', '青', '藏', '川', '宁', '琼', '澳', '台'],
      charList: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
      rules: {
        num: [
          { required: true, message: '停车场编号不能为空', trigger: 'blur' },
          { max: 12, message: '停车场编号不能超过12', trigger: 'blur' }
        ],
        typeCd: [
          { required: true, message: '类型不能为空', trigger: 'change' }
        ],
        extPaId: [
          { max: 64, message: '三方ID不能超过64', trigger: 'blur' }
        ],
        remark: [
          { max: 300, message: '备注不能超过300', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
        this.form.defaultProv = this.form.defaultProv || '粤'
        this.form.defaultChar = this.form.defaultChar || 'A'
      }
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
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateParkingArea(this.form)
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
