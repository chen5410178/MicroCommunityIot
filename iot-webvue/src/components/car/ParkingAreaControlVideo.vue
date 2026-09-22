<template>
  <div class="parking-area-control-video">
    <el-card shadow="never">
      <div v-loading="loading">
        <el-row :gutter="16">
          <el-col :span="12">
            <div class="video-section">
              <div class="control-row">
                <el-select v-model="inMachineId" placeholder="请选择入口摄像头" size="small" style="width: 200px" @change="onInMachineChange">
                  <el-option v-for="item in inMachines" :key="item.machineId" :label="item.machineName" :value="item.machineId" />
                </el-select>
                <div class="control-buttons">
                  <el-button size="small" @click="unlicensedCarIn">无牌车进场</el-button>
                  <el-button size="small" @click="customCarIn('1101')">手动进场</el-button>
                  <el-button size="small" type="primary" @click="openDoor('in')">开闸</el-button>
                  <el-button size="small" type="primary" @click="closeDoor('in')">关闸</el-button>
                </div>
              </div>
              <div class="video-container" id="receiver1Div" @click="openCloudFlvVideo('receiver1')">
                <img width="100%" src="/img/init.jpg" height="280" id="receiver1" style="object-fit: cover" />
                <div class="camera-icon"><i class="el-icon-video-camera"></i></div>
              </div>
              <div class="machine-info" v-if="inMachineCarNum">
                <i class="el-icon-bell" style="color: #007bff"></i>
                <span>{{ inMachineInOutTime }}</span>
                <span>{{ inMachineOpen }}</span>
                <span>{{ inMachineOpenMsg }}</span>
              </div>
              <div class="machine-info machine-info-empty" v-else>
                <i class="el-icon-bell" style="color: #007bff"></i>
                <span>没有进场车辆</span>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="video-section">
              <div class="control-row">
                <el-select v-model="outMachineId" placeholder="请选择出口摄像头" size="small" style="width: 200px" @change="onOutMachineChange">
                  <el-option v-for="item in outMachines" :key="item.machineId" :label="item.machineName" :value="item.machineId" />
                </el-select>
                <div class="control-buttons">
                  <el-button size="small" @click="outPayFeeQrCode">出口二维码</el-button>
                  <el-button size="small" @click="customCarIn('1102')">手动出场</el-button>
                  <el-button size="small" type="primary" @click="openDoor('out')">开闸</el-button>
                  <el-button size="small" type="primary" @click="closeDoor('out')">关闸</el-button>
                </div>
              </div>
              <div class="video-container" id="receiver2Div" @click="openCloudFlvVideo('receiver2')">
                <img width="100%" src="/img/init.jpg" height="280" id="receiver2" style="object-fit: cover" />
                <div class="camera-icon"><i class="el-icon-video-camera"></i></div>
              </div>
              <div class="machine-info" v-if="outMachineCarNum">
                <i class="el-icon-bell" style="color: #007bff"></i>
                <span>{{ outMachineInOutTime }}</span>
                <span>{{ outMachineOpen }}</span>
                <span>{{ outMachineOpenMsg }}</span>
              </div>
              <div class="machine-info machine-info-empty" v-else>
                <i class="el-icon-bell" style="color: #007bff"></i>
                <span>没有出场车辆</span>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="capture-images" v-if="inImg !== '/img/carInout.jpg' || outImg !== '/img/carInout.jpg'">
          <el-row :gutter="16">
            <el-col :span="12">
              <img :src="inImg" width="100%" height="350" style="object-fit: cover" @error="onImgError" />
            </el-col>
            <el-col :span="12">
              <img :src="outImg" width="100%" height="350" style="object-fit: cover" @error="onImgError" />
            </el-col>
          </el-row>
        </div>

      </div>
    </el-card>
  </div>
</template>

<script>
import { listBarriers, openBarrierDoor, closeBarrierDoor, getCloudFlvVideo } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'
import { loadScriptOnce, JESSIBUCA_URL } from '@/utils/asyncScript'

export default {
  name: 'ParkingAreaControlVideo',
  props: {
    boxId: { type: String, default: '' },
    paId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      inMachines: [],
      outMachines: [],
      inMachineId: '',
      outMachineId: '',
      inMachineCarNum: '',
      inMachineInOutTime: '',
      inMachineOpen: '',
      inMachineOpenMsg: '',
      outMachineCarNum: '',
      outMachineInOutTime: '',
      outMachineOpen: '',
      outMachineOpenMsg: '',
      inImg: '/img/carInout.jpg',
      outImg: '/img/carInout.jpg',
      jessibucaMap: {},
      huaXiaMap: {}
    }
  },
  watch: {
    boxId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.listMachines()
        }
      }
    }
  },
  beforeDestroy() {
    this.destroyAllVideo()
  },
  methods: {
    async listMachines() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listBarriers({ boxId: this.boxId, page: 1, row: 100, communityId })
        const data = res.data || []
        this.inMachines = data.filter(item => item.direction === '3306')
        this.outMachines = data.filter(item => item.direction === '3307')
        if (this.inMachines.length > 0) {
          this.inMachineId = this.inMachines[0].machineId
          this.$nextTick(() => { this.swatchInVideo() })
        }
        if (this.outMachines.length > 0) {
          this.outMachineId = this.outMachines[0].machineId
          this.$nextTick(() => { this.swatchOutVideo() })
        }
      } catch (e) {
        console.error('获取道闸设备失败', e)
      } finally {
        this.loading = false
      }
    },
    onInMachineChange() {
      this.swatchInVideo()
      this.inMachineCarNum = ''
      this.inMachineInOutTime = ''
      this.inMachineOpen = ''
      this.inMachineOpenMsg = ''
      this.$emit('changeMachine', { inMachineId: this.inMachineId })
    },
    onOutMachineChange() {
      this.swatchOutVideo()
      this.outMachineCarNum = ''
      this.outMachineInOutTime = ''
      this.outMachineOpen = ''
      this.outMachineOpenMsg = ''
      this.$emit('changeMachine', { outMachineId: this.outMachineId, boxId: this.boxId })
    },
    swatchInVideo() {
      let wsUrl = ''
      let hmId = ''
      this.inMachines.forEach(item => {
        if (item.machineId === this.inMachineId) {
          wsUrl = item.machineIp
          hmId = item.hmId
        }
      })
      if (!wsUrl) return
      if (hmId === '24' || hmId === 24) {
        wsUrl = 'ws://' + wsUrl + ':9999'
        this.showHuaXiaVideo(wsUrl, 'receiver1Div', 'receiver1')
        return
      }
      wsUrl = wsUrl.replace(':8131', ':9080')
      if (wsUrl.indexOf(':') < 0) {
        wsUrl = wsUrl + ':9080'
      }
      wsUrl += '/ws.flv'
      wsUrl = 'ws://' + wsUrl
      this.showCameraVideo(wsUrl, 'receiver1Div')
    },
    swatchOutVideo() {
      let wsUrl = ''
      let hmId = ''
      this.outMachines.forEach(item => {
        if (item.machineId === this.outMachineId) {
          wsUrl = item.machineIp
          hmId = item.hmId
        }
      })
      if (!wsUrl) return
      if (hmId === '24' || hmId === 24) {
        wsUrl = 'ws://' + wsUrl + ':9999'
        this.showHuaXiaVideo(wsUrl, 'receiver2Div', 'receiver2')
        return
      }
      wsUrl = wsUrl.replace(':8131', ':9080')
      if (wsUrl.indexOf(':') < 0) {
        wsUrl = wsUrl + ':9080'
      }
      wsUrl += '/ws.flv'
      wsUrl = 'ws://' + wsUrl
      this.showCameraVideo(wsUrl, 'receiver2Div')
    },
    async showCameraVideo(wsUrl, divId) {
      const container = document.getElementById(divId)
      if (!container) return
      if (this.jessibucaMap[divId]) {
        try { this.jessibucaMap[divId].destroy() } catch (e) { /* ignore */ }
      }
      try {
        await loadScriptOnce(JESSIBUCA_URL)
      } catch (e) {
        console.warn('Jessibuca 加载失败')
        return
      }
      if (typeof Jessibuca === 'undefined') {
        console.warn('Jessibuca 未加载')
        return
      }
      try {
        const jessibuca = new Jessibuca({
          container: container,
          videoBuffer: 0.2,
          isResize: false,
          text: '',
          loadingText: '',
          useMSE: false,
          debug: false,
          isNotMute: false
        })
        jessibuca.play(wsUrl)
        this.jessibucaMap[divId] = jessibuca
      } catch (e) {
        console.error('Jessibuca 初始化失败', e)
      }
    },
    showHuaXiaVideo(wsUrl, divId, imgId) {
      if (this.huaXiaMap[divId]) {
        try { this.huaXiaMap[divId].destroy() } catch (e) { /* ignore */ }
      }
      try {
        const huaXiaVideo = new HuaXiaVideoWrapper(wsUrl, imgId)
        huaXiaVideo.playVideo()
        this.huaXiaMap[divId] = huaXiaVideo
      } catch (e) {
        console.error('华夏视频初始化失败', e)
        this.showHuaXiaVideoFallback(wsUrl, divId, imgId)
      }
    },
    showHuaXiaVideoFallback(wsUrl, divId, imgId) {
      const imgEl = document.getElementById(imgId)
      if (!imgEl) return
      let ws = new WebSocket(wsUrl)
      ws.onopen = function () {}
      ws.onmessage = function (event) {
        if (typeof event.data === 'string') return
        const reader = new FileReader()
        reader.readAsDataURL(event.data)
        reader.onload = function (eve) {
          imgEl.src = eve.target.result
        }
      }
      ws.onclose = function () {}
      ws.onerror = function () {}
      this.huaXiaMap[divId] = { ws, destroy: () => { try { ws.close() } catch (e) { /* */ } } }
    },
    openDoor(inOut) {
      let machines = inOut === 'in' ? this.inMachines : this.outMachines
      let machineId = inOut === 'in' ? this.inMachineId : this.outMachineId
      if (machines.length === 0) {
        this.$message.warning('请先选择设备')
        return
      }
      let machineCode = ''
      machines.forEach(item => {
        if (item.machineId === machineId) {
          machineCode = item.machineCode
        }
      })
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      openBarrierDoor({ machineCode, stateName: '开门', state: '1500', url: '/barrier.openBarrierDoor', userRole: 'staff', communityId }).then(res => {
        if (res.code === 0) {
          this.$message.success('已请求设备')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      }).catch(() => { this.$message.error('操作失败') })
    },
    closeDoor(inOut) {
      let machines = inOut === 'in' ? this.inMachines : this.outMachines
      let machineId = inOut === 'in' ? this.inMachineId : this.outMachineId
      if (machines.length === 0) {
        this.$message.warning('请先选择设备')
        return
      }
      let machineCode = ''
      machines.forEach(item => {
        if (item.machineId === machineId) {
          machineCode = item.machineCode
        }
      })
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      closeBarrierDoor({ machineCode, stateName: '关门', state: '1500', url: '/barrier.closeBarrierDoor', userRole: 'staff', communityId }).then(res => {
        if (res.code === 0) {
          this.$message.success('已请求设备')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      }).catch(() => { this.$message.error('操作失败') })
    },
    customCarIn(type) {
      let machineId = type === '1101' ? this.inMachineId : this.outMachineId
      if (!machineId) {
        this.$message.warning('请选择摄像头')
        return
      }
      this.$emit('show-custom-inout', { type, machineId, boxId: this.boxId, paId: this.paId })
    },
    unlicensedCarIn() {
      if (!this.inMachineId) {
        this.$message.warning('请选择入场摄像头')
        return
      }
      this.$emit('unlicensed-car-in', { type: '1101', machineId: this.inMachineId })
    },
    outPayFeeQrCode() {
      if (!this.outMachineId) {
        this.$message.warning('请选择出场摄像头')
        return
      }
      this.$emit('show-qrcode', { machineId: this.outMachineId, boxId: this.boxId, title: '出口收费二维码' })
    },
    async openCloudFlvVideo(inOut) {
      let machineId = inOut === 'receiver1' ? this.inMachineId : this.outMachineId
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await getCloudFlvVideo({ machineId, communityId })
        if (res.code === 0 && res.data && res.data.url) {
          this.showCameraVideo(res.data.url, inOut + 'Div')
        } else {
          this.$message.error(res.msg || '获取视频失败')
        }
      } catch (e) {
        this.$message.error('获取视频失败')
      }
    },
    onWsMessage(data) {
      if (!data) return
      if (data.action === 'FEE_INFO') {
        if (this.outMachineId === data.machineId) {
          this.outMachineCarNum = data.carNum
          this.outMachineInOutTime = data.inOutTime
          this.outMachineOpen = data.open
          this.outMachineOpenMsg = data.remark || ''
          this.$emit('ws-data', { _direction: 'out', _data: data })
        } else {
          this.inMachineCarNum = data.carNum
          this.inMachineInOutTime = data.inOutTime
          this.inMachineOpen = data.open
          this.inMachineOpenMsg = data.remark || ''
          this.$emit('ws-data', { _direction: 'in', _data: data })
        }
      } else if (data.action === 'IN_OUT') {
        setTimeout(() => {
          if (this.inMachineId === data.machineId) {
            this.inImg = data.img
          }
          if (this.outMachineId === data.machineId) {
            this.outImg = data.img
          }
        }, 1500)
      }
    },
    destroyAllVideo() {
      Object.keys(this.jessibucaMap).forEach(key => {
        try { this.jessibucaMap[key].destroy() } catch (e) { /* */ }
      })
      this.jessibucaMap = {}
      Object.keys(this.huaXiaMap).forEach(key => {
        try { this.huaXiaMap[key].destroy() } catch (e) { /* */ }
      })
      this.huaXiaMap = {}
    },
    refresh() {
      this.listMachines()
    },
    onImgError(e) {
      e.target.src = '/img/init.jpg'
    }
  }
}

function HuaXiaVideoWrapper(url, imageId) {
  this.url = url
  this.ws = null
  this.lockReconnect = false
  this.imgEl = document.getElementById(imageId)
  this.playVideo = function () {
    if (this.ws) { this.ws.close() }
    const that = this
    this.ws = new WebSocket(this.url)
    this.ws.onopen = function () {}
    this.ws.onmessage = function (event) {
      if (typeof event.data === 'string') return
      const reader = new FileReader()
      reader.readAsDataURL(event.data)
      reader.onload = function (eve) {
        if (that.imgEl) { that.imgEl.src = eve.target.result }
      }
    }
    this.ws.onclose = function () { that.reconnect() }
    this.ws.onerror = function () { that.reconnect() }
  }
  this.destroy = function () {
    this.lockReconnect = false
    if (this.ws) { this.ws.close(); this.ws = null }
  }
  this.reconnect = function () {
    if (this.lockReconnect) return
    this.lockReconnect = true
    const that = this
    setTimeout(function () { that.playVideo(); that.lockReconnect = false }, 2000)
  }
}
</script>

<style scoped>
.parking-area-control-video { }
.video-section { margin-bottom: 8px; }
.control-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; flex-wrap: wrap; gap: 4px; }
.control-buttons { display: flex; gap: 4px; flex-wrap: wrap; }
.video-container { position: relative; width: 100%; height: 280px; background: #000; cursor: pointer; overflow: hidden; border: 1px solid #dcdfe6; }
.video-container img { display: block; }
.camera-icon { position: absolute; right: 8px; top: 8px; color: #fff; font-size: 20px; cursor: pointer; opacity: 0.7; z-index: 2; }
.camera-icon:hover { opacity: 1; }
.machine-info { padding: 8px 12px; font-size: 13px; background: #fff; }
.machine-info i { margin-right: 4px; }
.machine-info-empty { color: #909399; }
.capture-images { margin-top: 16px; }
.capture-images img { border: 1px solid #dee2e6; border-radius: 4px; }
</style>
