import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';

export function getCommunityName() {
	return "无";
}

export function getCommunityId() {
	return "-1"
}


export function queryFloors(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryFloors,
			data: _objData,
			method: "GET",
			//动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
				reject();
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
export function queryUnits(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryUnits,
			data: _objData,
			method: "GET",
			//动态数据
			success: function(res) {

				resolve(res.data);

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
export function queryRoomsByApp(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryRoomsByApp,
			data: _objData,
			method: "GET",
			//动态数据
			success: function(res) {
				let _json = res.data;
				if (_json.code == 0) {
					resolve(_json.data);
					return;
				}
				reject();
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