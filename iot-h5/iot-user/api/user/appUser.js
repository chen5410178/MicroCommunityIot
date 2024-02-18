import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';

/**
 * 是否房屋认证

 * @param {Object} _tel 手机号
 */
export function getAppUser() {
	let _data = {
		page:1,
		row:30,
	};
	return new Promise((resolve, reject) => {
		request({
			url: url.queryBindRoom,
			method: "GET",
			data: _data,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json  = res.data;
				if(_json.code != 0){
					reject(_json.msg);
					return;
				}
				if(!_json.data || _json.data.length <1 ){
					reject('未认证房屋');
					return;
				}
				
				uni.setStorageSync("userCommunitys",_json.data);
				let _currentCommunityInfo = uni.getStorageSync("currentCommunityInfo");
				if(!_currentCommunityInfo){
					uni.setStorageSync("currentCommunityInfo",_json.data[0]);
				}
				
				resolve(_json.data[0]);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}

export function getAppUsers() {
	let _data = {
		page:1,
		row:30,
	};
	return new Promise((resolve, reject) => {
		request({
			url: url.queryBindRoom,
			method: "GET",
			data: _data,
			success: function(res) {
				//将业主信息和房屋信息一起返回
				let _json  = res.data;
				if(_json.code != 0){
					reject(_json.msg);
					return;
				}
				if(!_json.data || _json.data.length <1 ){
					reject('未认证房屋');
					return;
				}
				uni.setStorageSync("userCommunitys",_json.data);
				resolve(_json.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}