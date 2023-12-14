
import util from '../../lib/java110/utils/util.js'

import {
	request,
	requestNoAuth
} from '../../lib/java110/java110Request.js'

import url from '../../constant/url.js';

import {getUserId} from '../user/userApi.js'

const SHOP_INFO = "SHOP_INFO";

const CURRENT_SHOP_INFO = "CURRENT_SHOP_INFO";


/**
 * 获取小区信息
 * @param {Object} reload 是否重新加载 小区信息
 * @param {Object} _condition 查询小区条件
 */
export function getShop(reload, _condition) {
	let _communityInfo = uni.getStorageSync(SHOP_INFO);
	let _that = this;
	return new Promise(function(reslove, reject) {
		request({
			url: url.queryShop,
			data: {
				page:1,
				row:50
			},
			success: function(res) {
				if (res.statusCode != 200) {
					uni.showToast({
						icon: 'none',
						title: res.data
					});
					return;
				}
				let data = res.data;
				if (data.total < 1) {
					uni.showToast({
						icon: none,
						title: '当前员工还没有隶属店铺,请先去后台添加'
					});
					return;
				}
				let _shops = data.data;
				uni.setStorageSync(SHOP_INFO, _shops);
				reslove(_shops)
			},
			fail: function(error) {
				// 调用服务端登录接口失败
				uni.showToast({
					title: '调用接口失败'
				});
			}
		});
	});
}
export function getCurrentShop() {
	let currentShop = uni.getStorageSync(CURRENT_SHOP_INFO);
	if (util.isNull(currentShop)) {
		return {};
	}
	if (currentShop instanceof Object) {
		return currentShop;
	}
	return JSON.parse(currentShop);
}

export function initShop(){
	return new Promise(function(reslove, reject) {
		getShop(true)
		.then(function(_shops) {
			if(!_shops || _shops.length<1){
				reject('商铺不存在');
				return ;
			}
			uni.setStorageSync(CURRENT_SHOP_INFO, _shops[0]);
			reslove();
		},function(_error){
			reject(_error);
		});
	});
}


export function applyShopCode(_data,_that){
	return new Promise(function(reslove,reject){
		request({
			url: url.applyShopCode,
			method: "POST",
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data);
				}else{
					reject(res.data.msg);
				}
			},
			fail: function(e) {
				reject('网络异常');
			}
		})
	});
}
