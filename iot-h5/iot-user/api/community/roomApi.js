import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';


export function queryUserRoom(_objData){
	return new Promise((resolve, reject) => {
		request({
			url: url.queryUserRoom,
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
