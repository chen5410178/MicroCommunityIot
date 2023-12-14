<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isShowSelectInStock?'show':''">
			<view class="cu-dialog">
				<!-- <view class="cu-bar bg-white search ">
					<view class="search-form round">
						<text class="cuIcon-search"></text>
						<input type="text" placeholder="输入会员手机号" v-model="name" confirm-type="search"></input>
					</view>
					<view class="action">
						<button class="cu-btn line-blue shadow-blur round sm margin-right-sm"
							@tap="_searchVipUser()">搜索</button>
						<button class="cu-btn line-blue round sm" @click="_closeVipUser()">关闭</button>
					</view>
				</view> -->
				<scroll-view scroll-y style="height: 700upx;">
					<block>
						<view>
							<view class="cu-list menu-avatar">
								<view class="cu-item" v-for="(items,sub) in inStocks" :key="sub"
									@click="_selectInStock(items)">
									<view class="cu-avatar round lg">
										<view class="text-grey">{{items.personName.substring(0,1)}}</view>
									</view>
									<view class="content">
										<view class="text-grey"><text class="text-abc">{{items.orderId}}</text></view>
										<view class="text-gray text-sm flex">{{items.createTime}}</view>
									</view>
									<view class="action">
										<view class="text-grey text-xs">{{items.personName}}</view>
									</view>
								</view>
							</view>
						</view>
					</block>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		queryInoutOrder,
	} from "../../api/product/productApi.js";
	export default {
		name: "selectInStock",
		data() {
			return {
				inStocks: [],
				name: '',
				isShowSelectInStock:false,
			};
		},
		methods: {
			_loadInStock: function() {
				let _that = this
				let params = {
					page: 1,
					row: 50,
					shopId: _that.getCurrentShop().shopId,
					orderType:'10000'
				}
				queryInoutOrder(_that, params)
					.then(function(result) {
						_that.inStocks = result.data;
					})
			},
			_searchInStock: function() {
				this._loadInStock();
			},
			_selectInStock: function(_order) {
				this.$emit('selectInStock', _order);
				this.isShowSelectInStock = false;
				
			},
			_closeInStock: function() {
				this.isShowSelectInStock = false;
			},
			openInStock: function() {
				this.isShowSelectInStock = true;
				this._loadInStock()
			}
		}
	}
</script>

<style>

</style>
