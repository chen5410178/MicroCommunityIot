import conf from '../conf/config.js'
const baseUrl = conf.baseUrl;
export default {
	baseUrl: baseUrl,
	wechatRefrashToken: baseUrl + "app/refreshToken", // 公众号刷新token
	appUserRegister: baseUrl + "app/login.appUserRegister", // 用户注册
	appUserLogin: baseUrl + "app/login.appUserLogin", // 用户注册

	NEED_NOT_LOGIN_PAGE: [
		'pages/login/login',
		'pages/login/register',
		
	],
	NEED_NOT_LOGIN_URL: [
		baseUrl + "app/payment/toPayTempCarFee"
	]
}
