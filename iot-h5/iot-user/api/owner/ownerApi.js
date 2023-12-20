import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';

export function getMemberId(){
	return null;
}

// 人脸审核
export function savePersonFace(_objData) {
	return new Promise( (resolve, reject) => {
		requestNoAuth({
			url: url.savePersonFace,
			data: _objData,
			method: "POST",
			//动态数据
			success: function(res) {
				let _json = res.data;
				uni.showToast({
					icon:'none',
					title:_json.msg
				});
				if (_json.code == 0) {
					resolve(_json);
					return;
				}
			},
			fail: function(e) {
				uni.hideLoading();
				uni.showToast({
					title: "服务器异常了",
					icon: 'none'
				})
			}
		});
	})
}
/**
 * 查询 审核进度

 * @param {Object} _objData
 */
export function listPersonFace(_objData) {
	return new Promise( (resolve, reject) => {
		requestNoAuth({
			url: url.listPersonFace,
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
				uni.hideLoading();
				uni.showToast({
					title: "服务器异常了",
					icon: 'none'
				})
			}
		});
	})
}


// 人脸审核
export function applyPassQrcode(_objData) {
	return new Promise( (resolve, reject) => {
		requestNoAuth({
			url: url.applyPassQrcode,
			data: _objData,
			method: "POST",
			//动态数据
			success: function(res) {
				let _json = res.data;
				uni.showToast({
					icon:'none',
					title:_json.msg
				});
				if (_json.code == 0) {
					resolve(_json);
					return;
				}
			},
			fail: function(e) {
				uni.hideLoading();
				uni.showToast({
					title: "服务器异常了",
					icon: 'none'
				})
			}
		});
	})
}

export function refreshPassQrcode(_objData) {
	return new Promise( (resolve, reject) => {
		requestNoAuth({
			url: url.refreshPassQrcode,
			data: _objData,
			method: "POST",
			//动态数据
			success: function(res) {
				let _json = res.data;
				uni.showToast({
					icon:'none',
					title:_json.msg
				});
				if (_json.code == 0) {
					resolve(_json);
					return;
				}
				
			},
			fail: function(e) {
				uni.hideLoading();
				uni.showToast({
					title: "服务器异常了",
					icon: 'none'
				})
			}
		});
	})
}

