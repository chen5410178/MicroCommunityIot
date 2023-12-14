import url from '../../../conf/url.js'
import {
	requestNoAuth
} from '../java110Request.js'
import {
	isNull
} from '../utils/StringUtil.js'



/**
 * 
 * H5 刷新鉴权登录
 * @param {Object} errorUrl 登录失败时的跳转页面
 * @param {Object} _login  
 * 
 * add by 吴学文 QQ 928255095
 */
export function wechatRefreshToken(errorUrl, _login) {
	let _errorUrl = errorUrl;
	uni.navigateTo({
		url: '/pages/public/login'
	})

}