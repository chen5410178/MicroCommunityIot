const loaded = {}

export function loadScriptOnce(url) {
  if (loaded[url]) return loaded[url]
  loaded[url] = new Promise(function (resolve, reject) {
    const script = document.createElement('script')
    script.src = url
    script.async = true
    script.onload = function () { resolve(url) }
    script.onerror = function () {
      loaded[url] = null
      reject(new Error('脚本加载失败: ' + url))
    }
    document.head.appendChild(script)
  })
  return loaded[url]
}

export function loadStyleOnce(url) {
  if (loaded[url]) return loaded[url]
  loaded[url] = new Promise(function (resolve, reject) {
    const link = document.createElement('link')
    link.rel = 'stylesheet'
    link.href = url
    link.onload = function () { resolve(url) }
    link.onerror = function () {
      loaded[url] = null
      reject(new Error('样式加载失败: ' + url))
    }
    document.head.appendChild(link)
  })
  return loaded[url]
}

export const TENCENT_MAP_URL = 'https://map.qq.com/api/gljs?v=1.exp&key=ZUXBZ-7F5AU-WLXV6-GRX62-RZ6P7-XOB3N'
export const JESSIBUCA_URL = '/js/jessibuca/jessibuca.js'
