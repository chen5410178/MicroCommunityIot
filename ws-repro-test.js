/* 模拟浏览器场景: 页面打开时 HMR(dev-ws) 已连接, 再发起业务 WS
 * 观察业务 WS 是否出现 pending/失败
 */
const net = require('net')
const crypto = require('crypto')

const BOX_ID = '102026071099080061'
const CLIENT_ID = '87651e3a-db7f-4c50-8456-3612b76f759c'

function rawUpgrade(host, port, path, headers = {}, timeoutMs = 5000) {
  return new Promise((resolve) => {
    const key = crypto.randomBytes(16).toString('base64')
    const sock = net.connect(port, host)
    let buf = ''
    let done = false
    const finish = (stage, extra) => {
      if (done) return
      done = true
      clearTimeout(timer)
      sock.destroy()
      resolve({ stage, extra, raw: buf, head: buf.split('\r\n\r\n')[0] })
    }
    const timer = setTimeout(() => finish('PENDING(5s无响应)'), timeoutMs)
    sock.on('connect', () => {
      const lines = [
        `GET ${path} HTTP/1.1`,
        `Host: ${host}:${port}`,
        'Origin: http://localhost:3002',
        'Upgrade: websocket',
        'Connection: Upgrade',
        `Sec-WebSocket-Key: ${key}`,
        'Sec-WebSocket-Version: 13',
        ...Object.entries(headers).map(([k, v]) => `${k}: ${v}`)
      ]
      sock.write(lines.join('\r\n') + '\r\n\r\n')
    })
    sock.on('data', d => {
      buf += d.toString('utf8')
      if (buf.includes('\r\n\r\n')) finish('RESPONSE')
    })
    sock.on('error', e => finish('ERROR', e.message))
    sock.on('close', () => finish('CLOSE'))
  })
}

async function main() {
  const rounds = 10
  console.log(`模拟浏览器场景: 先连 HMR(ws://localhost:3002/dev-ws), 再连业务 WS, 共 ${rounds} 轮\n`)

  for (let i = 1; i <= rounds; i++) {
    // 1. 先建立 HMR 连接(保持打开)
    const hmrSock = net.connect(3002, 'localhost')
    let hmrReady = false
    await new Promise((resolve) => {
      const key = crypto.randomBytes(16).toString('base64')
      hmrSock.on('connect', () => {
        hmrSock.write([
          'GET /dev-ws HTTP/1.1',
          'Host: localhost:3002',
          'Upgrade: websocket',
          'Connection: Upgrade',
          `Sec-WebSocket-Key: ${key}`,
          'Sec-WebSocket-Version: 13',
          '', ''
        ].join('\r\n'))
      })
      hmrSock.on('data', d => {
        if (!hmrReady && d.toString('utf8').includes('101')) {
          hmrReady = true
          resolve()
        }
      })
      hmrSock.on('error', () => resolve())
      setTimeout(resolve, 3000)
    })
    hmrSock.on('error', () => {})

    // 2. 业务 WS(带浏览器扩展头)
    const r = await rawUpgrade('localhost', 3002, `/ws/parkingBox/${BOX_ID}/${CLIENT_ID}`, {
      'Sec-WebSocket-Extensions': 'permessage-deflate; client_max_window_bits'
    })
    const status = r.head ? (r.head.match(/HTTP\/1\.[01] (\d+)/) || [])[1] : '?'
    console.log(`轮次${i}: HMR=${hmrReady ? 'OK' : 'FAIL'} | 业务WS: ${r.stage} ${status}${r.extra ? ' ' + r.extra : ''}`)

    hmrSock.destroy()
    await new Promise(res => setTimeout(res, 200))
  }
}

main().catch(e => { console.error(e); process.exit(1) })
