const fs = require('fs')
const path = require('path')
const zlib = require('zlib')

const distDir = path.resolve(__dirname, '..', 'dist')
const indexFile = path.join(distDir, 'index.html')

function walk(dir, list) {
  if (!fs.existsSync(dir)) return list
  fs.readdirSync(dir).forEach(name => {
    const full = path.join(dir, name)
    const stat = fs.statSync(full)
    if (stat.isDirectory()) {
      walk(full, list)
    } else {
      list.push({ file: full, size: stat.size })
    }
  })
  return list
}

function formatSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / 1024 / 1024).toFixed(2) + ' MB'
}

function groupOf(relPath) {
  if (/\.js\.gz$/.test(relPath)) return 'js.gz'
  if (/\.js\.br$/.test(relPath)) return 'js.br'
  if (/\.css\.gz$/.test(relPath)) return 'css.gz'
  if (/\.css\.br$/.test(relPath)) return 'css.br'
  if (/\.js\.map$/.test(relPath)) return 'js.map'
  if (/\.css\.map$/.test(relPath)) return 'css.map'
  const ext = path.extname(relPath).replace('.', '')
  return ext || 'other'
}

function main() {
  const files = walk(distDir, [])
  if (files.length === 0) {
    console.log('dist 目录不存在或为空，请先执行 npm run build')
    process.exit(1)
  }

  const relFiles = files.map(item => ({
    rel: path.relative(distDir, item.file).split(path.sep).join('/'),
    size: item.size,
    file: item.file
  }))

  const byGroup = {}
  let totalSize = 0
  relFiles.forEach(item => {
    const group = groupOf(item.rel)
    if (!byGroup[group]) byGroup[group] = { count: 0, size: 0 }
    byGroup[group].count++
    byGroup[group].size += item.size
    totalSize += item.size
  })

  console.log('==== iot-webvue dist 分析 ====')
  console.log('dist 路径: ' + distDir)
  console.log('文件总数: ' + relFiles.length + '，总体积: ' + formatSize(totalSize))
  console.log('')
  console.log('-- 按类型统计 --')
  Object.keys(byGroup).sort().forEach(group => {
    console.log('  ' + group + ': ' + byGroup[group].count + ' 个, ' + formatSize(byGroup[group].size))
  })

  const mapCount = relFiles.filter(item => /\.map$/.test(item.rel)).length
  console.log('')
  console.log('-- sourceMap --')
  console.log('  .map 文件数: ' + mapCount + (mapCount === 0 ? ' (已关闭 sourceMap)' : ''))

  console.log('')
  console.log('-- 首屏外链脚本 --')
  if (!fs.existsSync(indexFile)) {
    console.log('  index.html 不存在')
  } else {
    const html = fs.readFileSync(indexFile, 'utf8')
    const scripts = html.match(/<script[^>]*src=["'][^"']+["'][^>]*>/g) || []
    const external = scripts.filter(tag => /src=["'](https?:)?\/\//.test(tag))
    const nonBundle = scripts.filter(tag => {
      const matched = tag.match(/src=["']([^"']+)["']/)
      const src = matched ? matched[1] : ''
      return !/^\/js\/[^/]+$/.test(src)
    })
    console.log('  index.html script 标签总数: ' + scripts.length)
    console.log('  外链(外部域名)脚本数: ' + external.length)
    console.log('  非构建产物同步脚本数(首屏外链脚本): ' + nonBundle.length)
    nonBundle.forEach(tag => console.log('    ' + tag))
    external.forEach(tag => console.log('    ' + tag))
    const deferScripts = scripts.filter(tag => !/src=["'](https?:)?\/\//.test(tag))
    console.log('  本地首屏脚本:')
    deferScripts.forEach(tag => {
      const matched = tag.match(/src=["']([^"']+)["']/)
      const src = matched ? matched[1] : ''
      const local = path.join(distDir, src.replace(/^\//, ''))
      let info = ''
      if (fs.existsSync(local)) {
        info = formatSize(fs.statSync(local).size)
        if (fs.existsSync(local + '.gz')) {
          info += ' (gzip ' + formatSize(fs.statSync(local + '.gz').size) + ')'
        }
      }
      console.log('    ' + src + '  ' + info)
    })
    const cssLinks = html.match(/<link[^>]*href=["'][^"']+\.css["'][^>]*>/g) || []
    console.log('  首屏样式:')
    cssLinks.forEach(tag => {
      const matched = tag.match(/href=["']([^"']+)["']/)
      const href = matched ? matched[1] : ''
      const local = path.join(distDir, href.replace(/^\//, ''))
      let info = ''
      if (fs.existsSync(local)) {
        info = formatSize(fs.statSync(local).size)
        if (fs.existsSync(local + '.gz')) {
          info += ' (gzip ' + formatSize(fs.statSync(local + '.gz').size) + ')'
        }
      }
      console.log('    ' + href + '  ' + info)
    })
  }

  console.log('')
  console.log('-- chunk 名称分布 --')
  const jsFiles = relFiles.filter(item => /^js\/.*\.js$/.test(item.rel))
  const named = jsFiles.filter(item => !/\/\d+\.[0-9a-f]+\.js$/.test(item.rel))
  const unnamed = jsFiles.length - named.length
  console.log('  js chunk 总数: ' + jsFiles.length + '，命名 chunk: ' + named.length + '，匿名(数字) chunk: ' + unnamed)
  console.log('  重点 chunk:')
  jsFiles.filter(item => /chunk-(vendors|elementUI|echarts|three|editors|qrcode)|runtime|^js\/(app|dashboard-|page-index)/.test(item.rel))
    .sort((a, b) => b.size - a.size)
    .forEach(item => {
      let info = formatSize(item.size)
      if (fs.existsSync(item.file + '.gz')) {
        info += ' (gzip ' + formatSize(fs.statSync(item.file + '.gz').size) + ')'
      }
      console.log('    ' + item.rel + '  ' + info)
    })

  console.log('')
  console.log('-- 首屏关键资源体积估算(index.html 引用的本地 js) --')
  if (fs.existsSync(indexFile)) {
    const html = fs.readFileSync(indexFile, 'utf8')
    const srcs = (html.match(/<script[^>]*src=["'][^"']+["'][^>]*>/g) || [])
      .map(tag => (tag.match(/src=["']([^"']+)["']/) || [])[1] || '')
      .filter(src => src && !/^(https?:)?\/\//.test(src))
    let raw = 0
    let gzip = 0
    srcs.forEach(src => {
      const local = path.join(distDir, src.replace(/^\//, ''))
      if (!fs.existsSync(local)) return
      raw += fs.statSync(local).size
      if (fs.existsSync(local + '.gz')) {
        gzip += fs.statSync(local + '.gz').size
      } else {
        gzip += zlib.gzipSync(fs.readFileSync(local)).length
      }
    })
    console.log('  首屏 JS 原始体积: ' + formatSize(raw) + '，gzip 后: ' + formatSize(gzip))
  }
}

main()
