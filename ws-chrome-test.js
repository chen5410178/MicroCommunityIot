/* 用 CDP 控制真实 Chrome 测试 WebSocket 连接(真实时间, 不受 virtual-time 干扰)
 * 用法: node ws-chrome-test.js
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
  // 启动 Chrome
  const { spawn } = require('child_process')
  const chrome = spawn('C:/Program Files/Google/Chrome/Application/chrome.exe', [
    '--headless=new', '--disable-gpu', '--no-sandbox',
    '--user-data-dir=' + process.env.TEMP + '\\chrome-cdp-test',
    '--remote-debugging-port=9223',
    'about:blank'
  ], { stdio: 'ignore' })

  await new Promise(r => setTimeout(r, 3000))

  try {
    // 取目标
    let targets
    for (let i = 0; i < 10; i++) {
      try {
        targets = await getJSON('http://127.0.0.1:9223/json')
        if (targets.length) break
      } catch (e) {}
      await new Promise(r => setTimeout(r, 1000))
    }
    const page = targets.find(t => t.type === 'page')
    console.log('page ws:', page && page.webSocketDebuggerUrl)

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

    // 导航到测试页
    await send('Page.enable')
    await send('Page.navigate', { url: 'http://localhost:3002/ws-pending-test.html' })
    // 等真实 12 秒(页面内 setTimeout 会给出 readyState 结果)
    await new Promise(r => setTimeout(r, 12000))
    const res = await send('Runtime.evaluate', {
      expression: 'document.getElementById("log").textContent'
    })
    console.log('=== 测试结果:', res && res.result && res.result.value)
    ws.close()
  } finally {
    chrome.kill()
  }
}

main().catch(e => { console.error(e); process.exit(1) })
