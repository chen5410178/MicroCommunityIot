/**
 * 页面初始化数据封装
 * add by 吴学文 2020-09-07
 * QQ 92825595
 * 
 */
import mapping from '../java110Mapping.js';

import {
	isNull,
	isNotNull
} from '../utils/StringUtil.js';


import {
	getTokenExpireTime
} from '../utils/DateUtil.js'

/**
 * 获取WAppId
 */
export function getWAppId() {
	let wAppId = null;
	//1.0 获取当前页面
	let routes = getCurrentPages();
	//在微信小程序或是app中，通过curPage.options；如果是H5，则需要curPage.$route.query（H5中的curPage.options为undefined，所以刚好就不需要条件编译了）
	let curParam = routes[routes.length - 1].options;
	//2.0 从当前页面参数中获取
	wAppId = curParam.wAppId;


	//4.0 判断磁盘是否存在
	if (isNull(wAppId)) {
		wAppId = uni.getStorageSync(mapping.W_APP_ID)
	}
	if (isNull(wAppId)) {
		uni.showToast({
			icon: 'none',
			title: '未获取到微信信息，请从微信中重新打开'
		});
		return ''
	}
	//5.0 存储至磁盘中
	uni.setStorageSync(mapping.W_APP_ID, wAppId)
	return wAppId;
}

/**
 * 每个页面去加载小区信息
 */
export function getCommunity() {
	let hcCommunityId = null;
	//1.0 获取当前页面
	let routes = getCurrentPages();
	//在微信小程序或是app中，通过curPage.options；如果是H5，则需要curPage.$route.query（H5中的curPage.options为undefined，所以刚好就不需要条件编译了）
	let curParam = routes[routes.length - 1].options;
	//2.0 从当前页面参数中获取
	hcCommunityId = curParam.hcCommunityId;


	//4.0 判断磁盘是否存在
	if (isNull(hcCommunityId)) {
		let _community = uni.getStorageSync(mapping.COMMUNITY);
		if(_community){
			hcCommunityId = JSON.parse(_community).communityId
		}
	}
	if (isNull(hcCommunityId)) {
		return ''
	}
	let _param = {
		communityId: hcCommunityId
	};
	//5.0 存储至磁盘中
	uni.setStorageSync(mapping.COMMUNITY, JSON.stringify(_param))
	return hcCommunityId;
}

/**
 * 每个页面去加载小区信息
 */
export function getMallFrom() {
	let mallFrom = null;
	//1.0 获取当前页面
	let routes = getCurrentPages();
	//在微信小程序或是app中，通过curPage.options；如果是H5，则需要curPage.$route.query（H5中的curPage.options为undefined，所以刚好就不需要条件编译了）
	let curParam = routes[routes.length - 1].options;
	//2.0 从当前页面参数中获取
	mallFrom = curParam.mallFrom;


	//4.0 判断磁盘是否存在
	if (isNull(mallFrom)) {
		mallFrom = uni.getStorageSync(mapping.MALL_FROM)
	}
	if (isNull(mallFrom)) {
		return ''
	}

	let _title = {
		action: "setTitle",
		title: document.title,
		curUrl: window.location.href
	}
	window.parent.postMessage(_title, '*');
	let _param = {
		mallFrom: mallFrom
	};
	//5.0 存储至磁盘中
	uni.setStorageSync(mapping.MALL_FROM, mallFrom)
	return mallFrom;
}

/**
 * 刷新商城的token
 * @param {Object} _hcCode hc智慧家园分配的code
 */
export function refreshToken(_hcCode) {
	return new Promise((resolve, reject) => {
		refreshUserByToken({
			hcCode: _hcCode
		}).then(_data => {
			let _userInfo = _data.data;
			wx.setStorageSync(mapping.USER_INFO, _userInfo);
			wx.setStorageSync(mapping.LOGIN_FLAG, {
				sessionKey: _userInfo.userId,
				expireTime: getTokenExpireTime()
			});
			wx.setStorageSync(mapping.TOKEN, _userInfo.token);
			console.log('........................_userInfo.token')
			resolve();
		}, _err => {
			console.log('刷新回话失败', _err);
			reject(_err);
		})
	})
}

/**
 * 查询用户信息
 */
export function getCurrentUser() {
	return wx.getStorageSync(mapping.USER_INFO);
}

/**
 * 页面加载方法
 * @param {Object} _option 页面参数对象
 */
export function onLoad(_option, callback = () => {}) {
	let _hcCode = _option.hcCode;
	
	let _hcMallCode = _option.hcMallCode
	//获取小区ID
	getCommunity();
	//获取来源
	getMallFrom();
	
	if(_option.hasOwnProperty("shopId")){
		uni.setStorageSync(mapping.MALL_SHOP_ID,_option.shopId);
	}
	
	//根据HC 刷新回话
	if (isNotNull(_hcCode)) {
		console.log('........................refreshToken')
		return refreshToken(_hcCode);
	}
	
	return new Promise((resolve, reject) => {
		resolve();
	})

}
