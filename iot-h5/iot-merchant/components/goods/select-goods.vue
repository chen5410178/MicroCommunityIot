<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isShowSelectGoods?'show':''">
			<view class="cu-dialog" >
				<view class="u-menu-wrap">
					<!--:scroll-top="scrollTop" -->
					<scroll-view scroll-y scroll-with-animation class="u-tab-view menu-scroll-view" style="height: 800upx;">
						<view v-for="(category,index) in categoryList" :key="category.categoryId" class="u-tab-item"
							:class="[current==index ? 'u-tab-item-active' : '']" :data-current="index"
							@tap.stop="showCategory(index)">
							<text class="u-line-1">{{category.categoryName}}</text>
						</view>
					</scroll-view>
					<block>
						<scroll-view scroll-y class="right-box" style="height: 800upx;">
							<view class="page-view">
								<view class="class-item">
									<view class="item-title flex justify-between">
										<text>{{currentName}}</text>
										<button class="cu-btn line-blue round sm" @click="_closeSelectGoods()">关闭</button>
									</view>
									<view class="margin-top">		
										<view class="cu-list menu-avatar ">
											<view class="cu-item arrow" 
												v-for="(product,index) in productDtos" :key="index" @click="_selectProduct(product)">
												<view class="cu-avatar round lg" :style="'background-image:url('+product.coverPhoto+')'"></view>
												<view class="content">
													<view class="text-black">{{product.prodName}}</view>
												</view>
												<view class="aciton">
													<button class="cu-btn line-blue round sm">选择</button>
												</view>
											</view>
										</view>
									</view>
								</view>
							</view>
						</scroll-view>
					</block>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		loadProductCagetor,
		loadProduct
	} from "../../api/product/productApi.js";
	export default {
		name: "selectGoods",
		data() {
			return {
				current: 0,
				currentName: '',
				categoryList: [],
				productDtos: []
			};
		},
		props:{
			isShowSelectGoods:{
				type:Boolean,
				default:false
			}
		},
		created() {
			this._loadProductCategoryList();
		},
		methods:{
			_loadProductCategoryList() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				_that.categoryList = [];
				if (!_shopId) {
					return;
				}
				let params = {
					page: 1,
					row: 50,
					shopId: _shopId
				}
				loadProductCagetor(_that, params)
					.then(function(result) {
						_that.categoryList = result.data;
						_that.showCategory(0);
					})
			},
			showCategory: function(_index) {
				this.current = _index;
				if (this.categoryList && this.categoryList.length > 0) {
					this.currentName = this.categoryList[_index].categoryName;
					this._loadGoods();
				}
			},
			_loadGoods: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				_that.productDtos = [];
				if (!_shopId) {
					return;
				}
				let params = {
					page: 1,
					row: 100,
					shopId: _shopId,
					categoryId: this.categoryList[this.current].categoryId
				}
				loadProduct(_that, params)
					.then(function(res) {
						if (res.code === 0 && res.data.length > 0) {
							let _data = res.data;
							_that.productDtos = _data;
						}
					});
			},
			_selectProduct:function(_product){
				this.$emit('selectProduct',_product)
			},
			_closeSelectGoods:function(){
				this.$emit('closeSelectGoods',{})
				
			}
		}
	}
</script>

<style>
.heard-location-icon{
  width: 100%;
  height: 300rpx;
}

.u-wrap {
		height: calc(100vh);
		display: flex;
		flex-direction: column;
	}

	.u-search-box {
		padding: 18rpx 30rpx;
	}

	.u-menu-wrap {
		flex: 1;
		display: flex;
		overflow: hidden;
	}

	.u-tab-view {
		width: 200rpx;
		height: 100%;
	}

	.u-tab-item {
		height: 110rpx;
		background: #f6f6f6;
		box-sizing: border-box;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 26rpx;
		color: #444;
		font-weight: 400;
		line-height: 1;
	}

	.u-tab-item-active {
		position: relative;
		color: #000;
		font-size: 30rpx;
		font-weight: 600;
		background: #fff;
	}

	.u-tab-item-active::before {
		content: "";
		position: absolute;
		border-left: 4px solid $u-type-primary;
		height: 32rpx;
		left: 0;
		top: 39rpx;
	}

	.u-tab-view {
		height: 100%;
	}

	.right-box {
		background-color: rgb(250, 250, 250);
	}

	.page-view {
		padding: 16rpx;
	}

	.class-item {
		margin-bottom: 30rpx;
		background-color: #fff;
		padding: 16rpx;
		border-radius: 8rpx;
	}

	.item-title {
		font-size: 26rpx;
		color: $u-main-color;
		font-weight: bold;
	}

	.item-menu-name {
		font-weight: normal;
		font-size: 24rpx;
		color: $u-main-color;
		display: inline-block;
		white-space: nowrap;
		width: 90%;
		overflow: hidden;
		text-overflow:ellipsis;
	}

	.item-container {
		display: flex;
		flex-wrap: wrap;
	}

	.thumb-box {
		width: 33.333333%;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-direction: column;
		margin-top: 20rpx;
	}

	.item-menu-image {
		width: 120rpx;
		height: 120rpx;
	}
	
	.menu-avatar{
		left: 0px;
	}
	.cu-list.menu-avatar>.cu-item>.cu-avatar{
		left: 0px;
	}
	.cu-list.menu-avatar>.cu-item .content{
		left: 60px;
	}
</style>
