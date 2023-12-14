import mapping from '../java110Mapping.js'
import {
	request
} from '../java110Request.js'
import conf from '../../../conf/config.js'


import {
	isNull,
	isNotNull,
	isEmpty
} from '../utils/StringUtil.js';

import {
	getH5Url
} from '../utils/PageUtil.js'

const HAS_USER_OPEN_ID = conf.baseUrl + 'app/userAuth/checkUserHasOpenId';




/**
 * 检查会话是否 存在
 * add by 吴学文 QQ:928255095
 */
export function checkSession() {
	return new Promise((resolve, reject) => {
		let _that = this;
		let loginFlag = wx.getStorageSync(mapping.LOGIN_FLAG);
		let nowDate = new Date();
		//判断如果是APP
		// #ifdef APP-PLUS
		if (loginFlag && loginFlag.expireTime > nowDate.getTime()) {
			resolve();
		} else {
			// 无登录态
			reject();
			return;
		}
		// #endif
		//判断如果是H5
		// #ifdef H5
		if (loginFlag && loginFlag.expireTime > nowDate.getTime()) {
			resolve();
		} else {
			reject();
			return;
		}
		// #endif

		// #ifdef MP-WEIXIN
		if (loginFlag && loginFlag.expireTime > nowDate.getTime()) {
			// 检查 session_key 是否过期
			wx.checkSession({
				// session_key 有效(为过期)
				success: function() {

					resolve();
				},
				// session_key 过期
				fail: function() {
					// session_key过期
					reject();
				}
			});
		} else {
			// 无登录态
			reject();
		}
		// #endif
	});
}

/**
 * 是否登录 跳转鉴权
 * 
 * add by 吴学文 QQ:928255095
 */
export function loginAuth(options,_url) {
	
	//判断当前登录状态，不调跳转登录界面
	let loginFlag = uni.getStorageSync(mapping.LOGIN_FLAG);
	let nowDate = new Date();
	if (loginFlag && loginFlag.expireTime > nowDate.getTime()) {
		return;
	}
	//待hcCode 不去鉴权
	console.log('options',options)
	if(!isEmpty(options) && options.hasOwnProperty("hcCode")){
		return;
	}

	let _mallFrom = uni.getStorageSync(mapping.MALL_FROM);

	if (isNull(_url)) {
		_url = getH5Url();
	}


	if (_mallFrom) {
		let _param = {
			action: 'refreshToken',
			url: _url
		}
		window.parent.postMessage(_param, '*');
		throw new Error('去鉴权了')
	}
	//这里后期 考虑 不是内嵌的情况  
	uni.navigateTo({
		url:'/pages/public/login'
	})
	throw new Error('没有登录 请先登录');
}
/**
 * 是否登录 不跳转 只是判断是否登录
 * 
 * add by 吴学文 QQ:928255095
 */
export function hasLogin() {
	//判断当前登录状态，不调跳转登录界面
	let loginFlag = uni.getStorageSync(mapping.LOGIN_FLAG);
	let nowDate = new Date();
	if (loginFlag && loginFlag.expireTime > nowDate.getTime()) {
		return true;
	}
	return false;
}

/**
 * 刷新openId 功能
 */
export function refreshWechatOpenId(_successUrl, _errorUrl) {
	let _mallFrom = uni.getStorageSync(mapping.MALL_FROM);

	let _userInfo = uni.getStorageSync(mapping.USER_INFO);

	if (mapping.MALL_FROM_MINI == _mallFrom) {
		//这里写小程序的东西
		return;
	} else if (mapping.MALL_FROM_APP == _mallFrom) {
		//这里写APP的东西
		return;
	}
	let _isFrame = "N"
	if(_mallFrom){
		_isFrame = "Y";
	}
	let _openUrl = '';
	refreshOpenId({
		userId: _userInfo.userId,
		redirectUrl: _successUrl,
		errorUrl: _errorUrl,
		isFrame:_isFrame
	}).then(data => {
		_openUrl = data.openUrl;
		if (isNull(_mallFrom)) {
			window.location.href = _openUrl;
			return;
		}
		let _param = {
			action: 'navigateToPage',
			url: _openUrl
		}
		window.parent.postMessage(_param, '*');
	}, err => {
		uni.showToast({
			icon: 'none',
			title: '微信鉴权失败'
		})
	})
}

/**
 * 检查是否有open Id 
 */
export function hasUserOpenId(_rediectUrl) {
	if (isNull(_rediectUrl)) {
		_rediectUrl = getH5Url();
	}
	request({
		url: HAS_USER_OPEN_ID,
		method: 'get',
		data: {
			fromUrl: 'hc mall'
		},
		success: function(res) {
			let _param = res.data;
			if (_param.code == 0) {
				return;
			}
			//跳转 至鉴权页面
			refreshWechatOpenId(_rediectUrl);
		},
		fail: function(error) {
			// 调用服务端登录接口失败
			if (error.statusCode == 401) {
				return;
			}
			//跳转 至鉴权页面
			refreshWechatOpenId(_rediectUrl);
		}
	});
}
