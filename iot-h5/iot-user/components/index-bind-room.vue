<template>
	<view class="index-undo" >
		<view class="undo-menu flex justify-start flex-wrap">
			<view class="menu-item"  @click="_toBindRoom()">
				<view style="position: relative;">
					<image src="/static/images/ac.png"></image>
					<view><text>认证房屋</text></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {hasLogin} from '../api/user/sessionApi.js';
	export default {
		name:"indexBindRoom",
		data() {
			return {
				
			};
		},
		methods:{
			_toBindRoom:function(){
				//todo 没有登陆跳转至登陆页面
				if(!hasLogin()){
					uni.navigateTo({
						url:'/pages/user/login'
					});
					return;
				}
				uni.navigateTo({
					url:'/'
				})
			}
		}
	}
</script>

<style lang="scss">
	.index-undo {
		background-color: #ffffff;

		
		.undo-menu{
			padding:20upx;
			.menu-item{
				
				width: 100%;
				padding: 50upx;
				text-align: center;
				image{
					width: 160upx;
					height: 160upx;
				}
			}
		}
	}
</style>