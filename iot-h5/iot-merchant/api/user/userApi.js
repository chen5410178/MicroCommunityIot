import util from '../../lib/java110/utils/util.js';
import {
	request,
	requestNoAuth
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';
import {saveLoginFlag } from './sessionApi.js'
const LOGIN_FLAG = 'loginFlag'; //登录标识
export function login(userName, passwd) {

	let userInfo = {
		username: userName,
		passwd: passwd
	}
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.loginUrl,
			method: "POST",
			data: userInfo,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg);
					return;
				}
				let data = res.data;
				let _userInfo = {
					userName:data.name,
					userTel:data.tel,
					userId:data.userId,
					address:data.address,
				};
				uni.setStorageSync("userInfo", _userInfo);
				saveLoginFlag(data.userId,data.token)
				resolve();
			},
			fail: function(error) {
				// 调用服务端登录接口失败
				reject(error);
			}
		});
	});
}

export function getUserId(){
	let userInfo = uni.getStorageSync("userInfo");
	if(userInfo){
		return userInfo.userId;
	}
	return "";
}

export function getUserName(){
	let userInfo = uni.getStorageSync("userInfo");
	if(userInfo){
		return userInfo.userName;
	}
	return "";
}

export function getUserTel(){
	let userInfo = uni.getStorageSync("userInfo");
	if(userInfo){
		return userInfo.userTel;
	}
	return "";
}

