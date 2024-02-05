<template>
	<view>
		<view class="header flex justify-around align-center">
			<view class="header-item " v-for="(item,index) in headerMenus" :key="index" @click="_toPage(item)">
				<view style="position: relative;">
					<image :src="item.icon"></image>
					<view><text>{{item.name}}</text></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {hasLogin} from '../../api/user/sessionApi.js'
	export default {
		name: "indexHeader",
		data() {
			return {
				headerMenus:  [{
					icon:'/static/images/qrcode.png',
					name: '通行码',
					url: '/pages/complaintList/complaintList'
				}, {
					icon:'/static/images/qrcode-visit.png',
					name: '访客码',
					url: '/pages/repairDispatch/repairDispatch'
				}]
			};
		},
		methods: {
			
			_toPage: function(_item) {
				
				console.log('跳转',_item)
				
				if(!hasLogin()){
					uni.navigateTo({
						url:'/pages/user/login'
					});
					return;
				}
				
				uni.navigateTo({
					url: _item.url
				})
			}
		}
	}
</script>

<style lang="scss">
	.header {
		width: 100%;
		/** #ifndef H5 **/
		height: 320upx;
		/** #endif **/
		/** #ifdef H5 **/
		height: 320upx;
		/** #endif **/
		background-color: #368CFE;
		color: #FFFFFF;

		.header-item {
			//margin-top: 50upx;
			text-align: center;

			image {
				height: 100upx;
				width: 100upx;
			}
		}
	}
</style>
