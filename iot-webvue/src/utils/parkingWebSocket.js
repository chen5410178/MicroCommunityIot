export default class ParkingWebSocket {
  constructor(endpoint, id, onData) {
    this.endpoint = endpoint || 'parkingBox'
    this.id = id
    this.onData = onData
    this.clientId = this._uuid()
    this.websocket = null
    this.wsPingTimer = null
    this.wsPingInterval = null
    this.reconnectTimer = null
    this.destroyed = false
    this.init()
  }

  _uuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
      const r = (Math.random() * 16) | 0
      const v = c === 'x' ? r : (r & 0x3) | 0x8
      return v.toString(16)
    })
  }

  init() {
    const wsProtocol = window.location.protocol.startsWith('https') ? 'wss' : 'ws'
    const url = `${wsProtocol}://${window.location.host}/iot/ws/${this.endpoint}/${this.id}/${this.clientId}`
    try {
      this.websocket = new WebSocket(url)
    } catch (e) {
      this._scheduleReconnect()
      return
    }
    this.websocket.onopen = () => this._startHeartbeat()
    this.websocket.onmessage = (event) => {
      this.wsPingTimer = new Date().getTime()
      let data = event.data
      try { data = JSON.parse(data) } catch (e) { return }
      this.onData && this.onData(data)
    }
    this.websocket.onerror = (e) => {
      console.error('[ParkingWebSocket] 连接错误', url, e)
    }
    this.websocket.onclose = (e) => {
      console.warn('[ParkingWebSocket] 连接关闭', url, 'code:', e && e.code, 'reason:', e && e.reason)
      this._stopHeartbeat()
      this._scheduleReconnect()
    }
  }

  _startHeartbeat() {
    this.wsPingTimer = new Date().getTime()
    this._stopHeartbeat()
    this.wsPingInterval = setInterval(() => {
      if (this.websocket && this.websocket.readyState === 1) {
        const now = new Date().getTime()
        if (now - this.wsPingTimer > 15000) {
          this.websocket.send("{'cmd':'ping'}")
          this.wsPingTimer = now
        }
      } else {
        this.websocket && this.websocket.close()
      }
    }, 30000)
  }

  _stopHeartbeat() {
    if (this.wsPingInterval) {
      clearInterval(this.wsPingInterval)
      this.wsPingInterval = null
    }
  }

  _scheduleReconnect() {
    if (this.destroyed || this.reconnectTimer) return
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null
      if (!this.destroyed) this.init()
    }, 3000)
  }

  release() {
    this.destroyed = true
    this._stopHeartbeat()
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.websocket) {
      this.websocket.onclose = null
      this.websocket.close()
      this.websocket = null
    }
  }
}
