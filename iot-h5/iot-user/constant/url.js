import conf from '../conf/config.js'
const baseUrl = conf.baseUrl;
export default {
	baseUrl: baseUrl,
	wechatRefrashToken: baseUrl + "app/refreshToken", // 公众号刷新token
	

	NEED_NOT_LOGIN_PAGE: [
		'pages/login/login',
		'pages/login/register',
		
	],
	NEED_NOT_LOGIN_URL: [
		baseUrl + "app/payment/toPayTempCarFee"
	]
}
