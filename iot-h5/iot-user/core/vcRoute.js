/**
 * 路由处理文件
 * 
 * add by 吴学文 QQ 928255095
 */

import url from '../conf/url.js'

import {
	debug
} from '../lib/java110/utils/LogUtil.js'






import conf from '../conf/config.js';

/*
 * 跳转功能封装
 * @param {Object} _param 跳转入参
 */
export function navigateTo(_param, callback = () => {}) {

	//参数中刷入wAppId 
	
	debug('vcRoute', 'navigateTo', _param);
	uni.navigateTo(_param);
	//校验是否登录，如果没有登录跳转至温馨提示页面
	// checkSession(_param.url, function() {
	// 	//有回话 跳转至相应页面
	// 	uni.navigateTo(_param);
	// })
};





/**
 * 返回上层页面
 */
export function navigateBack() {
	uni.navigateBack({
		delta: 1
	});
}
