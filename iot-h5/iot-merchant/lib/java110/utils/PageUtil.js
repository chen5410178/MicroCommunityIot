import {isNull} from './StringUtil.js'
/**
 * 获取当前页面地址
 */
export function getCurrentUrl() {

	let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
	let curRoute = routes[routes.length - 1].route //获取当前页面路由
	return curRoute
}

/**
 * 获取当前页面地址和参数
 */
export function getCurrentUrlAndParam() {

	let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
	let curRoute = routes[routes.length - 1].route //获取当前页面路由
	let curParam = routes[routes.length - 1].options; //获取路由参数
	// 拼接参数
	let param = ''
	for (let key in curParam) {
		param += '&' + key + '=' + curParam[key]
	}
	if(isNull(param)){
		return curRoute;
	}
	return curRoute + "?" + param;
}

export function getH5Url(){
	return window.location.href;
}
