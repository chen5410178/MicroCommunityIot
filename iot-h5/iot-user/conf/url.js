import conf from '../conf/config.js'
const baseUrl = conf.baseUrl;
export default {
	baseUrl: baseUrl,
	listSystemInfo: baseUrl + "iot/api/system.listSystemInfo", // 查询系统信息（免登录）
	listSystemPrivacy: baseUrl + "iot/api/system.listSystemPrivacy", // 查询隐私/小区联系电话（免登录）
	wechatRefrashToken: baseUrl + "iot/api/refreshToken", // 公众号刷新token
	appUserRegister: baseUrl + "iot/api/login.appUserRegister", // 用户注册
	appUserLogin: baseUrl + "iot/api/login.appUserLogin", // 用户注册
	phoneWechatLogin: baseUrl + "iot/api/login.phoneWechatLogin",
	listChargeMachine: baseUrl + "iot/api/chargeMachine.listChargeMachine", // 用户注册
	listChargeMachinePort: baseUrl + "iot/api/chargeMachine.listChargeMachinePort", // 用户注册
	getChargeMachineByCode: baseUrl + "iot/api/chargeMachine.getAppChargeMachineByCode", // 按设备编码查询充电桩（扫码直达充电页，免登录）
	getCommunityWechatAppId: baseUrl + "iot/api/wechat.getCommunityWechatAppId", // 用户注册
	queryAppUserAccount: baseUrl + "iot/api/account.queryAppUserAccount", // 查询用户账户
	queryAppUserAccountDetail: baseUrl + "iot/api/account.queryAppUserAccountDetail", // 查询账户交易明细（业主端）
	refundAppUserAccount: baseUrl + "iot/api/account.ownerRefundAccount", // 业主账户余额退款（微信原路退回）

	startCharge: baseUrl + "iot/api/chargeMachine.startCharge", // 开始充电
	stopCharge: baseUrl + "iot/api/chargeMachine.stopCharge", // 结束充电
	startCarCharge: baseUrl + "iot/api/chargeMachine.startCarCharge", // 开始汽车充电
	stopCarCharge: baseUrl + "iot/api/chargeMachine.stopCarCharge", // 结束汽车充电
	heartbeatCarCharge: baseUrl + "iot/api/chargeMachine.heartbeatCarCharge", // 心跳充电是否开启岑巩
	queryCarChargeProcess: baseUrl + "iot/api/chargeMachine.queryCarChargeProcess", // 查询充电进度
	queryCouponUser: baseUrl + "iot/api/coupon.listCouponPropertyUser", //查询用户优惠卷
	

	listChargeMachineOrder: baseUrl + "iot/api/charge.queryPhoneChargeOrder", // 查询充电订单
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
	queryWaitVisitCarNum: baseUrl + "iot/api/visit.queryWaitVisitCarNum",
	
	
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
	userLogout: baseUrl + "iot/api/login.userLogout",


	listMeterQrcode: baseUrl + "iot/api/meterQrcode.listMeterQrcode",
	listMeterMachine: baseUrl + "iot/api/meterMachine.listMeterMachine",
	listMeterMachineCharge: baseUrl + "iot/api/meterMachineCharge.listMeterMachineCharge",
	userSendSms: baseUrl + "iot/api/user.userSendSms",
	updateUserPwd: baseUrl + "iot/api/user.updateUserPwd", // 修改登录密码（需登录）
	resetUserPwd: baseUrl + "iot/api/user.resetUserPwd", // 忘记密码重置（免登录）
	appUserModifyPasswd: baseUrl + "iot/api/user.appUserModifyPasswd", // 用户端修改密码（登录态免验证码）
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
	listUserCar: baseUrl + "iot/api/car.listUserCar",

	queryCommunitys: baseUrl + "iot/api/community.listCommunitys",
	saveAppUser: baseUrl + "iot/api/appUser.saveAppUser",
	findCarInParkingArea: baseUrl + "iot/api/parkingArea.findCarInParkingArea",
	getAppParkingArea: baseUrl + "iot/api/parkingArea.getAppParkingArea", // 手机端查询停车场信息（扫码免登录）
	getWechatMiniOpenId: baseUrl + "iot/api/wechat.getWechatMiniOpenId",
	refreshAppUserBindingOwnerOpenId: baseUrl+"iot/api/owner.refreshAppUserBindingOwnerOpenId",
	ownerUserLoginByOpenId: baseUrl+"iot/api/user.ownerUserLoginByOpenId",
	queryMyChargeOrder: baseUrl+"iot/api/charge.queryMyChargeOrder",
	saveUserChargeCar: baseUrl+"iot/api/user.saveUserChargeCar",
	getUserChargeCar: baseUrl+"iot/api/user.getUserChargeCar",
	getOrGenerateUnlicensedCarNum: baseUrl+"iot/api/car.getOrGenerateUnlicensedCarNum",

	listChargePeakAlleyPrice: baseUrl + "iot/api/chargePeakAlleyPrice.listChargePeakAlleyPrice", // 分时电价
	listChargeRule: baseUrl + "iot/api/chargeRule.listChargeRule", // 费率规则
	saveChargeMonthOrder: baseUrl + "iot/api/chargeCard.saveChargeMonthOrder", // 购买/续费充电月卡
	orderRefundMoney: baseUrl + "iot/api/chargeMachine.orderRefundMoney", // 充电订单退款
	listChargeProcessInfo: baseUrl + "iot/api/chargeProcessInfo.listChargeProcessInfo", // 充电进度信息
	listMyInvoiceOrders: baseUrl + "iot/api/invoice.listMyInvoiceOrders", // 查询我的开票订单(未开票/已开票)
	getMyInvoiceHeading: baseUrl + "iot/api/invoice.getMyInvoiceHeading", // 按登录手机号查询商城发票抬头
	saveInvoice: baseUrl + "iot/api/invoice.saveInvoice", // 提交开票申请






	NEED_NOT_LOGIN_PAGE: [
		'pages/user/login',
		'pages/user/register',

	],
	NEED_NOT_LOGIN_URL: [
		baseUrl + "app/payment/toPayTempCarFee"
	]
}