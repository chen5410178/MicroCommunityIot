<template>
	<view>
		<view class="cu-bar bg-white search ">
			<view class="search-form round">
				<text class="cuIcon-search"></text>
				<input type="text" placeholder="输入搜索的小区名称" v-model="communityName" confirm-type="search"></input>
			</view>
			<view class="action">
				<button class="cu-btn bg-gradual-green shadow-blur round" @tap="_searchCommunity()">搜索</button>
			</view>
		</view>

		<view>
			<view class="padding">店铺信息（只显示审核通过店铺）</view>
			<view class="cu-list menu ">
				<view class="cu-item" v-for="(item,sub) in shops" :key="sub" @tap="_doChangeCommunity(item)">
					<view class="content padding-tb-sm">
						<view>
							<text class="lg  cuIcon-homefill text-blue margin-right-xs"></text> {{item.shopName}}
						</view>
						<view class="text-gray text-sm">
							<text class="lg text-gray cuIcon-location margin-right-xs"></text> {{item.sendAddress}}
						</view>
					</view>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		getCommunity
	} from '../../api/community/community.js'
	
	import {loadShops} from '../../api/index/index.js';
	
	import mapping from '../../constant/mapping.js'
	export default {
		data() {
			return {
				communityName: '',
				shops: []
			}
		},
		onLoad() {
			this.java110Context.onLoad();
			this._loadShops();
		},
		methods: {
			_loadShops: function() {
				let _that = this
				let params = {
					page: 1,
					row: 50,
					state:'002',
					shopName:_that.communityName
				}
				loadShops(_that, params)
					.then(function(res) {
						_that.shops = res.data.data;
					});
			
			},
			_searchCommunity: function() {
				this._loadShops();
			},
			_doChangeCommunity: function(_shop) {
				uni.setStorageSync(mapping.CURRENT_SHOP_INFO, JSON.stringify(_shop));
				uni.navigateBack({
					delta: 1
				})
			}
		}
	}
</script>

<style>

</style>
