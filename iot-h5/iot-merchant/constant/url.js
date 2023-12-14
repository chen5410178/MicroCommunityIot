import conf from '../conf/config.js'
const baseUrl = conf.baseUrl;
const hcBaseUrl = conf.baseUrl;
export default {
	NEED_NOT_LOGIN_URL: [
		// this.listActivitiess,
		// this.listAdvertPhoto,
		// this.queryAppUserBindingOwner,
		// this.listJunkRequirements
	],
	baseUrl: baseUrl,
	hcBaseUrl: baseUrl, // 登录接口
	
	areaUrl: baseUrl + "app/area.listAreas",
	listMyEnteredCommunitys: baseUrl + 'app/community.listMyEnteredCommunitys', //查看员工小区
	queryStaffInfos: baseUrl + 'app/query.staff.infos', //查询员工信息
	changeStaffPwd: baseUrl + 'app/user.changeStaffPwd', //修改密码
	listAuditHistoryComplaints: baseUrl + 'app/auditUser.listAuditHistoryComplaints', //查询历史单
	saveComplaint: baseUrl + 'app/complaint', //查询业主房间
	userLogout: baseUrl + 'app/user.service.logout', //绑定业主
	listAuditComplaints: baseUrl + 'app/auditUser.listAuditComplaints', //查询绑定业主
	auditComplaint: baseUrl + 'app/complaint.auditComplaint', //查询业主门禁
	applyVisitorApplicationKey: baseUrl + "app/applicationKey.applyVisitorApplicationKey", //上传业主照片
	uploadOwnerPhoto: baseUrl + "app/owner.uploadOwnerPhoto",
	getOwnerPhotoPath: hcBaseUrl + "callComponent/download/getFile/fileByObjId",
	filePath: hcBaseUrl + "callComponent/download/getFile/file", //查询业主车位信息
	queryParkingSpacesByOwner: baseUrl + "app/parkingSpace.queryParkingSpacesByOwner", //查询停车位费用
	queryFeeByParkingSpace: baseUrl + "app/fee.queryFeeByParkingSpace", //查询物业费用

	preOrder: baseUrl + "app/payment/toPay", //查询小区
	listCommunitys: baseUrl + "app/community.listCommunitys", //查询小区文化

	// 投诉 处理意见
	listWorkflowAuditInfo: baseUrl + 'app/workflow.listWorkflowAuditInfo',
	queryFeeDiscount: baseUrl + "app/feeDiscount/queryFeeDiscount",
	updateApplyRoomDiscount: baseUrl + "app/applyRoomDiscount/updateApplyRoomDiscount",
	updateReviewApplyRoomDiscount: baseUrl + "app/applyRoomDiscount/updateReviewApplyRoomDiscount",
	queryMenus: baseUrl + "app/query.menu.info",
	listStaffPrivileges: baseUrl + "callComponent/staffPrivilege/listStaffPrivileges",
	queryRoomRenovation: baseUrl + "app/roomRenovation/queryRoomRenovation",
	updateRoomToExamine: baseUrl + "app/roomRenovation/updateRoomToExamine",
	saveRoomRenovationDetail: baseUrl + "app/roomRenovation/saveRoomRenovationDetail",
	queryRoomRenovationRecord: baseUrl + "app/roomRenovation/queryRoomRenovationRecord",
	updateRoomDecorationRecord: baseUrl + "app/roomRenovation/updateRoomDecorationRecord",
	uploadVideo: baseUrl + "callComponent/upload/uploadVedio/upload",
	queryRoomRenovationRecordDetail: baseUrl + "app/roomRenovation/queryRoomRenovationRecordDetail",
	// 查询字典表
	queryDictInfo: baseUrl + "callComponent/core/list",
	// 报修相关信息（维修类型/状态/商品类型...）
	queryRepairInfo: baseUrl + "callComponent/resourceStoreTypeManage/list",
	// queryResourceStoreResName: baseUrl + "app/resourceStore/queryResourceStoreResName",
	queryResourceStoreResName: baseUrl + "callComponent/resourceStore.listUserStorehouses",
	// 查询费用项（水电部分）
	queryFeeTypesItems: baseUrl + "callComponent/roomCreateFeeAdd/list",
	queryPreMeterWater: baseUrl + "app/meterWater/queryPreMeterWater",
	saveMeterWater: baseUrl + "callComponent/meterWater.saveMeterWater",
	// 采购/出库申请部分
	queryPurchaseApplyList: baseUrl + "callComponent/purchaseApplyManage/list",
	queryResourceStoreList: baseUrl + "callComponent/chooseResourceStore/list",
	queryResourceSupplier: baseUrl + "callComponent/resourceSupplier.listResourceSuppliers",
	queryFirstStaff: baseUrl + "app/workflow/getFirstStaff",
	savePurchaseApply: baseUrl + "app/purchase/purchaseApply",
	saveItemOutApply: baseUrl + "app/collection/goodsCollection",
	listMyAuditOrders: baseUrl + "callComponent/myAuditOrders/list",
	listMyItemOutOrders: baseUrl + "app/collection/getCollectionAuditOrder",
	listMyAllocationStoreAuditOrders: baseUrl + "callComponent/resourceStore.listAllocationStoreAuditOrders",
	saveResourceOut: baseUrl + "app/collection/resourceOut",
	saveResourceEnter: baseUrl + "app/purchase/resourceEnter",
	listAuditHistoryOrders: baseUrl + "callComponent/auditUser.listAuditHistoryOrders",
	listItemOutAuditHistoryOrders: baseUrl + "callComponent/auditUser.listItemOutAuditHistoryOrders",
	listAllocationStoreHisAuditOrders: baseUrl + "callComponent/resourceStore.listAllocationStoreHisAuditOrders",
	// listWorkflowAuditInfo2: baseUrl + "callComponent/workflow.listWorkflowAuditInfo",
	// 调拨部分
	listAllocationStorehouseApplys: baseUrl + "callComponent/resourceStore.listAllocationStorehouseApplys",
	listStoreHouses: baseUrl + "callComponent/resourceStore.listStorehouses",
	saveAllocationStorehouse: baseUrl + "callComponent/resourceStore.saveAllocationStorehouse",
	listAllocationStorehouses: baseUrl + "callComponent/resourceStore.listAllocationStorehouses",
	saveAuditAllocationStoreOrder: baseUrl + "callComponent/resourceStore.auditAllocationStoreOrder",
	listWorkflowStepStaffs: baseUrl + "callComponent/workflow.listWorkflowStepStaffs",
	saveMyAuditOrders: baseUrl + "callComponent/myAuditOrders/audit",
	// 公司/员工信息
	queryOrgInfo: baseUrl + "callComponent/orgManage/list",
	queryStaffListInfo: baseUrl + "callComponent/searchStaff/listStaff",

	// 发送验证
	sendTelMessageCode: baseUrl + 'callComponent/validate-tel/sendTelMessageCode',

	//注册
	registerStoreAndShop: baseUrl + 'app/shop.registerStoreAndShop',
	//添加供应商
	loginUrl: baseUrl + 'iot/api/login.pcUserLogin',
	//查询店铺停车劵
	listShopParkingCoupon: baseUrl + 'iot/api/parkingCoupon.listShopParkingCoupon',
	//赠送店铺停车劵
	saveParkingCouponCar: baseUrl + 'iot/api/parkingCoupon.saveParkingCouponCar',
	listParkingCouponCar: baseUrl + 'iot/api/parkingCoupon.listParkingCouponCar',
	applyShopCode: baseUrl + 'iot/api/shop.applyShopCode',
	listSystemInfo:baseUrl+"iot/api/system.listSystemInfo",
	queryShop:baseUrl+"iot/api/store.queryShop",
	
	
}
