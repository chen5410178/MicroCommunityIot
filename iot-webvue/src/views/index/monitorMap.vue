<template>
  <div class="monitor-map">
    <div class="map-left" :class="{ collapsed: leftCollapsed }">
      <div class="panel-header">
        <span v-show="!leftCollapsed">人员搜索</span>
        <el-button type="text" class="toggle-btn" @click="leftCollapsed = !leftCollapsed">
          <i :class="leftCollapsed ? 'el-icon-arrow-right' : 'el-icon-arrow-left'" />
        </el-button>
      </div>
      <div v-show="!leftCollapsed" class="panel-body">
        <div class="panel-item">
          <div class="item-label">日期</div>
          <el-date-picker
            v-model="queryDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            class="w-full"
          />
        </div>
        <div class="panel-item">
          <div class="item-label">人员</div>
          <el-input v-model="personName" placeholder="人员名称" clearable class="w-full" @input="fetchPersonFaces" />
        </div>
        <div class="person-list">
          <div
            v-for="item in personFaces"
            :key="item.personId"
            class="person-item"
            :class="{ active: personId === item.personId }"
            @click="clickPerson(item)"
          >
            {{ item.personName }}({{ item.personLink }})
          </div>
        </div>
      </div>
    </div>

    <div class="map-center">
      <div id="monitorMap" class="map-box"></div>
    </div>

    <div class="map-right" v-if="dispatchLogs.length > 0">
      <div class="panel-header">布控告警</div>
      <div class="panel-body">
        <div v-for="item in dispatchLogs" :key="item.logId" class="alert-card">
          <div class="alert-img" @click="clickAlertImage(item.faceUrl)">
            <img :src="item.faceUrl" />
          </div>
          <div class="alert-info">
            <div class="alert-title">{{ item.personName }}【{{ item.score }}】</div>
            <div>{{ item.createTime }}</div>
            <div>{{ item.remark }}</div>
          </div>
        </div>
      </div>
    </div>

    <PlayMonitorVideo :visible.sync="videoVisible" :video-info="currentMachine" />
    <ViewImage :visible.sync="imageVisible" :image-url="currentImageUrl" />
  </div>
</template>

<script>
import PlayMonitorVideo from '@/components/monitor/PlayMonitorVideo.vue'
import ViewImage from '@/components/common/viewImage.vue'
import { listDispatchFaces } from '@/api/ai/dispatchFaceApi'
import { getDispatchLog } from '@/api/ai/dispatchApi'
import { getFaceLog, queryMonitorCamera } from '@/api/ai/faceLogApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MonitorMap',
  components: { PlayMonitorVideo, ViewImage },
  data() {
    return {
      queryDate: '',
      personName: '',
      personId: '',
      personFaces: [],
      machines: [],
      faceLogs: [],
      dispatchLogs: [],
      map: null,
      videoVisible: false,
      currentMachine: {},
      imageVisible: false,
      currentImageUrl: '',
      leftCollapsed: false
    }
  },
  computed: {
    communityId() {
      return (getCurrentCommunity() || {}).communityId || ''
    }
  },
  mounted() {
    this.queryDate = this.formatDate(new Date())
    this.fetchPersonFaces()
    this.fetchMachines()
    this.fetchDispatchLogs()
  },
  watch: {
    queryDate() {
      if (this.map) {
        this.map.destroy()
        this.map = null
      }
      this.initMap()
      this.addPointMachine()
      if (this.personId) {
        this.fetchFaceLogs()
      }
    }
  },
  methods: {
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },
    timeMinFormat(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      if (isNaN(date.getTime())) return timeStr
      const h = String(date.getHours()).padStart(2, '0')
      const m = String(date.getMinutes()).padStart(2, '0')
      return `${h}:${m}`
    },
    fetchPersonFaces() {
      listDispatchFaces({
        page: 1,
        row: 10,
        communityId: this.communityId,
        personName: this.personName,
        personType: 'normal'
      }).then(res => {
        this.personFaces = res.data || []
      })
    },
    fetchMachines() {
      queryMonitorCamera({
        page: 1,
        row: 50,
        communityId: this.communityId
      }).then(res => {
        this.machines = res.data || []
        this.initMap()
        this.addPointMachine()
      })
    },
    fetchDispatchLogs() {
      getDispatchLog({
        page: 1,
        row: 100,
        communityId: this.communityId
      }).then(res => {
        this.dispatchLogs = res.data || []
      })
    },
    fetchFaceLogs() {
      getFaceLog({
        personId: this.personId,
        communityId: this.communityId,
        page: 1,
        row: 100,
        queryDate: this.queryDate
      }).then(res => {
        this.faceLogs = res.data || []
        this.initMap()
        this.addPointMachine()
        this.addPointPerson()
      })
    },
    clickPerson(item) {
      this.personId = item.personId
      this.fetchFaceLogs()
    },
    clickAlertImage(url) {
      this.currentImageUrl = url
      this.imageVisible = true
    },
    initMap() {
      if (typeof TMap === 'undefined') {
        return
      }
      let lat = 39.984120
      let lng = 116.307484
      if (this.machines && this.machines.length > 0) {
        lat = this.machines[0].lat
        lng = this.machines[0].lng
      }
      const center = new TMap.LatLng(lat, lng)
      const map = new TMap.Map(document.getElementById('monitorMap'), {
        center: center,
        zoom: 18,
        baseMap: {
          type: 'vector',
          features: ['base', 'building2d']
        }
      })
      this.map = map
      if (this.machines && this.machines.length > 0) {
        map.panTo(new TMap.LatLng(this.machines[0].lat, this.machines[0].lng))
      }
    },
    addPointMachine() {
      if (!this.map || !this.machines || this.machines.length === 0) {
        return
      }
      const geometriesMarker = []
      const geometriesLabel = []
      this.machines.forEach(m => {
        const position = new TMap.LatLng(m.lat, m.lng)
        geometriesMarker.push({
          id: m.machineId,
          styleId: 'cameraMarker',
          position: position
        })
        geometriesLabel.push({
          id: 'label_' + m.machineId,
          styleId: 'cameraLabel',
          position: position,
          content: m.machineName || ''
        })
      })
      const markerLayer = new TMap.MultiMarker({
        map: this.map,
        styles: {
          cameraMarker: new TMap.MarkerStyle({
            width: 35,
            height: 38,
            src: '/img/camera.png',
            cursor: 'pointer',
            anchor: { x: 32, y: 32 }
          })
        },
        geometries: geometriesMarker
      })
      markerLayer.on('click', (e) => {
        const machineId = e.geometry.id
        const machine = this.machines.find(m => m.machineId === machineId)
        if (machine) {
          this.currentMachine = { ...machine, communityId: this.communityId }
          this.videoVisible = true
        }
      })
      new TMap.MultiLabel({
        id: 'camera-label-layer',
        map: this.map,
        styles: {
          cameraLabel: new TMap.LabelStyle({
            color: '#3777FF',
            size: 15,
            offset: { x: 0, y: 15 },
            angle: 0,
            alignment: 'center',
            verticalAlignment: 'middle'
          })
        },
        geometries: geometriesLabel
      })
    },
    addPointPerson() {
      if (!this.map || !this.faceLogs || this.faceLogs.length === 0) {
        return
      }
      const geometriesMarker = []
      const geometriesLabel = []
      const path = []
      this.faceLogs.forEach(m => {
        try {
          const position = new TMap.LatLng(m.lat, m.lon)
          geometriesMarker.push({
            id: m.logId,
            styleId: 'personMarker',
            position: position
          })
          geometriesLabel.push({
            id: 'label_' + m.logId,
            styleId: 'personLabel',
            position: position,
            content: this.timeMinFormat(m.createTime)
          })
          path.push(position)
        } catch (e) {
          // ignore invalid data
        }
      })
      new TMap.MultiMarker({
        map: this.map,
        styles: {
          personMarker: new TMap.MarkerStyle({
            width: 25,
            height: 35,
            src: '/img/person.png',
            anchor: { x: 32, y: 32 }
          })
        },
        geometries: geometriesMarker
      })
      new TMap.MultiLabel({
        id: 'person-label-layer',
        map: this.map,
        styles: {
          personLabel: new TMap.LabelStyle({
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
        map: this.map,
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
    }
  }
}
</script>

<style scoped>
.monitor-map {
  display: flex;
  flex-direction: row;
  height: calc(100vh - 120px);
  min-height: 600px;
}
.map-left {
  width: 260px;
  min-width: 40px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
}
.map-left.collapsed {
  width: 40px;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 10px;
  font-weight: bold;
  font-size: 14px;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
}
.panel-body {
  padding: 12px;
  overflow-y: auto;
  flex: 1;
}
.panel-item {
  margin-bottom: 12px;
}
.item-label {
  font-size: 13px;
  margin-bottom: 4px;
  color: #606266;
}
.w-full {
  width: 100%;
}
.person-list {
  margin-top: 8px;
}
.person-item {
  padding: 6px 8px;
  cursor: pointer;
  font-size: 13px;
  border-radius: 4px;
  margin-bottom: 4px;
}
.person-item:hover {
  background: #ecf5ff;
}
.person-item.active {
  background: #409eff;
  color: #fff;
}
.map-center {
  flex: 1;
  position: relative;
}
.map-box {
  width: 100%;
  height: 100%;
}
.map-right {
  width: 300px;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}
.alert-card {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}
.alert-img {
  width: 60px;
  height: 60px;
  cursor: pointer;
  flex-shrink: 0;
  margin-right: 8px;
}
.alert-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}
.alert-info {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}
.alert-title {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}
.toggle-btn {
  padding: 0;
  font-size: 18px;
}
</style>
