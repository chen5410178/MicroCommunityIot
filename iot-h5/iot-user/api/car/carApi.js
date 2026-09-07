/**
 * 车辆信息处理 api 接口类
 * 
 * add by 吴学文 QQ 928255095
 */


import {
	request,requestNoAuth
} from '../../lib/java110/java110Request.js'
import
url
from '../../conf/url.js'


/**
 * 查询业主车辆
 */
export function getCars(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.queryParkingSpacesByOwner,
			method: "GET",
			data:_objData,
			success: function(res) {
				if (res.statusCode == 200) {
					//将业主信息和房屋信息一起返回
					let _parkingSpaces = res.data.parkingSpaces;
					if (_parkingSpaces.length == 0) {
						reject("业主没有房屋信息");
						return;
					}
					resolve(_parkingSpaces);
				} else {
					reject("查询车辆失败");
				}
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};


export function queryWaitPayFeeTempCar(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryWaitPayFeeTempCar,
			method: "GET",
			data:_objData,
			success: function(res) {
				if (res.statusCode == 200) {
					//将业主信息和房屋信息一起返回
					resolve(res.data);
				} else {
					reject("查询车辆失败");
				}
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};
/**
 * 保存业主车辆
 */
export function customCarInOut(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.customCarInOut,
			method: "POST",
			data:_objData,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				resolve(res);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};

export function queryMonthCardByCarNum(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryMonthCardByCarNum,
			method: "GET",
			data:_objData,
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					//将业主信息和房屋信息一起返回
					resolve(_json.data);
				} else {
					reject("查询车辆失败");
				}
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};




export function getUserCarMonthOrder(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserCarMonthOrder,
			data: _objData,
			method: "GET",
			//动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
			},
			fail: function(e) {
			}
		});
	})
}
/**
 * 获取用户车辆
 * @param {Object} _objData 
 */
export function getUserCar(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserCar,
			data: _objData,
			method: "GET",
			//动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
			},
			fail: function(e) {
			}
		});
	})
}

/**
 * 获取或生成无牌车虚拟车牌号
 * @param {Object} _objData { openId, openType, communityId }
 */
export function getOrGenerateUnlicensedCarNum(_objData){
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.getOrGenerateUnlicensedCarNum,
			data: _objData,
			method: "GET",
			success: function(res) {
				if (res.statusCode == 200) {
					let _json = res.data;
					if (_json.code == 0) {
						resolve(_json.data);
						return;
					}
					reject(_json);
				}
				reject(res);
			},
			fail: function(e) {
				reject(e);
			}
		});
	})
}

/**
 * 手机端查询停车场信息（扫码免登录）
 * @param {Object} _objData{
	 * paId:'',
	 * communityId:''
 }
 */
export function getAppParkingArea(_objData){
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.getAppParkingArea,
			data: _objData,
			method: "GET",
			success: function(res) {
				if (res.statusCode == 200) {
					let _json = res.data;
					if (_json.code == 0) {
						resolve(_json.data);
						return;
					}
				}
				reject(res);
			},
			fail: function(e) {
				reject(e);
			}
		});
	})
}

/**
 * 停车场找车
 * @param {Object} _objData{
	 * carNum:'',
	 * paId:''
 }
 */
export function getCarInParkingArea(_objData){
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.findCarInParkingArea,
			data: _objData,
			method: "GET",
			//动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
			},
			fail: function(e) {
			}
		});
	})
}

