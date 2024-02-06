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