import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import url from '../../conf/url.js';

export function getCouponUsers(_objData, _couponUsers) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.queryCouponUser,
			method: "GET",
			data: _objData, //动态数据
			success: function(res) {
				if (res.statusCode == 200) {
					//成功情况下跳转
					_couponUsers = res.data;
					if (_couponUsers.length < 1) {
						//_that.noData = true;
						reject();
					}
					resolve(_couponUsers);
					return;
				}
				reject();
			},
			fail: function(e) {
				reject();
			}
		});
	})
}