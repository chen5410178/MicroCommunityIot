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
		let obj = {
			tel: _tel
		};
		let msg = "";
		if (obj.tel == '') {
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
				_that.timer(_that);
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
export function ownerLogin(_tel) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.customCarInOut,
			method: "POST",
			data: _objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				resolve(res);
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
				if(_json.code != 0){
					reject(_json.msg);
					return;
				}
				resolve(res);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}