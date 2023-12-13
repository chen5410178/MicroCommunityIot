import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';
// #ifdef H5
const WexinPayFactory = require('../../factory/WexinPayFactory.js');
// #endif
export function getCouponUsers(_objData, _couponUsers) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryCouponUser,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					//成功情况下跳转
					_couponUsers = res.data;
					if (_couponUsers.length < 1) {
						//_that.noData = true;
						reject();
					}
					resolve(_couponUsers);
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


/**
 * 查询停车费
 * @param {Object} _objData 费用
 */
export function getTempCarFeeOrder(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.getTempCarFeeOrder,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					//成功情况下跳转
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

export function toPayTempCarFee(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.cashier,
			method: "POST",
			data: JSON.stringify(_objData), //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					//成功情况下跳转
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


export function toAliPayTempCarFee(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.alipayPayTempCarFee,
			method: "POST",
			data: JSON.stringify(_objData), //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					//成功情况下跳转
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
// 查询停车劵
export function getParkingCarCoupon(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listParkingCouponCar,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					//成功情况下跳转
					resolve(_json.data);
					return;
				}
				reject(_json.msg);
			},
			fail: function(e) {
				reject();
			}
		});
	})
}

/**
 * 收银台 支付
 * @param {Object} _that
 * @param {Object} _data
 * @param {Object} _successUrl
 */
export function cashierPayFee(_that, _data,_successUrl) {
	if(!_successUrl ){
		_successUrl = "/pages/successPage/successPage?msg=支付成功&objType=3003";
	}
	wx.showLoading({
		title: '支付中'
	});
	requestNoAuth({
		url: url.cashier,
		method: "POST",
		data: _data,
		//动态数据
		success: function(res) {
			wx.hideLoading();
			if (res.data.code == '0') {
				let data = res.data; //成功情况下跳转
				// #ifdef MP-WEIXIN
				uni.requestPayment({
					'timeStamp': data.timeStamp,
					'nonceStr': data.nonceStr,
					'package': data.package,
					'signType': data.signType,
					'paySign': data.sign,
					'success': function(res) {
						uni.navigateTo({
							url: _successUrl
						})
					},
					'fail': function(res) {
						console.log('fail:' + JSON.stringify(res));
					}
				});
				// #endif
				// #ifdef H5
				WexinPayFactory.wexinPay(data, function() {
					uni.navigateTo({
						url: _successUrl
					})
				});
				// #endif

				return;
			}
			if (res.statusCode == 200 && res.data.code == '100') {
				let data = res.data; //成功情况下跳转
				uni.showToast({
					title: "支付成功",
					duration: 2000
				});
				setTimeout(function() {
					uni.navigateTo({
						url: _successUrl
					})
				}, 2000)

				return;
			}
			wx.showToast({
				title: "缴费失败"+res.data.msg,
				icon: 'none',
				duration: 2000
			});
		},
		fail: function(e) {
			wx.hideLoading();
			wx.showToast({
				title: "服务器异常了",
				icon: 'none',
				duration: 2000
			});
		}
	});

}