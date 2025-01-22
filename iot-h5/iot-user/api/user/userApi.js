import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';


export function timer(_that) {
	let promise = new Promise((resolve, reject) => {
		let setTimer = setInterval(
			() => {
				let second = _that.second - 1;
				_that.second = second;
				_that.btnValue = second + '秒';
				_that.btnDisabled = true;
				if (_that.second <= 0) {
					_that.second = 60;
					_that.btnValue = '获取验证码';
					_that.btnDisabled = false;
					resolve(setTimer)
				}
			}, 1000)
	})
	promise.then((setTimer) => {
		clearInterval(setTimer)
	})
}

/**
 * 手机发送验证码 

 * @param {Object} _tel 手机号
 */
export function sendSmsCode(_tel, _that) {
	return new Promise((resolve, reject) => {
		let _objData = {
			tel: _tel
		};
		let msg = "";
		if (_objData.tel == '') {
			msg = '请输入手机号';
		}
		if (msg != "") {
			wx.showToast({
				title: msg,
				icon: 'none',
				duration: 2000
			});
			reject(msg);
			return;
		}
		requestNoAuth({
			url: url.userSendSms,
			method: "POST",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				wx.showToast({
					title: res.data,
					icon: 'none',
					duration: 3000
				});
				_that.codeMsg = res.data;
				timer(_that);
				resolve(res);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}

/**
 * 业主登陆

 * @param {Object} _tel
 */
export function ownerLogin(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.appUserLogin,
			method: "POST",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json = res.data;
				if (_json.code != 0) {
					reject(_json.msg);
					return;
				}
				uni.setStorageSync("userInfo",_json.data);
				resolve(_json.data);
			},
			fail: function(res) {
				reject(res);
			}
		});

	});

}
/**
 * 退出登录
 */
export function userLogout(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.userLogout,
			method: "POST",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				// let _json = res.data;
				// if (_json.code != 0) {
				// 	reject(_json.msg);
				// 	return;
				// }
				uni.removeStorageSync("userInfo");
				resolve();
			},
			fail: function(res) {
				reject(res);
			}
		});
	
	})
}
/**
 * 用户注册

 * @param {Object} _tel 手机号
 */
export function userRegister(_data) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.appUserRegister,
			method: "POST",
			data: _data,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json  = res.data;
				uni.showToast({
					icon:'none',
					title:_json.msg
				});
				if(_json.code != 0){
					reject(_json.msg);
					return;
				}
				resolve(res.data);
			},
			fail: function(res) {
				uni.showToast({
					icon:'none',
					title:res
				});
				reject(res);
			}
		});
	})
}

export function getUserId(){
	let _userInfo = uni.getStorageSync('userInfo');
	return _userInfo.userId;
}

export function getUserName(){
	let _userInfo = uni.getStorageSync('userInfo');
	return _userInfo.name;
}

export function getUserTel(){
	let _userInfo = uni.getStorageSync('userInfo');
	return _userInfo.tel;
}

export function getCommunityWechatAppId(_objData){
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.getCommunityWechatAppId,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					resolve(res.data);
					return;
				}
				reject();
			},
			fail: function(e) {
				reject();
			}
		});
	})
}

export function queryOwnerAccount(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.queryAppUserAccount,
			method: "GET",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json = res.data;
				if (_json.code != 0) {
					reject(_json.msg);
					return;
				}
				resolve(_json.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	
	})
}

/**
 * 刷新用户
 * @param {Object} _objData
 */
export function refreshUserOpenId(_objData){
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.refreshOpenId,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					resolve(res.data);
					return;
				}
				reject();
			},
			fail: function(e) {
				reject();
			}
		});
	})
}

export function getWechatMiniOpenId(_objData){
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.getWechatMiniOpenId,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					resolve(res.data);
					return;
				}
				reject();
			},
			fail: function(e) {
				reject();
			}
		});
	})
}

// 异步上传图片
export function uploadImageAsync(_objData,_that) {
	return new Promise( (resolve, reject) => {
		requestNoAuth({
			url: url.uploadImage,
			data: _objData,
			method: "POST",
			//动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					let imgInfo = res.data;
					resolve(imgInfo);
				}
			},
			fail: function(e) {
				uni.hideLoading();
				uni.showToast({
					title: "服务器异常了",
					icon: 'none'
				})
			}
		});
	})
}

export function refreshAppUserBindingOwnerOpenId(_that, _data) {
	uni.showLoading({
		title: '加载中',
		mask: true
	});
	return new Promise(
		(resolve, reject) => {
			requestNoAuth({
				url: url.refreshAppUserBindingOwnerOpenId,
				method: "POST",
				data: _data,
				//动态数据
				success: function(res) {
					uni.hideLoading();
					let _json = res.data;
					if (_json.code != 0) {
						reject(_json.msg);
						return;
					}
					resolve(_json);
				},
				fail: function(e) {
					uni.hideLoading();
					reject(e);
				}
			});
		})
}

export function getUserChargeCar(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.getUserChargeCar,
			method: "GET",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json = res.data;
				if (_json.code != 0) {
					reject(_json.msg);
					return;
				}
				resolve(_json);
			},
			fail: function(res) {
				reject(res);
			}
		});
	
	})
}

export function saveUserChargeCar(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.saveUserChargeCar,
			method: "POST",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json = res.data;
				if (_json.code != 0) {
					reject(_json.msg);
					return;
				}
				resolve();
			},
			fail: function(res) {
				reject(res);
			}
		});
	
	})
}