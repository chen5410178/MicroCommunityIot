/* 用 CDP 控制真实 Chrome 对照测试: localhost vs [::1] vs 127.0.0.1
 * 验证 Chrome 对 ws://localhost 的 IPv6(::1) 优先假设
 */
const http = require('http')

function getJSON(url) {
  return new Promise((resolve, reject) => {
    http.get(url, res => {
      let d = ''
      res.on('data', c => d += c)
      res.on('end', () => { try { resolve(JSON.parse(d)) } catch (e) { reject(e) } })
    }).on('error', reject)
  })
}

async function main() {
  const { spawn } = require('child_process')
  const chrome = spawn('C:/Program Files/Google/Chrome/Application/chrome.exe', [
    '--headless=new', '--disable-gpu', '--no-sandbox',
    '--user-data-dir=' + process.env.TEMP + '\\chrome-cdp-test3',
    '--remote-debugging-port=9225',
    'about:blank'
  ], { stdio: 'ignore' })
  await new Promise(r => setTimeout(r, 3000))

  try {
    let targets
    for (let i = 0; i < 10; i++) {
      try {
        targets = await getJSON('http://127.0.0.1:9225/json')
        if (targets.length) break
      } catch (e) {}
      await new Promise(r => setTimeout(r, 1000))
    }
    const page = targets.find(t => t.type === 'page')
    const ws = new WebSocket(page.webSocketDebuggerUrl)
    let msgId = 0
    const pending = new Map()
    function send(method, params = {}) {
      return new Promise((resolve, reject) => {
        const id = ++msgId
        pending.set(id, { resolve, reject })
        ws.send(JSON.stringify({ id, method, params }))
      })
    }
    ws.onmessage = (e) => {
      const m = JSON.parse(e.data)
      if (m.id && pending.has(m.id)) {
        pending.get(m.id).resolve(m.result)
        pending.delete(m.id)
      }
    }
    await new Promise(r => ws.onopen = r)

    const expr = `
      (async () => {
        try {
          const boxId = '102026071099080061'
          const uid = crypto.randomUUID()
          const out = {}
          const targets = {
            'localhost:3002': 'ws://localhost:3002/ws/parkingBox/' + boxId + '/' + uid,
            '127.0.0.1:3002': 'ws://127.0.0.1:3002/ws/parkingBox/' + boxId + '/' + uid,
            '[::1]:3002': 'ws://[::1]:3002/ws/parkingBox/' + boxId + '/' + uid
          }
          for (const [label, url] of Object.entries(targets)) {
            out[label] = await new Promise(res => {
              const w = new WebSocket(url)
              const t = setTimeout(() => { try{w.close()}catch(e){}; res('PENDING') }, 5000)
              w.onopen = () => { clearTimeout(t); res('OPEN') }
              w.onerror = (e) => { clearTimeout(t); res('ERROR ' + (e.message || 'unknown')) }
            })
          }
          return JSON.stringify(out)
        } catch (e) { return 'EXCEPTION ' + e.message }
      })()
    `
    // 先导航到 dev server 页面(与真实用户环境一致), 等加载完成再测
    await send('Page.enable')
    await send('Page.navigate', { url: 'http://localhost:3002/ws-pending-test.html' })
    await new Promise(r => setTimeout(r, 2000))
    const res = await send('Runtime.evaluate', { expression: expr, awaitPromise: true, returnByValue: true })
    console.log('=== Chrome(真实时间, 页面=http://localhost:3002) 对照结果:', res && res.result && res.result.value)
    ws.close()
  } finally {
    chrome.kill()
  }
}

main().catch(e => { console.error(e); process.exit(1) })
