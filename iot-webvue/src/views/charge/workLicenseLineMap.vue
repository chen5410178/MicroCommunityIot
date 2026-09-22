<template>
  <div class="work-license-line-map">
    <el-card shadow="never" class="search-card">
      <el-row :gutter="16" type="flex" align="middle">
        <el-col :span="6">
          <el-form-item label="日期">
            <el-date-picker
              v-model="uploadDate"
              type="date"
              placeholder="选择日期"
              value-format="yyyy-MM-dd"
              @change="handleDateChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <span class="staff-label">员工：{{ staffName }}</span>
        </el-col>
        <el-col :span="4">
          <el-button @click="goBack">返回</el-button>
        </el-col>
      </el-row>
    </el-card>
    <el-card shadow="never" class="map-card">
      <div id="wlLineMap" class="map-container"></div>
    </el-card>
  </div>
</template>

<script>
import { listWorkLicenseMachine, listWorkLicensePos } from '@/api/charge/chargeApi'
import { wgs84togcj02 } from '@/utils/mapUtil'
import { loadScriptOnce, TENCENT_MAP_URL } from '@/utils/asyncScript'

export default {
  name: 'WorkLicenseLineMap',
  data() {
    return {
      machineId: '',
      uploadDate: '',
      staffName: '',
      pos: [],
      mapWrapper: null
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    this.uploadDate = this.$route.query.uploadDate || this.formatDate(new Date())
    this.preloadMapScript()
    this.fetchStaffName()
    this.fetchPos()
  },
  methods: {
    preloadMapScript() {
      loadScriptOnce(TENCENT_MAP_URL).catch(() => {})
    },
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },
    goBack() {
      this.$router.go(-1)
    },
    handleDateChange() {
      if (this.mapWrapper && this.mapWrapper.map) {
        this.mapWrapper.map.destroy()
      }
      this.mapWrapper = null
      this.fetchPos()
    },
    fetchStaffName() {
      if (!this.machineId) return
      listWorkLicenseMachine({ page: 1, row: 1, machineId: this.machineId }).then(res => {
        const data = res.data || []
        if (data.length > 0) {
          this.staffName = data[0].staffName || ''
        }
      })
    },
    fetchPos() {
      if (!this.machineId) return
      listWorkLicensePos({
        page: 1,
        row: 1000,
        machineId: this.machineId,
        uploadDate: this.uploadDate
      }).then(res => {
        this.pos = (res.data || []).filter(m => m.lon !== 0 && m.lat !== 0)
        this.$nextTick(() => {
          this.initMap()
        })
      })
    },
    async initMap() {
      if (!this.pos || this.pos.length === 0) {
        return
      }
      try {
        await loadScriptOnce(TENCENT_MAP_URL)
      } catch (e) {
        return
      }
      if (typeof TMap === 'undefined') {
        return
      }
      const posData = this.pos.map(m => ({ ...m }))
      posData.forEach(m => {
        const tmpLon = m.lon
        m.lon = m.lat
        m.lat = tmpLon
      })
      posData.forEach(m => {
        const latLon = wgs84togcj02(m.lon, m.lat)
        m.lat = latLon.lat
        m.lon = latLon.lon
      })

      let centerLat = 39.984120
      let centerLng = 116.307484
      if (posData.length > 0) {
        centerLat = posData[0].lat
        centerLng = posData[0].lon
      }
      const center = new TMap.LatLng(centerLat, centerLng)
      const map = new TMap.Map(document.getElementById('wlLineMap'), {
        center: center,
        zoom: 18,
        baseMap: {
          type: 'vector',
          features: ['base', 'building2d', 'point']
        }
      })
      this.mapWrapper = { map }
      this.addPointMachine(map, posData)
    },
    addPointMachine(map, posData) {
      const geometriesMarker = []
      const geometriesLabel = []
      const path = []
      posData.forEach(m => {
        try {
          const position = new TMap.LatLng(m.lat, m.lon)
          geometriesMarker.push({
            id: m.wltId,
            styleId: 'myStyle',
            position: position
          })
          geometriesLabel.push({
            id: 'label_' + m.wltId,
            styleId: 'label',
            position: position,
            content: this.timeMinFormat(m.createTime)
          })
          path.push(position)
        } catch (e) {
          // ignore invalid data
        }
      })
      new TMap.MultiMarker({
        map: map,
        styles: {
          myStyle: new TMap.MarkerStyle({
            width: 25,
            height: 35,
            src: '/img/maper.png',
            anchor: { x: 32, y: 32 }
          })
        },
        geometries: geometriesMarker
      })
      new TMap.MultiLabel({
        id: 'label-layer',
        map: map,
        styles: {
          label: new TMap.LabelStyle({
            color: '#3777FF',
            size: 20,
            offset: { x: 0, y: 15 },
            angle: 0,
            alignment: 'center',
            verticalAlignment: 'middle'
          })
        },
        geometries: geometriesLabel
      })
      new TMap.MultiPolyline({
        id: 'polyline-layer',
        map: map,
        styles: {
          style_blue: new TMap.PolylineStyle({
            color: '#3777FF',
            width: 6,
            borderWidth: 5,
            borderColor: '#FFF',
            lineCap: 'butt'
          })
        },
        geometries: [{
          id: 'pl_1',
          styleId: 'style_blue',
          paths: path
        }]
      })
    },
    timeMinFormat(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      if (isNaN(date.getTime())) return timeStr
      const h = String(date.getHours()).padStart(2, '0')
      const m = String(date.getMinutes()).padStart(2, '0')
      return `${h}:${m}`
    }
  }
}
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 600px;
}
.map-card {
  margin-top: 16px;
}
.staff-label {
  line-height: 36px;
  font-size: 16px;
}
</style>
