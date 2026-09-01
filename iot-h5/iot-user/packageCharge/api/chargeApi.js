/**
 * 充电业务 API 聚合（桩/订单/月卡/电价/钱包/车辆）
 * 复用 api/machine/machineApi.js 已有函数，避免重复封装
 */
import {
	request,
	requestNoAuth
} from '@/lib/java110/java110Request.js'
import url from '@/conf/url.js'

/**
 * 查询充电桩列表（电瓶车/电动车）
 */
export function listChargeMachine(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listChargeMachine,
			method: "GET",
			data: _objData,
			success: function(res) {
				resolve(res.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 查询充电桩端口（插座/枪）
 */
export function listChargeMachinePort(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listChargeMachinePort,
			method: "GET",
			data: _objData,
			success: function(res) {
				resolve(res.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 分时电价（尖峰平谷 · 只读）
 */
export function listChargePeakAlleyPrice(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listChargePeakAlleyPrice,
			method: "GET",
			data: _objData,
			success: function(res) {
				resolve(res.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 费率规则
 */
export function listChargeRule(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listChargeRule,
			method: "GET",
			data: _objData,
			success: function(res) {
				resolve(res.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 查询我的充电订单（在充/历史）
 */
export function queryMyChargeOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.queryMyChargeOrder,
			method: "POST",
			data: JSON.stringify(_objData),
			success: function(res) {
				if (res.statusCode == 200) {
					resolve(res.data)
					return
				}
				reject()
			},
			fail: function(e) {
				reject()
			}
		})
	})
}

/**
 * 查询充电订单（按手机号，历史/在充，分页）
 * 返回完整响应 { code, msg, data, records }，data 为订单数组
 */
export function queryPhoneChargeOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listChargeMachineOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 充电订单退款
 */
export function orderRefundMoney(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.orderRefundMoney,
			method: "POST",
			data: JSON.stringify(_objData),
			success: function(res) {
				if (res.statusCode == 200) {
					resolve(res.data)
					return
				}
				reject()
			},
			fail: function(e) {
				reject()
			}
		})
	})
}

/**
 * 查询充电月卡套餐
 */
export function listChargeMonthCard(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listChargeMonthCard,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 查询充电月卡订单（购买记录）
 */
export function listChargeMonthOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listChargeMonthOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 查询用户充电月卡订单
 */
export function listUserChargeMonthOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserChargeMonthOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 购买/续费充电月卡
 */
export function saveChargeMonthOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.saveChargeMonthOrder,
			method: "POST",
			data: JSON.stringify(_objData),
			success: function(res) {
				if (res.statusCode == 200) {
					resolve(res.data)
					return
				}
				reject()
			},
			fail: function(e) {
				reject()
			}
		})
	})
}

/**
 * 查询用户账户（余额）
 */
export function queryAppUserAccount(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.queryAppUserAccount,
			method: "GET",
			data: _objData,
			success: function(res) {
				let _json = res.data
				if (_json.code != 0) {
					reject(_json.msg)
					return
				}
				resolve(_json.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 查询用户车辆
 */
export function listUserCar(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserCar,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 按车牌查询月卡
 */
export function queryMonthCardByCarNum(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.queryMonthCardByCarNum,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}

/**
 * 查询用户月租续费订单
 */
export function listUserCarMonthOrder(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserCarMonthOrder,
			method: "GET",
			data: _objData,
			success: function(res) {
				if (res.data.code != 0) {
					reject(res.data.msg)
					return
				}
				resolve(res.data.data)
			},
			fail: function(res) {
				reject(res)
			}
		})
	})
}
