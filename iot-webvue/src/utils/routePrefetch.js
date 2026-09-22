const prefetched = {}

const PREFETCH_BLACKLIST = [
  /^\/pages\/3d\//,
  /Statistics/
]

export function normalizeMenuPath(href) {
  return href ? String(href).replace(/^\/?#\//, '/') : ''
}

export function isPrefetchAllowed(path) {
  if (!path) return false
  return !PREFETCH_BLACKLIST.some(reg => reg.test(path))
}

export function collectMenuPaths(menus) {
  const paths = []
  ;(menus || []).forEach(menu => {
    ;(menu.childs || []).forEach(sub => {
      if (sub.isShow !== 'Y') return
      const path = normalizeMenuPath(sub.href)
      if (!isPrefetchAllowed(path)) return
      if (paths.indexOf(path) === -1) {
        paths.push(path)
      }
    })
  })
  return paths
}

export function prefetchRoutePath(router, path) {
  if (!router || !isPrefetchAllowed(path) || prefetched[path]) return
  let resolved
  try {
    resolved = router.resolve(path)
  } catch (e) {
    return
  }
  const matched = (resolved && resolved.resolved && resolved.resolved.matched) || []
  const record = matched[matched.length - 1]
  const comp = record && record.components && record.components.default
  if (typeof comp === 'function') {
    prefetched[path] = true
    comp().catch(function () { prefetched[path] = false })
  }
}

export function prefetchOnIdle(router, paths, options) {
  if (!router) return
  const opts = options || {}
  const budget = opts.budget || 6
  const list = (paths || []).filter(isPrefetchAllowed).slice(0, budget)
  if (list.length === 0) return
  const idle = window.requestIdleCallback || function (cb) { return setTimeout(cb, 600) }
  let index = 0
  const step = function () {
    if (index >= list.length) return
    prefetchRoutePath(router, list[index])
    index++
    idle(step)
  }
  idle(step)
}
