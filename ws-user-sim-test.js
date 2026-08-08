/* 完整模拟用户场景：页面经 http://localhost:3002 打开，页面内 WS 连 ws://localhost:3002（带 netlog） */
const http = require('http');
const fs = require('fs');
const path = require('path');
const { spawn } = require('child_process');

const DEBUG_PORT = 9334;
const NETLOG = path.join(process.env.TEMP, 'ws-user-netlog.json');
const CHROME = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const USER_DATA = path.join(process.env.TEMP, 'ws-user-chrome-' + Date.now());
const UUID = '87651e3a-db7f-4c50-8456-3612b76f759c';
const WS_URL = 'ws://localhost:3002/ws/parkingBox/102026071099080061/' + UUID;

async function getJson(url) {
  const resp = await fetch(url);
  return resp.json();
}

async function main() {
  const chrome = spawn(CHROME, [
    '--remote-debugging-port=' + DEBUG_PORT,
    '--user-data-dir=' + USER_DATA,
    '--no-first-run', '--no-default-browser-check',
    '--log-net-log=' + NETLOG,
    'http://localhost:3002/#/ws-user-sim' // 模拟用户从 localhost:3002 打开
  ], { stdio: 'ignore' });

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
  const logs = [];
  ws.onmessage = (e) => {
    const m = JSON.parse(e.data);
    if (m.id && pending.has(m.id)) { pending.get(m.id).resolve(m.result); pending.delete(m.id); }
    if (m.method === 'Runtime.consoleAPICalled') {
      const text = m.params.args.map(a => a.value || a.description || '').join(' ');
      logs.push(text);
      console.log('[page]', text);
    }
  };
  if (ws.readyState !== 1) await new Promise(r => ws.onopen = r);
  await send('Runtime.enable');

  // 在页面内执行与用户相同的操作：new WebSocket(...)
  const script = `
    (function(){
      const ws = new WebSocket('${WS_URL}');
      const out = [];
      const t = setTimeout(() => { out.push('5s timeout, readyState=' + ws.readyState); window.__wsOut = JSON.stringify(out); console.log('[WS] ' + out.join(' | ')); ws.close(); }, 5000);
      ws.onopen = () => { clearTimeout(t); out.push('OPEN readyState=' + ws.readyState); window.__wsOut = JSON.stringify(out); console.log('[WS] ' + out.join(' | ')); };
      ws.onmessage = (e) => { out.push('MESSAGE: ' + e.data); window.__wsOut = JSON.stringify(out); console.log('[WS] ' + out.join(' | ')); };
      ws.onerror = () => { out.push('ERROR readyState=' + ws.readyState); window.__wsOut = JSON.stringify(out); console.log('[WS] ' + out.join(' | ')); };
      ws.onclose = (e) => { clearTimeout(t); out.push('CLOSE code=' + e.code); window.__wsOut = JSON.stringify(out); console.log('[WS] ' + out.join(' | ')); };
      return 'injected';
    })()
  `;
  const r = await send('Runtime.evaluate', { expression: script, returnByValue: true });
  console.log('[inject]', JSON.stringify(r.result && r.result.value));

  await new Promise(r => setTimeout(r, 8000));
  const res = await send('Runtime.evaluate', {
    expression: `JSON.stringify((window.__wsOut || 'no capture'))`,
    returnByValue: true
  });
  console.log('==== 用户场景模拟完成 ====');
  console.log('页面 URL:', page.url);
  console.log('WS URL:', WS_URL);
  console.log('日志:');
  logs.forEach(l => console.log('  ' + l));

  chrome.kill();
  setTimeout(() => {
    try {
      const netlog = JSON.parse(fs.readFileSync(NETLOG, 'utf8'));
      const evs = netlog.events || [];
      console.log('\n==== netlog 关键事件 ====');
      let wsSeen = false;
      for (const ev of evs) {
        const p = ev.params || {};
        const src = ev.source && ev.source.type || '';
        const s = ev.source && ev.source.id || '';
        const line = JSON.stringify(ev.params);
        if (line.includes('3002')) {
          if (line.includes('TRANSPORT_CONNECT_JOB_CONNECT_ATTEMPT') || line.includes('TCP_CONNECT_ATTEMPT') || line.includes('TCP_CONNECT phase') || line.includes('CONNECTION_POOL') || line.includes('CANCELLED')) {
            console.log(`  t=${ev.time} ${src}${s ? ':' + JSON.stringify(s) : ''} ${ev.type}: ${line.slice(0, 180)}`);
          }
        }
      }
    } catch (e) { console.log('[netlog 读取失败]', e.message); }
  }, 3000);
}

main().catch(e => { console.error(e); process.exit(1); });
