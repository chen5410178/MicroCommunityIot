<template>
  <div class="work-license-map">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input v-model="searchForm.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchForm.machineCode" placeholder="请输入设备编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchForm.staffName" placeholder="请输入员工名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="map-card">
      <div id="wlMap" class="map-container"></div>
    </el-card>
  </div>
</template>

<script>
import { listWorkLicenseMachine } from '@/api/charge/chargeApi'
import { wgs84togcj02 } from '@/utils/mapUtil'
import { loadScriptOnce, TENCENT_MAP_URL } from '@/utils/asyncScript'

export default {
  name: 'WorkLicenseMap',
  data() {
    return {
      searchForm: {
        machineName: '',
        machineCode: '',
        staffName: ''
      },
      machines: [],
      map: null
    }
  },
  mounted() {
    this.preloadMapScript()
    this.fetchMachines()
  },
  methods: {
    preloadMapScript() {
      loadScriptOnce(TENCENT_MAP_URL).catch(() => {})
    },
    fetchMachines() {
      const params = {
        page: 1,
        row: 30,
        ...this.searchForm
      }
      listWorkLicenseMachine(params).then(res => {
        this.machines = res.data || []
        this.$nextTick(() => {
          this.initMap()
        })
      })
    },
    handleSearch() {
      if (this.map) {
        this.map.destroy()
        this.map = null
      }
      this.fetchMachines()
    },
    handleReset() {
      this.searchForm = {
        machineName: '',
        machineCode: '',
        staffName: ''
      }
      this.handleSearch()
    },
    async initMap() {
      if (!this.machines || this.machines.length === 0) {
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
      const machines = this.machines.map(m => ({ ...m }))
      machines.forEach(m => {
        if (m.lat === 0 || m.lon === 0) {
          return
        }
        const latLon = wgs84togcj02(m.lat, m.lon)
        m.lon = latLon.lat
        m.lat = latLon.lon
      })
      const validMachines = machines.filter(m => m.lat !== 0 && m.lon !== 0)
      let centerLat = 39.984120
      let centerLng = 116.307484
      if (validMachines.length > 0) {
        centerLat = validMachines[0].lat
        centerLng = validMachines[0].lon
      }
      const center = new TMap.LatLng(centerLat, centerLng)
      const map = new TMap.Map(document.getElementById('wlMap'), {
        center: center,
        zoom: 18,
        baseMap: {
          type: 'vector',
          features: ['base', 'building2d', 'point']
        }
      })
      this.map = map
      this.addPointMachine(map, validMachines)
    },
    addPointMachine(map, machines) {
      const geometriesMarker = []
      const geometriesLabel = []
      machines.forEach(m => {
        const position = new TMap.LatLng(m.lon, m.lat)
        geometriesMarker.push({
          id: m.machineId,
          styleId: 'myStyle',
          position: position
        })
        geometriesLabel.push({
          id: 'label_' + m.machineId,
          styleId: 'label',
          position: position,
          content: m.staffName || ''
        })
      })
      const markerLayer = new TMap.MultiMarker({
        map: map,
        styles: {
          myStyle: new TMap.MarkerStyle({
            width: 25,
            height: 35,
            src: '/img/maper.png',
            cursor: 'pointer',
            anchor: { x: 32, y: 32 }
          })
        },
        geometries: geometriesMarker
      })
      markerLayer.on('click', (e) => {
        const curDate = this.formatDate(new Date())
        this.$router.push({
          path: '/pages/charge/workLicenseLineMap',
          query: { machineId: e.geometry.id, uploadDate: curDate }
        })
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
    },
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
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
</style>
