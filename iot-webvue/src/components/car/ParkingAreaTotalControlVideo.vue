<template>
  <div class="total-control-video">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>总控台</span>
      </div>
      <div v-loading="loading">
        <el-row :gutter="16">
          <el-col :span="12" v-for="item in machines" :key="item.machineId" class="machine-col">
            <div class="machine-card">
              <div class="machine-header">
                <div class="machine-name">
                  <span class="name">{{ item.machineName }}</span>
                  <el-tag size="mini" :type="item.direction === '3306' ? 'success' : 'warning'">
                    {{ item.direction === '3306' ? '进口' : '出口' }}
                  </el-tag>
                </div>
                <div class="machine-actions">
                  <el-button v-if="item.direction === '3306'" size="mini" @click="customCarIn(item, '1101')">手动进场</el-button>
                  <el-button v-else size="mini" @click="customCarIn(item, '1102')">手动出场</el-button>
                  <el-button size="mini" type="primary" @click="openDoor(item)">开闸</el-button>
                  <el-button size="mini" type="primary" @click="closeDoor(item)">关闸</el-button>
                </div>
              </div>
              <div class="video-container" :id="'receiverDiv' + item.machineId" @click="openCloudFlvVideo(item.machineId)">
                <img width="100%" src="/img/init.jpg" height="280" :id="'receiver' + item.machineId" style="object-fit: cover" />
                <img v-if="item.inOutImg" :src="item.inOutImg" class="inout-img" />
                <div class="camera-icon" v-if="item.machineId">
                  <i class="el-icon-video-camera"></i>
                </div>
              </div>
              <div class="machine-info" v-if="item.carNum">
                <i class="el-icon-bell" style="color: #007bff"></i>
                <span>{{ item.inOutTime }}</span>
                <span>{{ item.open }}</span>
                <span>{{ item.openMsg }}</span>
              </div>
              <div class="machine-info machine-info-empty" v-else>
                <i class="el-icon-bell" style="color: #007bff"></i>
                <span>没有进场车辆</span>
              </div>
            </div>
          </el-col>
        </el-row>
        <div v-if="machines.length === 0 && !loading" class="empty-tip">暂无道闸设备</div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listBarriers, openBarrierDoor, closeBarrierDoor, getCloudFlvVideo } from '@/api/car/carApi'

let HuaXiaVideo = null
try {
  // HuaxiaVideo is bundled as a module
} catch (e) {
  // ignore
}

export default {
  name: 'ParkingAreaTotalControlVideo',
  props: {
    paId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      machines: [],
      jessibucaMap: {},
      huaXiaMap: {}
    }
  },
  watch: {
    paId: {
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listBarriers({ paId: this.paId, page: 1, row: 100, communityId })
        const data = res.data || []
        data.forEach(item => {
          item.carNum = ''
          item.inOutTime = ''
          item.open = ''
          item.openMsg = ''
          item.inOutImg = ''
        })
        this.machines = data
        this.$nextTick(() => {
          this.initMachineVideo()
        })
      } catch (e) {
        console.error('获取道闸设备失败', e)
      } finally {
        this.loading = false
      }
    },
    initMachineVideo() {
      setTimeout(() => {
        this.machines.forEach(item => {
          this.switchVideo(item)
        })
      }, 2000)
    },
    switchVideo(machine) {
      let wsUrl = machine.machineIp
      const hmId = machine.hmId

      if (hmId === '24' || hmId === 24) {
        wsUrl = 'ws://' + wsUrl + ':9999'
        this.showHuaXiaVideo(wsUrl, 'receiverDiv' + machine.machineId)
        return
      }

      wsUrl = (wsUrl || '').replace(':8131', ':9080')
      if (wsUrl.indexOf(':') < 0) {
        wsUrl = wsUrl + ':9080'
      }
      wsUrl += '/ws.flv'
      wsUrl = 'ws://' + wsUrl
      this.showCameraVideo(wsUrl, 'receiverDiv' + machine.machineId)
    },
    showCameraVideo(wsUrl, divId) {
      const container = document.getElementById(divId)
      if (!container) return

      if (this.jessibucaMap[divId]) {
        try { this.jessibucaMap[divId].destroy() } catch (e) { /* ignore */ }
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
    showHuaXiaVideo(wsUrl, divId) {
      const imgId = divId.replace('receiverDiv', 'receiver')
      if (this.huaXiaMap[divId]) {
        try { this.huaXiaMap[divId].destroy() } catch (e) { /* ignore */ }
      }

      try {
        const huaXiaVideo = new HuaXiaVideoWrapper(wsUrl, imgId)
        huaXiaVideo.playVideo()
        this.huaXiaMap[divId] = huaXiaVideo
      } catch (e) {
        console.error('华夏视频初始化失败', e)
        this.showHuaXiaVideoFallback(wsUrl, divId)
      }
    },
    showHuaXiaVideoFallback(wsUrl, divId) {
      const imgId = divId.replace('receiverDiv', 'receiver')
      const imgEl = document.getElementById(imgId)
      if (!imgEl) return

      let ws = new WebSocket(wsUrl)
      ws.onopen = function () {
        // connected
      }
      ws.onmessage = function (event) {
        if (typeof event.data === 'string') return
        const reader = new FileReader()
        reader.readAsDataURL(event.data)
        reader.onload = function (eve) {
          imgEl.src = eve.target.result
        }
      }
      ws.onclose = function () {
        // reconnect
      }
      ws.onerror = function () {
        // error
      }
      this.huaXiaMap[divId] = { ws, destroy: () => { try { ws.close() } catch (e) { /* */ } } }
    },
    openDoor(machine) {
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      const data = {
        machineCode: machine.machineCode,
        stateName: '开门',
        state: '1500',
        url: '/barrier.openBarrierDoor',
        userRole: 'staff',
        communityId
      }
      openBarrierDoor(data).then(res => {
        if (res.code === 0) {
          this.$message.success('已请求设备')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      }).catch(() => {
        this.$message.error('操作失败')
      })
    },
    closeDoor(machine) {
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      const data = {
        machineCode: machine.machineCode,
        stateName: '关门',
        state: '1500',
        url: '/barrier.closeBarrierDoor',
        userRole: 'staff',
        communityId
      }
      closeBarrierDoor(data).then(res => {
        if (res.code === 0) {
          this.$message.success('已请求设备')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      }).catch(() => {
        this.$message.error('操作失败')
      })
    },
    customCarIn(machine, type) {
      this.$emit('show-custom-inout', {
        type: type,
        machineId: machine.machineId,
        boxId: machine.locationObjId,
        paId: this.paId
      })
    },
    async openCloudFlvVideo(machineId) {
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      try {
        const res = await getCloudFlvVideo({ machineId, communityId })
        if (res.code === 0 && res.data && res.data.url) {
          this.showCameraVideo(res.data.url, 'receiverDiv' + machineId)
        } else {
          this.$message.error(res.msg || '获取视频失败')
        }
      } catch (e) {
        this.$message.error('获取视频失败')
      }
    },
    onWsMessage(data) {
      if (data.action === 'FEE_INFO') {
        this.showCarInoutMachineInoutInfo(data)
      } else if (data.action === 'IN_OUT') {
        this.showCarInoutMachineImgInfo(data)
      }
    },
    showCarInoutMachineInoutInfo(data) {
      const matchedMachine = this.machines.find(item => item.machineId === data.machineId)
      if (matchedMachine) {
        matchedMachine.carNum = data.carNum
        matchedMachine.inOutTime = data.inOutTime
        matchedMachine.open = data.open
        matchedMachine.openMsg = data.remark || ''
      }
      this.$emit('ws-data', { _machine: matchedMachine, _data: data })
    },
    showCarInoutMachineImgInfo(data) {
      this.machines.forEach(item => {
        if (item.machineId === data.machineId) {
          setTimeout(() => {
            item.inOutImg = data.clitImg
          }, 1500)
        }
      })
    },
    refresh() {
      this.listMachines()
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
    }
  }
}

function HuaXiaVideoWrapper(url, imageId) {
  this.url = url
  this.ws = null
  this.lockReconnect = false
  this.imgEl = document.getElementById(imageId)

  this.playVideo = function () {
    if (this.ws) {
      this.ws.close()
    }
    const that = this
    this.ws = new WebSocket(this.url)
    this.ws.onopen = function () {
      // connected
    }
    this.ws.onmessage = function (event) {
      if (typeof event.data === 'string') return
      const reader = new FileReader()
      reader.readAsDataURL(event.data)
      reader.onload = function (eve) {
        if (that.imgEl) {
          that.imgEl.src = eve.target.result
        }
      }
    }
    this.ws.onclose = function () {
      that.reconnect()
    }
    this.ws.onerror = function () {
      that.reconnect()
    }
  }

  this.destroy = function () {
    this.lockReconnect = false
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  this.reconnect = function () {
    if (this.lockReconnect) return
    this.lockReconnect = true
    const that = this
    setTimeout(function () {
      that.playVideo()
      that.lockReconnect = false
    }, 2000)
  }
}
</script>

<style scoped>
.machine-col {
  margin-bottom: 16px;
}
.machine-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}
.machine-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  flex-wrap: wrap;
  gap: 8px;
}
.machine-name .name {
  font-size: 14px;
  font-weight: 600;
  margin-right: 6px;
}
.machine-actions {
  display: flex;
  gap: 4px;
}
.video-container {
  position: relative;
  width: 100%;
  height: 280px;
  background: #000;
  cursor: pointer;
  overflow: hidden;
}
.video-container img {
  display: block;
}
.inout-img {
  position: absolute;
  left: 0;
  bottom: 0;
  height: 70px;
  z-index: 1;
}
.camera-icon {
  position: absolute;
  right: 8px;
  top: 8px;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  opacity: 0.7;
  z-index: 2;
}
.camera-icon:hover {
  opacity: 1;
}
.machine-info {
  padding: 8px 12px;
  font-size: 13px;
  background: #fff;
}
.machine-info i {
  margin-right: 4px;
}
.machine-info-empty {
  color: #909399;
}
.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
