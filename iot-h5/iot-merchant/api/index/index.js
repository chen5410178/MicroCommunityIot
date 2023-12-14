import mapping from '../../constant/mapping.js'

import {
	loadProduct
} from '@/api/product/productApi.js'
/**
 * 查询 广告信息
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求内容
 */
export function loadAdvert(_that, _data) {
	return new Promise(function(reslove, reject) {
		_that.context.get({
			url: _that.url.listAdvertPhoto,
			data: _data, //动态数据
			success: function(res) {
				reslove(res);
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		});
	})
}

/**
 * 查询shopId
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求内容
 */
export function loadShopId() {
	return mapping.SHOP_ID;
}
/**
 * 查询 权限
 * @param {Object} _that 上下文对象
 */
export function listStaffPrivileges(_that) {
	_that.context.get({
		url: _that.url.listStaffPrivileges,
		data: {
			a: '123'
		}, //动态数据
		success: function(res) {
			let _data = res.data;
			let _privilege = [];
			_data.datas.forEach(item => {
				_privilege.push(item.pId);
			});
			uni.setStorageSync('hc_staff_privilege', JSON.stringify(_privilege));
		},
		fail: function(e) {

		}
	});

}
/**
 * 查询首页 目录
 */
export function loadCategory() {
	return {
		pageone: [{
				name: "收银台",
				src: "/static/image/menu/cashier.png",
				href: "/pages/cashier/cashier"
			}, {
				name: "商品规格",
				src: "/static/image/menu/spec.png",
				href: "/pages/productSpecManage/productSpecManage"
			}, {
				name: "商品",
				src: "/static/image/menu/goods.png",
				href: "/pages/goodsManage/goodsManage"
			}, {
				name: "仓库",
				src: "/static/image/menu/stock.png",
				href: "/pages/shopHouseManage/shopHouseManage"
			}, {
				name: "会员",
				src: "/static/image/menu/vipuser.png",
				href: "/pages/vipUser/vipUser"
			}, {
				name: "积分",
				src: "/static/image/menu/jifeng.png",
				href: "/pages/vipUser/vipLevel"
			}, {
				name: "家政",
				src: "/static/image/menu/service.png",
				href: "/pages/serviceManage/serviceManage"
			}, {
				name: "库存管理",
				src: "/static/image/menu/storehouse.png",
				href: "/pages/stock/stock"
			},
		],
		pagetwo: [
			{
				name: "停车劵",
				src: "/static/image/index_goods.png",
				href: "/pages/coupon/parkingCoupon"
			},
		]
	};
}

/**
 * 查询文化
 * @param {Object} _that 上下文 对象
 * @param {Object} _data 查询内容
 */
export function loadActivitys(_that, _data) {
	return new Promise(function(reslove, reject) {
		_that.context.get({
			url: _that.url.listActivitiess,
			data: _data, //动态数据
			success: function(res) {
				reslove(res);
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		});
	});
}

/**
 * 查询店铺信息
 * @param {Object} _that 上下文 对象
 * @param {Object} _data 查询内容
 */
export function loadShops(_that, _data) {
	return new Promise(function(reslove, reject) {
		_that.context.get({
			url: _that.url.queryShop,
			data: _data, //动态数据
			success: function(res) {
				reslove(res);
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		});
	});
}
/**
 * 扫码
 * @param {扫码信息} _code 
 */
export function scanQrCode(_code, _that) {

	if (_code.startsWith('http://') || _code.startsWith('https://')) {
		setTimeout(function() {
			uni.navigateTo({
				url: '/pages/hcWebView/hcWebView?url=' + _code
			})
		}, 1000);
		return;
	}
	if (_code.startsWith('order-write-off:')) {
		return;
	}

	//查询商品
	loadProduct(_that, {
			shopId: _that.getCurrentShop().shopId,
			page: 1,
			row: 1,
			barCode: _code
		})
		.then(_data => {
			if (!_data.data || _data.data.length < 1) {
				uni.showToast({
					icon:'none',
					title:_code
				});
				return;
			}
			uni.navigateTo({
				url: '/pages/goodsManage/productView?productId=' + _data.data[0].productId
			})
		})

}
