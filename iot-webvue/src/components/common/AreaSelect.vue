<template>
  <div class="area-select">
    <el-select
      v-model="provCode"
      placeholder="请选择省份"
      @change="handleProvChange"
      clearable
      style="width: 32%"
    >
      <el-option
        v-for="item in provList"
        :key="item.areaCode"
        :label="item.areaName"
        :value="item.areaCode"
      />
    </el-select>
    <el-select
      v-model="cityCode"
      placeholder="请选择城市"
      @change="handleCityChange"
      clearable
      :disabled="!provCode"
      style="width: 32%; margin-left: 2%"
    >
      <el-option
        v-for="item in cityList"
        :key="item.areaCode"
        :label="item.areaName"
        :value="item.areaCode"
      />
    </el-select>
    <el-select
      v-model="areaCode"
      placeholder="请选择区县"
      @change="handleAreaChange"
      clearable
      :disabled="!cityCode"
      style="width: 32%; margin-left: 2%"
    >
      <el-option
        v-for="item in areaList"
        :key="item.areaCode"
        :label="item.areaName"
        :value="item.areaCode"
      />
    </el-select>
  </div>
</template>

<script>
import { listAreas } from '@/api/common/cityAreaApi'

export default {
  name: 'AreaSelect',
  props: {
    value: { type: String, default: '' }
  },
  data() {
    return {
      provCode: '',
      cityCode: '',
      areaCode: '',
      provList: [],
      cityList: [],
      areaList: []
    }
  },
  watch: {
    value(val) {
      if (!val) {
        this.resetAll()
      }
    }
  },
  mounted() {
    this.loadProvs()
  },
  methods: {
    async loadProvs() {
      try {
        const res = await listAreas({ areaLevel: '101', parentAreaCode: '0', page: 1, row: 500 })
        this.provList = res.data || []
      } catch { /* ignore */ }
    },
    async handleProvChange(code) {
      this.cityCode = ''
      this.areaCode = ''
      this.cityList = []
      this.areaList = []
      if (!code) {
        this.$emit('input', '')
        this.$emit('change', null)
        return
      }
      try {
        const res = await listAreas({ areaLevel: '202', parentAreaCode: code, page: 1, row: 500 })
        this.cityList = res.data || []
      } catch { /* ignore */ }
    },
    async handleCityChange(code) {
      this.areaCode = ''
      this.areaList = []
      if (!code) {
        const prov = this.provList.find(p => p.areaCode === this.provCode)
        this.$emit('input', '')
        this.$emit('change', { provCode: this.provCode, cityCode: '', areaCode: '', provName: prov ? prov.areaName : '', cityName: '', areaName: '' })
        return
      }
      try {
        const res = await listAreas({ areaLevel: '303', parentAreaCode: code, page: 1, row: 500 })
        this.areaList = res.data || []
      } catch { /* ignore */ }
    },
    handleAreaChange(code) {
      const prov = this.provList.find(p => p.areaCode === this.provCode)
      const city = this.cityList.find(c => c.areaCode === this.cityCode)
      const area = this.areaList.find(a => a.areaCode === code)
      this.$emit('input', code || '')
      this.$emit('change', {
        provCode: this.provCode,
        cityCode: this.cityCode,
        areaCode: code || '',
        provName: prov ? prov.areaName : '',
        cityName: city ? city.areaName : '',
        areaName: area ? area.areaName : ''
      })
    },
    resetAll() {
      this.provCode = ''
      this.cityCode = ''
      this.areaCode = ''
      this.cityList = []
      this.areaList = []
    },
    setValue(areaCode) {
      this.areaCode = areaCode
    }
  }
}
</script>
