import { getPageUrl } from './UrlUtil.js'
import { isEmpty } from './StringUtil.js'

// 回跳地址存储键（登录/支付成功后回跳的业务页地址）
const TARGET_URL = 'targetUrl'

// tab 页清单（uni.navigateTo/redirectTo 不能跳，必须 switchTab）
const TAB_PAGES = ['pages/index/index', 'pages/index/func', 'pages/index/my', 'pages/charge/scanCharge']

// 自身页面清单（回跳命中则丢弃，防止死循环）
const SELF_PAGES = ['pages/user/login', 'pages/user/register', 'pages/user/forgotPwd', 'pages/user/changePwd']

/**
 * 写入回跳地址（业务页在即将离开去登录/支付时调用）
 * @param {Object} _url 页面地址
 */
export function saveTargetUrl(_url) {
	if (isEmpty(_url)) {
		return
	}
	uni.setStorageSync(TARGET_URL, _url)
}

/**
 * 读取并立即删除（一次性消费），无值返回 ''
 */
export function takeTargetUrl() {
	let _url = uni.getStorageSync(TARGET_URL)
	if (isEmpty(_url)) {
		return ''
	}
	uni.removeStorageSync(TARGET_URL)
	return _url
}

/**
 * 清理回跳地址（仅用于兜底/异常分支）
 */
export function removeTargetUrl() {
	uni.removeStorageSync(TARGET_URL)
}

/**
 * 组装当前页地址（含 query 参数）
 */
export function buildCurrentPageUrl() {
	return getPageUrl()
}

/**
 * 统一回跳：目标为空回 _fallbackUrl；目标为 tab 页走 switchTab；否则 redirectTo
 * @param {Object} _targetUrl 回跳地址
 * @param {Object} _fallbackUrl 兜底地址，默认首页
 */
export function navigateToTargetUrl(_targetUrl, _fallbackUrl) {
	let _fallback = _fallbackUrl || '/pages/index/index'
	let _url = _filterTargetUrl(_targetUrl)
	if (isEmpty(_url)) {
		_goPage(_fallback)
		return
	}
	if (_isTabPage(_url)) {
		uni.switchTab({
			url: _pathOf(_url),
			fail: function() {
				// 未注册为 tabBar 页时退化为普通跳转
				uni.redirectTo({
					url: _url,
					fail: function() {
						_goPage(_fallback)
					}
				})
			}
		})
		return
	}
	uni.redirectTo({
		url: _url,
		fail: function() {
			_goPage(_fallback)
		}
	})
}

function _filterTargetUrl(_targetUrl) {
	if (isEmpty(_targetUrl)) {
		return ''
	}
	let _url = String(_targetUrl).trim()
	if (isEmpty(_url)) {
		return ''
	}
	if (SELF_PAGES.indexOf(_pathOf(_url)) > -1) {
		return ''
	}
	return _url
}

function _pathOf(_url) {
	let _path = String(_url).split('?')[0]
	if (_path.charAt(0) != '/') {
		_path = '/' + _path
	}
	return _path
}

function _isTabPage(_url) {
	if (isEmpty(_url)) {
		return false
	}
	return TAB_PAGES.indexOf(_pathOf(_url).substr(1)) > -1
}

function _goPage(_url) {
	if (_isTabPage(_url)) {
		uni.switchTab({
			url: _pathOf(_url)
		})
		return
	}
	uni.redirectTo({
		url: _url
	})
}
