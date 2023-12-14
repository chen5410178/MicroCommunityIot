import url from '../../constant/url.js'

export function queryShopCoupons(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.listShopParkingCoupon,
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
		_that.context.post({
			url: url.saveParkingCouponCar,
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
		_that.context.get({
			url: url.listParkingCouponCar,
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