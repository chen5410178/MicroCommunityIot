/**
 * 车辆信息处理 api 接口类
 * 
 * add by 吴学文 QQ 928255095
 */


import {
	request,
	requestNoAuth
} from '@/lib/java110/java110Request.js'
import
url
from '@/conf/url.js'


export function getChargeMachines(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listChargeMachine,
			method: "GET",
			data: _objData,
			success: function(res) {
				let _data = res.data;
				resolve(_data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};
export function getChargeMachinePort(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listChargeMachinePort,
			method: "GET",
			data: _objData,
			success: function(res) {
				let _data = res.data;
				resolve(_data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};

export function startCharge(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.startCharge,
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

export function stopCharge(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.stopCharge,
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

export function startCarCharge(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.startCarCharge,
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

export function stopCarCharge(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.stopCarCharge,
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

export function heartbeatCarCharge(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.heartbeatCarCharge,
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

/**
 * 查询充电订单
 */
export function getChargeMachineOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listChargeMachineOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg);
					return;
				}
				resolve(res.data.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};

/**
 * 查询充电订单
 */
export function getMeterMachine(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listMeterMachine,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg);
					return;
				}
				resolve(res.data.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};

/**
 * 查询充电订单
 */
export function getChargeMonthOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listChargeMonthOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg);
					return;
				}
				resolve(res.data.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};


/**
 * 查询充电订单
 */
export function getChargeMonthCard(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listChargeMonthCard,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg);
					return;
				}
				resolve(res.data.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};

export function getUserChargeMonthOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserChargeMonthOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg);
					return;
				}
				resolve(res.data.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
};


