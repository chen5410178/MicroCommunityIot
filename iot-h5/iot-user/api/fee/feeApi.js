import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';

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