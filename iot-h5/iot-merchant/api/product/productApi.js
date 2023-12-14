import url from '../../constant/url.js'

/**
 * 查询商品信息
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function loadProduct(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryProduct,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

//修改产品信息
export function updateProduct(_data,_that){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.updateProduct,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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

//修改产品状态
export function updateProductState(_data,_that){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.updateProductState,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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
//删除产品信息
export function deleteProduct(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.deleteProduct,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}


/**
 * 查询楼栋信息
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function loadProductCagetor(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryProductCagetor,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
//保存商品分组
export function saveProductCagetor(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveProductCagetor,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}


//修改商品分组
export function editProductCagetor(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.editProductCagetor,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
//删除商品分组
export function delProductCagetor(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.delProductCagetor,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}



/**
 * 查询产品规格信息
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryProductSpec(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryProductSpec,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
//保存产品规格
export function saveProductSpec(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveProductSpec,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}


//修改产品规格
export function updateProductSpec(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.updateProductSpec,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
//删除产品规格
export function deleteProductSpec(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.deleteProductSpec,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

export function queryResourceSupplier(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryResourceSupplier,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
/**
 * 查询仓库
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryShopHouse(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryShopHouse,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
//保存仓库
export function saveShopHouse(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveShopHouse,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}


//修改仓库
export function updateShopHouse(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.updateShopHouse,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}
//删除仓库
export function deleteShopHouse(_that, _data){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.deleteShopHouse,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

//添加产品信息
export function saveProduct(_data,_that){
	console.log(_data)
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveProduct,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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

//添加产品信息
export function saveInoutOrder(_data,_that){
	console.log(_data)
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveInoutOrder,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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

//查询明细

/**
 * 查询仓库
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryInoutOrder(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryInoutOrder,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

/**
 * 查询商品
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryInoutOrderProduct(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryInoutOrderProduct,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

/**
 * 查询商品
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryAssetInventory(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryAssetInventory,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}

/**
 * 查询盘点商品
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryAssetInventoryProduct(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryAssetInventoryProduct,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}


//资产盘点
export function saveAssetInventory(_data,_that){
	console.log(_data)
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveAssetInventory,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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

//删除盘点
export function deleteAssetInventory(_data,_that){
	console.log(_data)
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.deleteAssetInventory,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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

//修改盘点
export function updateAssetInventory(_data,_that){
	console.log(_data)
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.updateAssetInventory,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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


//盘点入库
export function assetInventoryInStock(_data,_that){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.assetInventoryInStock,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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

// 查询调拨
/**
 * @param {Object} _that 上下文对象
 * @param {Object} _data 请求报文
 */
export function queryAllocationOrder(_that,_data){
	return new Promise(function(reslove,reject){
		_that.context.get({
			url: url.queryAllocationOrder,
			data:_data,
			success: function(res) {
				if(res.statusCode == 200){
					reslove(res.data);
				}else{
					wx.showToast({
						title: "服务器异常了",
						icon: 'none',
						duration: 2000
					})
				}
			},
			fail: function(e) {
				wx.showToast({
					title: "服务器异常了",
					icon: 'none',
					duration: 2000
				})
			}
		})
	});
}


//调拨
export function saveAllocationOrder(_data,_that){
	return new Promise(function(reslove,reject){
		_that.context.post({
			url: url.saveAllocationOrder,
			data:_data,
			success: function(res) {
				if(res.data.code == 0){
					reslove(res.data.msg);
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
