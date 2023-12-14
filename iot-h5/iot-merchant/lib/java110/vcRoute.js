/**
 * 路由处理文件
 * 
 * add by 吴学文 QQ 928255095
 */

import {
	debug
} from './utils/LogUtil.js'

import mapping from './java110Mapping.js'




// 页面初始化相关


/*
 * 跳转功能封装
 * @param {Object} _param 跳转入参
 */
export function navigateTo(_param, _iframe) {

	//是否来自于HC的嵌套
	let _mallFrom = uni.getStorageSync(mapping.MALL_FROM)

	if (!_iframe || !_mallFrom) {
		uni.navigateTo(_param);
		return;
	}
	_param.action = 'navigateTo';
	window.parent.postMessage(_param, '*');
};

/*
 * 跳转功能封装
 * @param {Object} _param 跳转入参
 */
export function navigateHome(_param) {
	//是否来自于HC的嵌套
	let _mallFrom = uni.getStorageSync(mapping.MALL_FROM)
	if (!_mallFrom) {
		uni.navigateTo(_param);
		return;
	}
	_param.action = 'navigateHome';
	window.parent.postMessage(_param, '*');
};

/**
 * 返回上层页面
 */
export function navigateBack(_iframe) {

	//是否来自于HC的嵌套
	let _mallFrom = uni.getStorageSync(mapping.MALL_FROM)

	if (!_iframe || !_mallFrom) {
		uni.navigateBack({
			delta: 1
		});
		return;
	}
	let _param = {};
	_param.action = 'navigateBack';
	window.parent.postMessage(_param, '*');
}
