/* 有头 Chrome（真实时间）测试 ws://localhost:3002 的 WebSocket 连接 */
const http = require('http');
const fs = require('fs');
const path = require('path');
const { spawn } = require('child_process');

const DEBUG_PORT = 9333;
const PAGE_PORT = 9444;
const CHROME = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const USER_DATA = path.join(process.env.TEMP, 'ws-headed-chrome-' + Date.now());
const UUID = '87651e3a-db7f-4c50-8456-3612b76f759c'; // 用户同款
const WS_URL = 'ws://localhost:3002/ws/parkingBox/102026071099080061/' + UUID;

const pageHtml = `<!DOCTYPE html><html><body>
<script>
const out = [];
function log(s){ out.push(new Date().toISOString().slice(17,23) + ' ' + s); console.log(s); }
log('start: ' + ${JSON.stringify(WS_URL)});
const ws = new WebSocket(${JSON.stringify(WS_URL)});
let timer = setTimeout(() => { log('READYSTATE_TIMEOUT_5S readyState=' + ws.readyState); ws.close(); }, 5000);
ws.onopen = () => { clearTimeout(timer); log('OPEN readyState=' + ws.readyState); };
ws.onmessage = (e) => log('MESSAGE: ' + e.data);
ws.onerror = (e) => log('ERROR readyState=' + ws.readyState);
ws.onclose = (e) => { clearTimeout(timer); log('CLOSE code=' + e.code + ' reason=' + e.reason); };
setTimeout(() => { log('RESULT: ' + JSON.stringify(out)); }, 7000);
</script></body></html>`;

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(pageHtml);
});

async function getJson(url) {
  const resp = await fetch(url);
  return resp.json();
}

async function main() {
  await new Promise(r => server.listen(PAGE_PORT, '127.0.0.1', r));
  console.log('[page] http://127.0.0.1:' + PAGE_PORT + '/ws-headed-test.html');

  const chrome = spawn(CHROME, [
    '--remote-debugging-port=' + DEBUG_PORT,
    '--user-data-dir=' + USER_DATA,
    '--no-first-run', '--no-default-browser-check',
    'http://127.0.0.1:' + PAGE_PORT + '/ws-headed-test.html'
  ], { stdio: 'ignore' });

  // 等 CDP 就绪
  let targets;
  for (let i = 0; i < 40; i++) {
    try { targets = await getJson('http://127.0.0.1:' + DEBUG_PORT + '/json'); if (targets.length) break; } catch (e) {}
    await new Promise(r => setTimeout(r, 500));
  }
  if (!targets || !targets.length) { console.log('[fail] CDP 未就绪'); chrome.kill(); process.exit(1); }
  const page = targets.find(t => t.type === 'page');
  console.log('[cdp] page url=', page.url);

  const ws = new WebSocket(page.webSocketDebuggerUrl);
  let id = 0;
  const pending = new Map();
  function send(method, params) {
    return new Promise((resolve, reject) => {
      const mid = ++id;
      pending.set(mid, { resolve, reject });
      ws.send(JSON.stringify({ id: mid, method, params }));
    });
  }
  ws.onmessage = (e) => {
    const m = JSON.parse(e.data);
    if (m.id && pending.has(m.id)) { pending.get(m.id).resolve(m.result); pending.delete(m.id); }
  };
  await new Promise(r => ws.onopen = r);

  await send('Runtime.enable');
  await send('Page.enable');
  // 重新加载页面（保证从我们的 HTML 开始）
  await send('Page.navigate', { url: 'http://127.0.0.1:' + PAGE_PORT + '/ws-headed-test.html' });
  await new Promise(r => setTimeout(r, 1500));

  // 收集 console 日志
  const logs = [];
  ws.onmessage = (e) => {
    const m = JSON.parse(e.data);
    if (m.method === 'Runtime.consoleAPICalled') {
      const text = m.params.args.map(a => a.value || a.description || '').join(' ');
      logs.push(text);
      console.log('[page]', text);
    }
  };

  // 等 8 秒收集结果
  await new Promise(r => setTimeout(r, 8000));
  console.log('\n==== 有头 Chrome 测试完成 ====');
  console.log('URL:', WS_URL);
  console.log('日志:');
  logs.forEach(l => console.log('  ' + l));
  chrome.kill();
  process.exit(0);
}

main().catch(e => { console.error(e); process.exit(1); });
