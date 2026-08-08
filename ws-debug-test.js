/* 停车场岗亭 WebSocket 连接诊断脚本
 * 用法: node ws-debug-test.js [boxId] [clientId]
 * 默认 boxId=102026071099080061, clientId=自动生成标准 UUID
 * 分别测试:
 *   1. 直连后端 ws://192.168.101.109:9999
 *   2. 经 dev server 代理 ws://localhost:3002
 */
const net = require('net')
const crypto = require('crypto')

const boxId = process.argv[2] || '102026071099080061'
const clientId = process.argv[3] || crypto.randomUUID()
console.log(`boxId    = ${boxId}`)
console.log(`clientId = ${clientId} (${clientId.length}位, 含横线:${clientId.includes('-')})`)
console.log('='.repeat(80))

// 手动 HTTP Upgrade 请求, 打印原始响应头, 可看到 101/404/502/403 等状态
function httpUpgrade(url, timeoutMs = 8000) {
  return new Promise((resolve) => {
    const u = new URL(url)
    const key = crypto.randomBytes(16).toString('base64')
    const port = u.port || (u.protocol === 'wss:' ? 443 : 80)
    const host = u.hostname
    const sock = net.connect(port, host)
    let buf = ''
    let done = false
    const finish = (stage, extra) => {
      if (done) return
      done = true
      clearTimeout(timer)
      sock.destroy()
      resolve({ stage, extra, raw: buf })
    }
    const timer = setTimeout(() => finish('timeout(8s无响应)'), timeoutMs)
    sock.on('connect', () => {
      const path = u.pathname + (u.search || '')
      const req = [
        `GET ${path} HTTP/1.1`,
        `Host: ${host}:${port}`,
        'Upgrade: websocket',
        'Connection: Upgrade',
        `Sec-WebSocket-Key: ${key}`,
        'Sec-WebSocket-Version: 13',
        'Origin: http://localhost:3002',
        ''
      ].join('\r\n') + '\r\n'
      console.log(`[Upgrade请求] ${url}`)
      sock.write(req)
    })
    sock.on('data', d => {
      buf += d.toString('utf8')
      if (buf.includes('\r\n\r\n')) finish('response')
    })
    sock.on('error', e => finish('error', e.message))
    sock.on('close', () => finish('close'))
  })
}

// 原生 WebSocket 客户端, 打印 open/error/close 事件
function wsConnect(url, label, timeoutMs = 8000) {
  return new Promise((resolve) => {
    console.log(`\n[WebSocket] ${label}: ${url}`)
    let done = false
    const finish = (r) => { if (!done) { done = true; resolve(r) } }
    const timer = setTimeout(() => finish({ event: 'timeout', detail: '8s未收到任何事件' }), timeoutMs)
    try {
      const ws = new WebSocket(url)
      ws.onopen = () => { clearTimeout(timer); finish({ event: 'open', detail: '握手成功 101' }) }
      ws.onerror = (e) => { finish({ event: 'error', detail: e && e.message ? e.message : (e && e.error ? String(e.error) : '未知错误(通常伴随close)') }) }
      ws.onclose = (e) => { clearTimeout(timer); finish({ event: 'close', detail: `code=${e.code} reason=${e.reason || ''} wasClean=${e.wasClean}` }) }
      ws.onmessage = (m) => { console.log(`    [消息] ${m.data}`) }
    } catch (e) {
      clearTimeout(timer)
      finish({ event: 'exception', detail: String(e && e.stack || e) })
    }
  })
}

async function main() {
  const path = `/ws/parkingBox/${boxId}/${clientId}`
  const targets = [
    { url: `ws://192.168.101.109:9999${path}`, label: '直连后端9999' },
    { url: `ws://localhost:3002${path}`, label: '经dev server 3002代理' }
  ]

  // 先测普通 HTTP 转发是否通(代理健康检查)
  console.log('[HTTP检查] http://localhost:3002/iot/api/xxx')
  try {
    const r = await fetch('http://localhost:3002/iot/api/xxx', { method: 'GET' })
    console.log(`    status=${r.status}`)
  } catch (e) {
    console.log(`    fetch失败: ${e.message}`)
  }
  console.log('='.repeat(80))

  for (const t of targets) {
    const up = await httpUpgrade(t.url)
    const head = up.raw.split('\r\n\r\n')[0] || up.raw
    console.log(`\n--- [HTTP Upgrade] ${t.label} ---`)
    console.log(`阶段: ${up.stage}${up.extra ? ' | ' + up.extra : ''}`)
    console.log(`响应头:\n${head}`)
    if (up.raw.includes('Sec-WebSocket-Accept')) {
      console.log('>>> 握手成功, 服务器已确认升级')
    } else if (/^HTTP\/1\.[01] 101/.test(head)) {
      console.log('>>> 状态101, 但缺 Sec-WebSocket-Accept(异常)')
    } else {
      console.log('>>> 握手失败(非101)')
    }

    const r = await wsConnect(t.url, t.label)
    console.log(`    [结果] ${r.event}: ${r.detail}`)
  }
  console.log('\n' + '='.repeat(80))
  console.log('诊断完成')
}

main().catch(e => { console.error(e); process.exit(1) })
