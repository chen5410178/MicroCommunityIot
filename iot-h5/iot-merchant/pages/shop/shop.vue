<template>
	<view>
		<view class="margin-top">
			<view class="cu-list menu">
				<view class="cu-item arrow" @tap="_myStore()">
					<view class="content">
						<text class="lg text-gray cuIcon-friendaddfill"></text>
						<text class="text-grey">我的商家</text>
					</view>
				</view>
				<view class="cu-item arrow" @tap="_toShopIndex()">
					<view class="content">
						<text class="lg text-gray cuIcon-lock"></text>
						<text class="text-grey">访问商铺</text>
					</view>
				</view>
				
				
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			}
		},
		methods: {
			_myStore:function(){
				uni.navigateTo({
					url:"/pages/userInfo/userInfo"
				});
			},
			_toShopIndex:function(){
				uni.navigateTo({
					url:"/pages/shop/shopQrCode"
				});
			}
		}
	}
</script>

<style>

</style>
