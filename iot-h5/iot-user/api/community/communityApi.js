import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';

export function getCommunityName() {
	let _community = uni.getStorageSync("currentCommunityInfo");
	if(_community){
		return _community.communityName;
	}
	return "无";
}

export function getCommunityId() {
	let _community = uni.getStorageSync("currentCommunityInfo");
	if(_community){
		return _community.communityId;
	}
	return "-1"
}

/**
 * 保存当前小区缓存（规范化为 {communityId, communityName}）
 * @param {Object} _community 小区对象（兼容 {communityId,name} 与 {communityId,communityName}）
 */
export function saveCurrentCommunity(_community) {
	if (!_community) return;
	uni.setStorageSync("currentCommunityInfo", {
		communityId: _community.communityId || '-1',
		communityName: _community.communityName || _community.name || '无'
	});
}

/**
 * 获取当前有效小区ID：缓存有效直接用；为 -1/空 则查询小区列表取第一个并写入当前小区
 * @returns {Promise<string>} 有效小区ID；查不到小区返回 ''
 */
export function ensureCommunityId() {
	const cur = getCommunityId();
	if (cur && cur !== '-1') return Promise.resolve(cur);
	return queryCommunitys({
		page: 1,
		row: 50
	}).then(res => {
		const list = (res && res.code == 0 && res.data) || [];
		if (!list.length) return '';
		const first = list[0];
		saveCurrentCommunity({
			communityId: first.communityId,
			communityName: first.name || first.communityName
		});
		return first.communityId;
	}).catch(() => '');
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
export function queryCommunitys(_objData) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryCommunitys,
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

export function getUserProperty(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.listUserProperty,
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

