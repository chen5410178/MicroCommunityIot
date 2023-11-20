import conf from '../conf/config.js'
const baseUrl = conf.baseUrl;
export default {
	baseUrl: baseUrl,
	wechatRefrashToken: baseUrl + "iot/api/refreshToken", // 公众号刷新token
	appUserRegister: baseUrl + "iot/api/login.appUserRegister", // 用户注册
	appUserLogin: baseUrl + "iot/api/login.appUserLogin", // 用户注册
	listChargeMachine: baseUrl + "iot/api/chargeMachine.listChargeMachine", // 用户注册
	listChargeMachinePort:baseUrl + "iot/api/chargeMachine.listChargeMachinePort", // 用户注册
	getCommunityWechatAppId:baseUrl + "iot/api/wechat.getCommunityWechatAppId", // 用户注册
	queryAppUserAccount:baseUrl + "iot/api/account.queryAppUserAccount", // 查询用户账户
	
	startCharge:baseUrl + "iot/api/chargeMachine.startCharge", // 开始充电
	stopCharge:baseUrl + "iot/api/chargeMachine.stopCharge", // 结束充电
	
	listChargeMachineOrder:baseUrl + "iot/api/chargeMachine.listChargeMachineOrder", // 查询充电订单
	listChargeMonthOrder:baseUrl + "iot/api/chargeCard.listChargeMonthOrder", // 查询充电订单
	listChargeMonthCard:baseUrl + "iot/api/chargeCard.listChargeMonthCard", // 查询月卡
	
	

	NEED_NOT_LOGIN_PAGE: [
		'pages/user/login',
		'pages/user/register',
		
	],
	NEED_NOT_LOGIN_URL: [
		baseUrl + "app/payment/toPayTempCarFee"
	]
}
