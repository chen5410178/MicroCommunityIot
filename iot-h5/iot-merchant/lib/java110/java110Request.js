import {
	getDateYYYYMMDDHHMISS
} from './utils/DateUtil.js'

import url from '../../conf/url.js'
import {
	generatorSeq
} from './utils/SeqUtil.js'

import conf from '../../conf/config.js'
import {
	hasLogin,
	getToken
} from '@/api/user/sessionApi.js'
import {
	getWAppId
} from './init/initPage.js'

import {
	wechatRefreshToken
} from './auth/H5Login.js'

const W_APP_ID = "W_APP_ID"
/**
 * 获取请求头信息
 * add by 吴学文 QQ：928255095
 **/
export function getHeaders() {
	let _wAppId = uni.getStorageSync(W_APP_ID);
	let _token = getToken();
	if (!_token) {
		_token = "no login";
	}
	return {
		"app-id": conf.appId,
		"transaction-id": generatorSeq(),
		"req-time": getDateYYYYMMDDHHMISS(),
		"sign": '1234567',
		"user-id": '-1',
		"Accept": '*/*',
		"w-app-id": _wAppId,
		"Authorization": "Bearer " + _token
	};
}
/**
 * http 请求 加入是否登录判断
 */
export function request(_reqObj) {

	//这里判断只有在 post 方式时 放加载框
	if (_reqObj.hasOwnProperty("method") && "POST" == _reqObj.method) {
		uni.showLoading({
			title: '加载中',
			mask: true
		});
		_reqObj.complete = function() {
			uni.hideLoading();
		}
	}


	let _headers = getHeaders();
	if (_reqObj.hasOwnProperty('header')) {
		let _header = _reqObj.header;
		for (let key in _headers) {
			_header.key = _headers[key];
		}
	} else {
		_reqObj.header = _headers;
	}

	//白名单直接跳过检查登录
	if (url.NEED_NOT_LOGIN_URL.includes(_reqObj.url)) {
		_reqObj.communityId = mapping.HC_TEST_COMMUNITY_ID;
		uni.request(_reqObj);
		return;
	}
	//校验是否登录，如果没有登录跳转至温馨提示页面
	if (hasLogin()) {
		uni.request(_reqObj);
		return;
	}

	//先微信登录
	wechatRefreshToken();
}

/**
 * 
 * 不用鉴权的 HTTP 请求
 * @param {Object} _reqObj 请求参数
 */
export function requestNoAuth(_reqObj) {
	//这里判断只有在 post 方式时 放加载框
	if (_reqObj.hasOwnProperty("method") && "POST" == _reqObj.method) {
		uni.showLoading({
			title: '加载中',
			mask: true
		});
		_reqObj.complete = function() {
			uni.hideLoading();
		}
	}
	let _headers = getHeaders();
	if (_reqObj.hasOwnProperty('header')) {
		let _header = _reqObj.header;
		for (let key in _headers) {
			_header.key = _headers[key];
		}
	} else {
		_reqObj.header = _headers;
	}

	uni.request(_reqObj);

}