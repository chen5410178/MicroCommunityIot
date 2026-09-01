/**
 * 自登陆处理js
 * 
 */
import {
	request
} from '../../lib/java110/java110Request.js'
import {
	requestNoAuth
} from '../../lib/java110/java110Request.js';
import url from '@/conf/url.js';
import {
	refreshUserOpenId,
	getWechatMiniOpenId,
	getCommunityWechatAppId,
} from '../../api/user/userApi.js';

import {
	getCommunityId
} from '../../api/community/communityApi.js';

import {
	saveOwnerStorage,
	saveUserLoginInfo,
	removeUserLoginInfo,
	getWAppId,
	saveWAppId,
	getLoginFlag
} from '@/lib/java110/utils/StorageUtil.js';

import {
	isWxOrAli
} from '../../lib/java110/utils/EnvUtil.js';

const LOGIN_FLAG = 'loginFlag'; //登录标识

/**
 * 是否 登录
 */
export function hasLogin() {
	let loginFlag = uni.getStorageSync(LOGIN_FLAG);
	let nowDate = new Date();
	if (loginFlag && loginFlag.expireTime && loginFlag.expireTime > nowDate.getTime()) {
		return true;
	} else {
		return false;
	}
};


/**
 * 自登陆
 * @param {Object} _objData
 */
export function autoLogin(options) {
	//todo 检查登录信息是否过期

	if (hasLogin()) {
		return;
	}
	let _openId = options.openId;
	if (_openId) { //h5 自登陆的情况
		loginByOpenId(_openId);
		return;
	}

	//todo 如果参数中带了wAppId
	let _wAppId = options.wAppId;

	if (_wAppId) {
		_generatorOpenId(options, _wAppId);
		return;
	}
	let _communityId = getCommunityId();
	if (options.communityId) {
		_communityId = options.communityId;
	}
	let _objType = "1100"; // todo public
	getCommunityWechatAppId({
		communityId: _communityId,
		objType: _objType
	}).then(_data => {
		if (!_data.data || _data.data.indexOf('wx') < 0) {
			return;
		}
		_generatorOpenId(options, _data.data);
	})



}

export async function autoLoginMini(options) {
	return new Promise((resolve, reject) => {
		if (hasLogin()) {
			resolve();
			return;
		}

		let _communityId = getCommunityId();
		if (options.communityId) {
			_communityId = options.communityId;
		}

		getCommunityWechatAppId({
			communityId: _communityId,
			objType: "1000"
		}).then(_data => {
			if (!_data.data || _data.data.indexOf('wx') < 0) {
				reject('未配置微信小程序');
				return;
			}
			return uni.login();
		},_err=>{
			reject(_err);
		}).then(loginRes => {
			console.log(loginRes[1])
			if (!loginRes[1].code) {
				reject('登录失败');
				return;
			}
			let accountInfo = uni.getAccountInfoSync();
			let appId = accountInfo.miniProgram.appId;

			return getWechatMiniOpenId({
				code: loginRes[1].code,
				appId: appId,
			});
		},_err=>{
			reject(_err);
		}).then(_data => {
			if (_data.code != 0) {
				uni.showToast({
					icon: 'none',
					title: _data.msg
				});
				reject(_data.msg);
				return;
			}
			return loginByOpenId(_data.data);
		},err=>{
			reject(err);
		}).then(_data=>{
			console.log(_data);
			resolve(_data);
		},err=>{
			reject(err);
		})
	});
}



export function _generatorOpenId(options, appId) {
	if (isWxOrAli() != 'WECHAT') {
		return;
	}
	let _redirectUrl = window.location.href;
	refreshUserOpenId({
		redirectUrl: _redirectUrl,
		wAppId: appId,
	}).then(_data => {
		if (_data.code == 0) {
			window.location.href = _data.data.openUrl;
			return;
		}
	});
}

export function loginByOpenId(openId) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.ownerUserLoginByOpenId,
			method: "POST",
			data: {
				openId: openId
			},
			//动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code != 0) {
					reject(_json.msg);
					return;
				}
				//todo 保存业主信息
				uni.setStorageSync("userInfo", _json.data);
				uni.setStorageSync("currentCommunityInfo", {
					communityId: _json.data.communityId,
					communityName: _json.data.communityName,
				});
				saveUserLoginInfo(_json.data.userId, _json.data.token, _json.data.key);
				resolve('登陆成功');
			},
			fail: function(e) {
				uni.hideLoading();
				console.error(e);
				reject('登陆失败');
			}
		});
	});
}