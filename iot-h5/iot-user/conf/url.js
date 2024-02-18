import conf from '../conf/config.js'
const baseUrl = conf.baseUrl;
export default {
	baseUrl: baseUrl,
	wechatRefrashToken: baseUrl + "iot/api/refreshToken", // 公众号刷新token
	appUserRegister: baseUrl + "iot/api/login.appUserRegister", // 用户注册
	appUserLogin: baseUrl + "iot/api/login.appUserLogin", // 用户注册
	listChargeMachine: baseUrl + "iot/api/chargeMachine.listChargeMachine", // 用户注册
	listChargeMachinePort: baseUrl + "iot/api/chargeMachine.listChargeMachinePort", // 用户注册
	getCommunityWechatAppId: baseUrl + "iot/api/wechat.getCommunityWechatAppId", // 用户注册
	queryAppUserAccount: baseUrl + "iot/api/account.queryAppUserAccount", // 查询用户账户

	startCharge: baseUrl + "iot/api/chargeMachine.startCharge", // 开始充电
	stopCharge: baseUrl + "iot/api/chargeMachine.stopCharge", // 结束充电

	listChargeMachineOrder: baseUrl + "iot/api/chargeMachine.listChargeMachineOrder", // 查询充电订单
	listChargeMonthOrder: baseUrl + "iot/api/chargeCard.listChargeMonthOrder", // 查询充电订单
	listChargeMonthCard: baseUrl + "iot/api/chargeCard.listChargeMonthCard", // 查询月卡
	customCarInOut: baseUrl + "iot/api/barrier.customCarInOut", //手工保存车牌号
	getTempCarFeeOrder: baseUrl + "iot/api/tempCarFee.getTempCarFeeOrder",
	refreshOpenId: baseUrl + "iot/api/wechat.wechatRefreshOpenId", // 公众号刷新token
	cashier: baseUrl + "iot/api/payment.cashier",
	listParkingCouponCar: baseUrl + "iot/api/parkingCoupon.listParkingCouponCar",
	queryMonthCardByCarNum: baseUrl + "iot/api/carMonth.queryMonthCardByCarNum",
	queryWaitPayFeeTempCar: baseUrl + "iot/api/car.queryWaitPayFeeTempCar",
	listVisitType: baseUrl + "iot/api/visitType.listVisitType",
	saveAddVisit: baseUrl + "iot/api/visit.saveVisit",
	listOwnerVisit: baseUrl + "iot/api/visit.listVisit",
	uploadImage: baseUrl + "iot/api/upload/uploadImage",
	queryFloors: baseUrl + "iot/api/floor.queryFloors",
	queryUnits: baseUrl + "iot/api/unit.queryUnits",
	queryRoomsByApp: baseUrl + "iot/api/room.queryRoomsByApp",
	savePersonFace: baseUrl + "iot/api/personFace.savePersonFace",
	listPersonFace: baseUrl + "iot/api/personFace.listPersonFace",
	applyPassQrcode: baseUrl + "iot/api/passcode.applyPassQrcode",
	refreshPassQrcode: baseUrl + "iot/api/passcode.refreshPassQrcode",
	refreshUserPassQrcode: baseUrl + "iot/api/passcode.refreshUserPassQrcode",
	openPassQrcode: baseUrl + "iot/api/passcode.openPassQrcode",
	userLogout:  baseUrl + "iot/api/login.userLogout",
	

	listMeterQrcode: baseUrl + "iot/api/meterQrcode.listMeterQrcode",
	listMeterMachine: baseUrl + "iot/api/meterMachine.listMeterMachine",
	listMeterMachineCharge: baseUrl + "iot/api/meterMachineCharge.listMeterMachineCharge",
	userSendSms: baseUrl + "iot/api/user.userSendSms",
	queryBindRoom: baseUrl + "iot/api/appUser.queryBindRoom",
	getVisitQrCodeUrl: baseUrl + "iot/api/visit.getVisitQrCodeUrl",
	listOwnerAccessControl: baseUrl + "iot/api/accessControl.listOwnerAccessControl",
	ownerOpenAccessControlDoor: baseUrl + "iot/api/accessControl.ownerOpenAccessControlDoor",
	listOwnerFace: baseUrl + "iot/api/owner.listOwnerFace",
	uploadOwnerPhoto: baseUrl + "iot/api/owner.uploadOwnerPhoto",
	listOwnerFaceDownload: baseUrl + "iot/api/accessControlLog.listOwnerFaceDownload",
	getOwnerVisit: baseUrl + "iot/api/visit.listOwnerVisit",
	//todo 查询业主信息
	listUserOwner: baseUrl + "iot/api/owner.listUserOwner",
	//todo 查询物业公司
	listUserProperty: baseUrl + "iot/api/store.listUserProperty",
	//todo 查询业主房屋
	queryUserRoom: baseUrl + "iot/api/room.queryUserRoom",
	listUserCarMonthOrder: baseUrl + "iot/api/carMonth.listUserCarMonthOrder",
	listUserChargeMonthOrder: baseUrl + "iot/api/chargeCard.listUserChargeMonthOrder",
	
	
	
	
	
	
	NEED_NOT_LOGIN_PAGE: [
		'pages/user/login',
		'pages/user/register',

	],
	NEED_NOT_LOGIN_URL: [
		baseUrl + "app/payment/toPayTempCarFee"
	]
}