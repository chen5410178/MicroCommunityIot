import url from '../../conf/url.js';
import {
	request,
	requestNoAuth
} from '../../lib/java110/java110Request.js';

export function queryShopCoupons(_that,_data){
	return new Promise(function(reslove,reject){
		request({
			url: url.listShopParkingCoupon,
			method: "GET",
			data:_data,
			success: function(res) {
				reslove(res.data);
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

export function saveCustomCoupon(_data,_that){
	return new Promise(function(reslove,reject){
		request({
			url: url.saveParkingCouponCar,
			method: "POST",
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
				}else{
					reject(res.data.msg);
				}
			},
			fail: function(e) {
				reject('网络异常');
			}
		})
	});
}




export function queryShopCouponCars(_that,_data){
	return new Promise(function(reslove,reject){
		request({
			url: url.listParkingCouponCar,
			method: "GET",
			data:_data,
			success: function(res) {
				reslove(res.data);
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

export function receiveParkingCoupon(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.saveParkingCouponCar,
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
