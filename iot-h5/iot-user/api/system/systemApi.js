import {
	requestNoAuth
} from '@/lib/java110/java110Request.js'
import url from '@/conf/url.js'

/**
 * 查询系统信息（免登录）：版本号/系统名称/公司名/用户服务协议/隐私保护指引/版权信息
 * @returns {Promise<Object>} 系统配置对象（system_info 首条）
 */
export function getSystemInfo() {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listSystemInfo,
			method: "GET",
			data: {
				page: 1,
				row: 1
			},
			success: function(res) {
				let _json = res.data
				if (_json.code == 0 && _json.data && _json.data.length > 0) {
					resolve(_json.data[0])
					return
				}
				reject(_json)
			},
			fail: function(e) {
				reject(e)
			}
		})
	})
}

/**
 * 查询隐私/小区联系电话（免登录）
 * @param {String} communityId 小区ID
 * @returns {Promise<Object>} 响应体 {code, msg, data:{tel, version, userProtocol, merchantProtocol, copyrightInfo, ...}}
 */
export function getSystemPrivacy(communityId) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listSystemPrivacy,
			method: "GET",
			data: {
				communityId: communityId
			},
			success: function(res) {
				let _json = res.data
				if (_json.code == 0) {
					resolve(_json)
					return
				}
				reject(_json)
			},
			fail: function(e) {
				reject(e)
			}
		})
	})
}
