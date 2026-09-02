/**
 * 批量 SVG → PNG 图标渲染脚本（方案 4.2 方案 A：Chrome headless）
 * 用法：node scripts/svg2png.js
 * 输入：下方 icons 定义（SVG path 取自原型 0818充电微信小程序_app(1).html）
 * 输出：../../iot-webvue/public/h5/images/{common,charge}/*.png（200×200 透明底，内容 150×150 居中）
 */
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const CHROME = 'C:/Program Files/Google/Chrome/Application/chrome.exe';
const ROOT = path.resolve(__dirname, '..');
const SRC_DIR = path.join(__dirname, '_svg-src');
const OUT_DIR = path.join(ROOT, '..', '..', 'iot-webvue', 'public', 'h5', 'images');

// 图标定义：[子目录, 文件名, 颜色, 是否填充, path d 数组（含 rect/circle/line 前缀标记）]
const icons = [
  // ================= common（TabBar + 通用） =================
  ['common', 'ic-home', '#9ca3af', false, [
    'p:M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
    'p:M9 22V12h6v10'
  ]],
  ['common', 'ic-home-selected', '#10b981', false, [
    'p:M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
    'p:M9 22V12h6v10'
  ]],
  ['common', 'ic-scan', '#9ca3af', false, [
    'p:M3 7V5a2 2 0 0 1 2-2h2',
    'p:M17 3h2a2 2 0 0 1 2 2v2',
    'p:M21 17v2a2 2 0 0 1-2 2h-2',
    'p:M7 21H5a2 2 0 0 1-2-2v-2',
    'l:7 12 17 12',
    'l:12 7 12 17'
  ]],
  ['common', 'ic-scan-selected', '#10b981', false, [
    'p:M3 7V5a2 2 0 0 1 2-2h2',
    'p:M17 3h2a2 2 0 0 1 2 2v2',
    'p:M21 17v2a2 2 0 0 1-2 2h-2',
    'p:M7 21H5a2 2 0 0 1-2-2v-2',
    'l:7 12 17 12',
    'l:12 7 12 17'
  ]],
  ['common', 'ic-user', '#9ca3af', false, [
    'p:M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2',
    'c:12 7 4'
  ]],
  ['common', 'ic-user-selected', '#10b981', false, [
    'p:M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2',
    'c:12 7 4'
  ]],
  ['common', 'ic-back', '#334155', false, [
    'p:M15 18l-6-6 6-6'
  ]],
  ['common', 'ic-bell', '#334155', false, [
    'p:M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9',
    'p:M13.73 21a2 2 0 0 1-3.46 0'
  ]],
  ['common', 'ic-bell-violet', '#8b5cf6', false, [
    'p:M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9',
    'p:M13.73 21a2 2 0 0 1-3.46 0'
  ]],
  ['common', 'ic-moon', '#a5b4fc', false, [
    'p:M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z'
  ]],
  ['common', 'ic-sun', '#fbbf24', false, [
    'c:12 12 4',
    'p:M12 2v2',
    'p:M12 20v2',
    'p:M4.93 4.93l1.41 1.41',
    'p:M17.66 17.66l1.41 1.41',
    'p:M2 12h2',
    'p:M20 12h2',
    'p:M6.34 17.66l-1.41 1.41',
    'p:M19.07 4.93l-1.41 1.41'
  ]],
  ['common', 'ic-pin', '#64748b', false, [
    'p:M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z',
    'c:12 10 3'
  ]],
  ['common', 'ic-pin-green', '#10b981', false, [
    'p:M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z',
    'c:12 10 3'
  ]],
  ['common', 'ic-pin-white', '#ffffff', false, [
    'p:M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z',
    'c:12 10 3'
  ]],
  ['common', 'ic-chevron-down', '#94a3b8', false, [
    'p:M6 9l6 6 6-6'
  ]],
  ['common', 'ic-chevron-down-white', '#ffffff', false, [
    'p:M6 9l6 6 6-6'
  ]],
  ['common', 'ic-chevron-right', '#94a3b8', false, [
    'p:M9 18l6-6-6-6'
  ]],
  ['common', 'ic-check', '#10b981', false, [
    'p:M20 6L9 17l-5-5'
  ]],
  ['common', 'ic-check-gray', '#94a3b8', false, [
    'p:M20 6L9 17l-5-5'
  ]],
  ['common', 'ic-alert', '#ef4444', false, [
    'c:12 12 10',
    'p:M12 16v-4',
    'p:M12 8h.01'
  ]],
  ['common', 'ic-info', '#94a3b8', false, [
    'c:12 12 10',
    'p:M12 16v-4',
    'p:M12 8h.01'
  ]],
  ['common', 'ic-plus', '#ffffff', false, [
    'c:12 12 10',
    'p:M12 8v8',
    'p:M8 12h8'
  ]],
  ['common', 'ic-phone', '#6366f1', false, [
    'p:M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z'
  ]],
  ['common', 'ic-lock', '#334155', false, [
    'r:3 11 18 11 2',
    'p:M7 11V7a5 5 0 0 1 10 0v4'
  ]],
  ['common', 'ic-lock-green', '#10b981', false, [
    'r:3 11 18 11 2',
    'p:M7 11V7a5 5 0 0 1 10 0v4'
  ]],
  ['common', 'ic-lock-white', '#ffffff', false, [
    'r:3 11 18 11 2',
    'p:M7 11V7a5 5 0 0 1 10 0v4'
  ]],
  ['common', 'ic-trash', '#ef4444', false, [
    'p:M3 6h18',
    'p:M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6',
    'p:M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2',
    'l:10 11 10 17',
    'l:14 11 14 17'
  ]],
  ['common', 'ic-eye', '#059669', false, [
    'p:M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z',
    'c:12 12 3'
  ]],
  ['common', 'ic-delete', '#334155', false, [
    'p:M21 4H8l-7 8 7 8h13a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2z',
    'p:M18 9l-6 6',
    'p:M12 9l6 6'
  ]],
  ['common', 'ic-search', '#94a3b8', false, [
    'c:11 11 8',
    'p:M21 21l-4.35-4.35'
  ]],
  ['common', 'ic-edit', '#64748b', false, [
    'p:M12 20h9',
    'p:M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z'
  ]],
  ['common', 'ic-doc', '#94a3b8', false, [
    'p:M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z',
    'p:M14 2v6h6'
  ]],
  ['common', 'ic-shield', '#94a3b8', false, [
    'p:M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'
  ]],
  ['common', 'ic-inbox', '#94a3b8', false, [
    'p:M22 12h-6l-2 3h-4l-2-3H2',
    'p:M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z'
  ]],
  ['common', 'ic-user-plus', '#ffffff', false, [
    'p:M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2',
    'c:9 7 4',
    'p:M19 8v6',
    'p:M22 11h-6'
  ]],

  // ================= charge（充电业务） =================
  ['charge', 'ic-bolt', '#10b981', false, [
    'p:M13 2L3 14h7l-1 8 10-12h-7l1-8z'
  ]],
  ['charge', 'ic-bolt-white', '#ffffff', true, [
    'p:M13 2L3 14h7l-1 8 10-12h-7l1-8z'
  ]],
  ['charge', 'ic-scan', '#10b981', false, [
    'p:M3 7V5a2 2 0 0 1 2-2h2',
    'p:M17 3h2a2 2 0 0 1 2 2v2',
    'p:M21 17v2a2 2 0 0 1-2 2h-2',
    'p:M7 21H5a2 2 0 0 1-2-2v-2',
    'l:7 12 17 12',
    'l:12 7 12 17'
  ]],
  ['charge', 'ic-calendar', '#f59e0b', false, [
    'r:3 4 18 18 2',
    'l:16 2 16 6',
    'l:8 2 8 6',
    'l:3 10 21 10',
    'p:M9 16l2 2 4-4'
  ]],
  ['charge', 'ic-calendar-white', '#ffffff', false, [
    'r:3 4 18 18 2',
    'l:16 2 16 6',
    'l:8 2 8 6',
    'l:3 10 21 10',
    'p:M9 16l2 2 4-4'
  ]],
  ['charge', 'ic-wallet', '#3b82f6', false, [
    'p:M20 12V8H6a2 2 0 0 1 0-4h12v4',
    'p:M4 6v12a2 2 0 0 0 2 2h14v-4',
    'p:M18 12a2 2 0 0 0 0 4h4v-4z'
  ]],
  ['charge', 'ic-chart', '#8b5cf6', false, [
    'p:M3 3v18h18',
    'p:M18 17V9',
    'p:M13 17V5',
    'p:M8 17v-3'
  ]],
  ['charge', 'ic-bike', '#10b981', false, [
    'c:5.5 17.5 3.5',
    'c:18.5 17.5 3.5',
    'p:M15 6h4l-2 6h-7l-2.5-4H4',
    'p:M9 10h4'
  ]],
  ['charge', 'ic-bike-light', '#93c5fd', false, [
    'c:5.5 17.5 3.5',
    'c:18.5 17.5 3.5',
    'p:M15 6h4l-2 6h-7l-2.5-4H4',
    'p:M9 10h4'
  ]],
  ['charge', 'ic-car', '#3b82f6', false, [
    'p:M5 17h14',
    'p:M3 17l1.5-5.5A2 2 0 0 1 6.4 10h11.2a2 2 0 0 1 1.9 1.5L21 17',
    'c:7 17 1.6',
    'c:17 17 1.6'
  ]],
  ['charge', 'ic-car-light', '#6ee7b7', false, [
    'p:M5 17h14',
    'p:M3 17l1.5-5.5A2 2 0 0 1 6.4 10h11.2a2 2 0 0 1 1.9 1.5L21 17',
    'c:7 17 1.6',
    'c:17 17 1.6'
  ]],
  ['charge', 'ic-car-white', '#ffffff', false, [
    'p:M5 17h14',
    'p:M3 17l1.5-5.5A2 2 0 0 1 6.4 10h11.2a2 2 0 0 1 1.9 1.5L21 17',
    'c:7 17 1.6',
    'c:17 17 1.6'
  ]],
  ['charge', 'ic-car-full', '#3b82f6', false, [
    'p:M5 17h14l-1.5-8a2 2 0 0 0-2-1.5h-7A2 2 0 0 0 6.5 9L5 17z',
    'p:M5 17v3',
    'p:M19 17v3',
    'c:8.5 14 1',
    'c:15.5 14 1'
  ]],
  ['charge', 'ic-car-nav', '#10b981', true, [
    'p:M5 11l1.5-4.5A2 2 0 0 1 8.4 5h7.2a2 2 0 0 1 1.9 1.5L19 11m-14 0h14m-14 0a2 2 0 0 0-2 2v3h2m14-5a2 2 0 0 1 2 2v3h-2m-14 0h14m-14 0a2 2 0 1 0 4 0m6 0a2 2 0 1 0 4 0'
  ]],
  ['charge', 'ic-credit', '#10b981', false, [
    'r:2 5 20 14 3',
    'p:M2 10h20',
    'c:16 15 1.5'
  ]],
  ['charge', 'ic-card', '#f59e0b', false, [
    'r:3 4 18 18 2',
    'l:16 2 16 6',
    'l:8 2 8 6',
    'l:3 10 21 10',
    'p:M9 16l2 2 4-4'
  ]],
  ['charge', 'ic-clock', '#334155', false, [
    'c:12 12 10',
    'p:M12 6v6l4 2'
  ]],
  ['charge', 'ic-phone', '#10b981', false, [
    'p:M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z'
  ]],
  ['charge', 'ic-phone-white', '#ffffff', false, [
    'p:M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z'
  ]],
  ['charge', 'ic-plug', '#94a3b8', false, [
    'p:M12 22v-5',
    'p:M9 8V2',
    'p:M15 8V2',
    'p:M18 8v5a4 4 0 0 1-4 4h-4a4 4 0 0 1-4-4V8Z'
  ]],
  ['charge', 'ic-receipt', '#10b981', false, [
    'p:M3 7v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-6l-2-2H5a2 2 0 0 0-2 2z',
    'p:M15 14l2-2-2-2',
    'p:M11 14h6'
  ]],
  ['charge', 'ic-invoice', '#f59e0b', false, [
    'r:3 4 18 18 2',
    'l:16 2 16 6',
    'l:8 2 8 6',
    'l:3 10 21 10'
  ]]
];

// 生成 SVG 源
fs.mkdirSync(SRC_DIR, { recursive: true });
for (const [dir, name, color, fill, parts] of icons) {
  const inner = parts.map(part => {
    const [tag, ...rest] = part.split(':');
    if (tag === 'p') return `<path d="${rest.join(':')}"/>`;
    if (tag === 'c') {
      const [cx, cy, r] = rest.join(':').split(' ').map(Number);
      return `<circle cx="${cx}" cy="${cy}" r="${r}"/>`;
    }
    if (tag === 'r') {
      const [x, y, w, h, rx] = rest.join(':').split(' ').map(Number);
      return `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${rx}"/>`;
    }
    if (tag === 'l') {
      const [x1, y1, x2, y2] = rest.join(':').split(' ').map(Number);
      return `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}"/>`;
    }
    return '';
  }).join('');
  const strokeAttr = fill ? '' : ` stroke="${color}" stroke-width="2.05" stroke-linecap="round" stroke-linejoin="round"`;
  const fillAttr = fill ? ` fill="${color}"` : ' fill="none"';
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><g transform="translate(4 4) scale(8)"${strokeAttr}${fillAttr}>${inner}</g></svg>`;
  fs.writeFileSync(path.join(SRC_DIR, name + '.svg'), svg, 'utf8');
  console.log('生成 SVG:', name);
}

// Chrome headless 批量渲染
for (const [dir, name] of icons) {
  const src = path.join(SRC_DIR, name + '.svg');
  const outDir = path.join(OUT_DIR, dir);
  fs.mkdirSync(outDir, { recursive: true });
  const out = path.join(outDir, name + '.png');
  const fileUrl = 'file:///' + src.replace(/\\/g, '/');
  const cmd = `"${CHROME}" --headless --disable-gpu --screenshot="${out}" --window-size=200,200 --default-background-color=00000000 "${fileUrl}"`;
  try {
    execSync(cmd, { stdio: 'pipe', timeout: 30000 });
    console.log('渲染 PNG:', dir + '/' + name + '.png');
  } catch (e) {
    console.error('渲染失败:', name, e.message);
  }
}
console.log('完成');
// 补充：类型切换 active 白色图标
const extra = [
  ['charge', 'ic-bike-white', '#ffffff', false, [
    'c:5.5 17.5 3.5',
    'c:18.5 17.5 3.5',
    'p:M15 6h4l-2 6h-7l-2.5-4H4',
    'p:M9 10h4'
  ]],
  ['charge', 'ic-bolt-white', '#ffffff', false, [
    'p:M13 2L3 14h7l-1 8 10-12h-7l1-8z'
  ]],
  ['common', 'ic-moon-dark', '#334155', false, [
    'p:M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z'
  ]]
];
for (const [dir, name, color, fill, parts] of extra) {
  const inner = parts.map(part => {
    const [tag, ...rest] = part.split(':');
    if (tag === 'p') return `<path d="${rest.join(':')}"/>`;
    if (tag === 'c') { const [cx, cy, r] = rest.join(':').split(' ').map(Number); return `<circle cx="${cx}" cy="${cy}" r="${r}"/>`; }
    if (tag === 'r') { const [x, y, w, h, rx] = rest.join(':').split(' ').map(Number); return `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${rx}"/>`; }
    if (tag === 'l') { const [x1, y1, x2, y2] = rest.join(':').split(' ').map(Number); return `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}"/>`; }
    return '';
  }).join('');
  const strokeAttr = fill ? '' : ` stroke="${color}" stroke-width="2.05" stroke-linecap="round" stroke-linejoin="round"`;
  const fillAttr = fill ? ` fill="${color}"` : ' fill="none"';
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><g transform="translate(4 4) scale(8)"${strokeAttr}${fillAttr}>${inner}</g></svg>`;
  fs.writeFileSync(path.join(SRC_DIR, name + '.svg'), svg, 'utf8');
  const fileUrl = 'file:///' + path.join(SRC_DIR, name + '.svg').replace(/\\/g, '/');
  const out = path.join(OUT_DIR, dir, name + '.png');
  try {
    execSync(`"${CHROME}" --headless --disable-gpu --screenshot="${out}" --window-size=200,200 --default-background-color=00000000 "${fileUrl}"`, { stdio: 'pipe', timeout: 30000 });
    console.log('渲染 PNG:', dir + '/' + name + '.png');
  } catch (e) { console.error('渲染失败:', name, e.message); }
}
console.log('完成');
