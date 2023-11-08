/**
 * 上下文对象,再其他文件调用常量 方法 时间工具类时 只引入上下文文件
 * 
 * add by wuxw 2019-12-27
 * 
 * the source opened https://gitee.com/java110/WechatOwnerService
 */


import {
	getHeaders,
	request,
	requestNoAuth
} from './java110Request.js'



import {getLoginFlag,getUserInfo} from './utils/StorageUtil.js'

let util = {};

/**
 * http 请求 加入是否登录判断
 */
const requestObj = function(_reqObj) {

	//白名单直接跳过检查登录
	if (constant.url.NEED_NOT_LOGIN_URL.includes(_reqObj.url)) {
		requestNoAuth(_reqObj);
		return;
	}
	//校验是否登录，如果没有登录跳转至温馨提示页面
	request(_reqObj);
};
/**
 * 获取位置
 * add by wuxw 2019-12-28
 */




module.exports = {
	
	request: requestObj,

};
