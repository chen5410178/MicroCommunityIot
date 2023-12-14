<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isShowSpec?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white ">
					<view class="action">
						<text class="cuIcon-title text-orange "></text>{{productInfo.prodName}}
					</view>
					<view class="action">
						<button class="cu-btn line-blue round sm" @click="_closeSelectGoodsSpec()">关闭</button>
					</view>
				</view>
				<scroll-view scroll-y style="height: 700upx;">
					<block>
						<view>
							<view class="cu-list menu-avatar no-padding">
								<view class="cu-item arrow" v-for="(items,sub) in productInfo.productSpecValues" :key="sub"
									@click="_selectGoodsSpec(items)">
									<view class="cu-avatar round lg">{{items.specName[0]}}</view>
									<view class="content">
										<view class="text-grey"><text class="text-abc">{{items.specName}}{{_getSpecDetailName(items.productSpecDetails)}}</text></view>
									</view>
									<view class="action">
										<button class="cu-btn line-blue round sm">选择</button>
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
		loadProduct
	} from '@/api/product/productApi.js'
	export default {
		name: "selectGoodsSpec",
		data() {
			return {
				productInfo: {},
				isShowSpec: false,
			};
		},
		methods: {
			queryProduct: function(_condition) {
				_condition.page=1;
				_condition.row=1;
				_condition.shopId=this.getCurrentShop().shopId;
				loadProduct(this, _condition)
					.then(_data => {
						if (!_data.data || _data.data.length < 1) {
							uni.showToast({
								icon: 'none',
								title: '未找到商品',
							})
							return;
						}
						this.productInfo = _data.data[0];
						console.log(this.productInfo)
						if(this.productInfo.productSpecValues.length == 1){
							this.$emit('getGoodsSpec',this.productInfo.productSpecValues[0],this.productInfo);
							return ;
						}
						this.isShowSpec = true;
					})
			},
			_getSpecDetailName: function(_details) {
				if (!_details || _details.length < 1) {
					return '';
				}
				let _value = '';
				_details.forEach(_item => {
					_value += (_item.detailName + ":" + _item.detailValue + ";")
				})
				return _value;
			},
			_selectGoodsSpec:function(_spec){
				this.$emit('getGoodsSpec',_spec,this.productInfo);
				this.isShowSpec = false;
			},
			_closeSelectGoodsSpec:function(){
				this.isShowSpec = false;
			}
		}
	}
</script>

<style>

</style>
