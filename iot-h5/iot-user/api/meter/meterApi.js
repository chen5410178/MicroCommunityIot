import {request, requestNoAuth} from '../../lib/java110/java110Request.js'
import url from '../../conf/url.js'
import {formatTime}  from '../../lib/java110/utils/DateUtil.js'

/**
 * 查询水电二维码
 */
export function listMeterQrcode(_data) {
	wx.showLoading({
		title: '加载中'
	});
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listMeterQrcode,
			method: "get",
			data: _data,
			success: function(res) {
				wx.hideLoading();
				resolve(res.data);
			},
			fail: function(res) {
				wx.hideLoading();
				reject(res);
			}
		})
	})
}
/**
 * 查询水电表
 */
export function listMeterMachine(_data) {
	wx.showLoading({
		title: '加载中'
	});
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listMeterMachine,
			method: "get",
			data: _data,
			success: function(res) {
				wx.hideLoading();
				resolve(res.data);
			},
			fail: function(res) {
				wx.hideLoading();
				reject(res);
			}
		})
	})
}
/**
 * 查询充值记录
 */
export function getMeterRechargeRecord(_data) {
	wx.showLoading({
		title: '加载中'
	});
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listMeterMachineCharge,
			method: "get",
			data: _data,
			success: function(res) {
				wx.hideLoading();
				resolve(res.data);
			},
			fail: function(res) {
				wx.hideLoading();
				reject(res);
			}
		})
	})
}