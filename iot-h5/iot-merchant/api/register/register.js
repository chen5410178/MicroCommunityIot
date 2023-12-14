import url from '../../constant/url.js'

import {
	requestNoAuth,
	request
} from '../../lib/java110/java110Request.js';
import mapping from '../../lib/java110/java110Mapping.js'

import {isNull} from '../../lib/java110/utils/StringUtil.js'

/**
 * 商品确认接单
 */
export function saveRegister(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.orderCartGrabbing,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}