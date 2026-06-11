<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加标准"
    width="800px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="标准名称" prop="feeName">
            <el-input v-model="form.feeName" placeholder="必填，请填写标准名称" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="停车场" prop="paId">
            <el-select v-model="form.paId" placeholder="必填，请选择停车场" style="width: 100%">
              <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="车辆类型" prop="carType">
            <el-select v-model="form.carType" placeholder="必填，请选择车辆类型" style="width: 100%">
              <el-option v-for="item in carTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收费规则" prop="ruleId">
            <el-select v-model="form.ruleId" placeholder="必填，请选择收费规则" style="width: 100%" @change="handleRuleChange">
              <el-option v-for="item in rules" :key="item.ruleId" :label="item.ruleName" :value="item.ruleId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker v-model="form.startTime" type="date" placeholder="必填，请选择开始时间" value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker v-model="form.endTime" type="date" placeholder="必填，请选择结束时间" value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-for="(row, idx) in attrRows" :key="idx">
        <el-col :span="12" v-for="(item, colIdx) in row" :key="colIdx">
          <el-form-item :label="item.specName" :prop="'attr_' + (idx * 2 + colIdx)" :rules="[{ required: true, message: item.specName + '不能为空', trigger: 'blur' }]">
            <el-input v-model="item.value" :placeholder="'必填，请填写' + item.specName" />
            <span v-if="item.remark" style="color: #909399; font-size: 12px;">{{ item.remark }}</span>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveTempCarFeeConfig, queryTempCarFeeRules } from '@/api/car/carApi'
import { listParkingAreas } from '@/api/car/carApi'
import { listDicts } from '@/api/dev/devApi'

export default {
  name: 'AddTempCarFeeConfig',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      parkingAreas: [],
      carTypes: [],
      rules: [],
      form: {
        feeName: '',
        paId: '',
        carType: '',
        ruleId: '',
        startTime: '',
        endTime: '',
        attrs: []
      },
      rules: {
        feeName: [
          { required: true, message: '标准名称不能为空', trigger: 'blur' },
          { max: 64, message: '标准名称不能超过64个字符', trigger: 'blur' }
        ],
        paId: [{ required: true, message: '停车场不能为空', trigger: 'change' }],
        carType: [{ required: true, message: '车辆类型不能为空', trigger: 'change' }],
        ruleId: [{ required: true, message: '收费规则不能为空', trigger: 'change' }],
        startTime: [{ required: true, message: '开始时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, message: '结束时间不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    attrRows() {
      const rows = []
      for (let i = 0; i < this.form.attrs.length; i += 2) {
        rows.push(this.form.attrs.slice(i, i + 2))
      }
      return rows
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadInitData()
        this.form = {
          feeName: '',
          paId: '',
          carType: '',
          ruleId: '',
          startTime: '',
          endTime: '',
          attrs: []
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadInitData() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const [parkRes, ruleRes, dictRes] = await Promise.all([
          listParkingAreas({ page: 1, row: 50, communityId }),
          queryTempCarFeeRules({ page: 1, row: 100 }),
          listDicts({ page: 1, row: 100, tableName: 'temp_car_fee_config', columnName: 'car_type' })
        ])
        this.parkingAreas = parkRes.data || []
        this.rules = ruleRes.data || []
        this.carTypes = dictRes.data || []
      } catch (e) {
        console.error('加载初始化数据失败', e)
      }
    },
    handleRuleChange(ruleId) {
      const rule = this.rules.find(item => item.ruleId === ruleId)
      if (rule && rule.tempCarFeeRuleSpecs) {
        this.form.attrs = rule.tempCarFeeRuleSpecs.map(spec => ({
          ...spec,
          value: '',
          specCd: spec.specId
        }))
      } else {
        this.form.attrs = []
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    validateAttrs() {
      if (!this.form.attrs || this.form.attrs.length === 0) return true
      for (let i = 0; i < this.form.attrs.length; i++) {
        if (!this.form.attrs[i].value || this.form.attrs[i].value.trim() === '') {
          this.$message.warning(this.form.attrs[i].specName + '不能为空')
          return false
        }
      }
      return true
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      if (!this.validateAttrs()) return
      if (this.form.startTime && this.form.endTime) {
        if (this.form.startTime >= this.form.endTime) {
          this.$message.warning('开始时间必须小于结束时间')
          return
        }
      }
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveTempCarFeeConfig({ ...this.form, communityId })
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
