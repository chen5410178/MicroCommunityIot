import {
	request,
	requestNoAuth
} from '@/lib/java110/java110Request.js'
import
url
from '@/conf/url.js';


export function getOwnerAccessControl(_objData) {
	return new Promise((resolve, reject) => {
		request({
			url: url.listOwnerAccessControl,
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

export function ownerOpenAccessControlDoor(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.ownerOpenAccessControlDoor,
			method: "POST",
			data: _objData,
			success: function(res) {
				let _json = res.data;
				if(_json.code != 0){
					uni.showToast({
						icon:'none',
						title:_json.msg
					});
					return ;
				}
				resolve(_json);
			},
			fail: function(res) {
				reject(res);
			}
		});
	});
}