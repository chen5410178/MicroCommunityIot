import {
	request
} from '@/lib/java110/java110Request.js'
import url from '@/conf/url.js'

/**
 * 查询我的开票订单（未开票/已开票，按当前小区）
 * @param {Object} _objData { communityId, bizCategory }
 */
export function listMyInvoiceOrders(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listMyInvoiceOrders,
			data: _objData,
			method: "GET",
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
				reject(_json.msg || '查询开票订单失败');
			},
			fail: function(e) {
				reject(e)
			}
		});
	})
}

/**
 * 按登录手机号查询商城发票抬头
 */
export function getMyInvoiceHeading() {
	return new Promise((resolve, reject) => {
		request({
			url: url.getMyInvoiceHeading,
			method: "GET",
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
				reject(_json.msg || '查询发票抬头失败');
			},
			fail: function(e) {
				reject(e)
			}
		});
	})
}

/**
 * 手机端提交开票申请（归属手机号取登录用户，入参 phone 仅用于校验本人）
 * @param {Object} _objData { communityId, bizCategory, orderIds, headingType, headingName, taxNo, email, invoiceType, phone }
 */
export function appOpenInvoice(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.appOpenInvoice,
			data: JSON.stringify(_objData),
			method: "POST",
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
				reject(_json.msg || '提交开票失败');
			},
			fail: function(e) {
				reject(e)
			}
		});
	})
}
