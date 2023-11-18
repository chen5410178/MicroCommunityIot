<template>
	<view>
		<my-person ref="myPersonRef"></my-person>

		<!-- <my-menu></my-menu> -->

		<my-system ref="mySystem"></my-system>
	</view>
</template>

<script>
	/** index.js **/

	import conf from '@/conf/config.js';

	import {
		hasOwner
	} from '@/api/owner/ownerApi.js'
	import {
		queryOwnerAccount
	} from '@/api/user/userApi.js'

	import myPerson from '@/components/index/my-person.vue'
	// import myMenu from '@/components/index/my-menu.vue'
	import mySystem from '@/components/index/my-system.vue'
	export default {
		data() {
			return {
				property: {},
				// 用户信息	
			};
		},
		components: {
			myPerson,
			//myMenu,
			mySystem
		},
		onLoad: function(options) {
			this.vc.onLoad(options, () => {
				this.loadOwnerAccount();
			});
		},
		onShow: function() {
			let _that = this; //查询用户信息
			if (this.$refs.myPersonRef) {
				this.$refs.myPersonRef.refreshPageLoginInfo();
			}
			if (this.$refs.mySystem) {
				this.$refs.mySystem._judgeHasLogin();
			}

		},
		methods: {
			// 原onShow方法
			// 自动登录后 刷新页面登录信息
			onGotUserInfo: function(e) {
				console.log("nickname=" + JSON.stringify(e.detail.userInfo));
			},
			// 我的服务跳转
			to_serve(v) {
				this.vc.navigateTo({
					url: v.href,
				})
			},
		}
	};
</script>
<style lang="less">
	/**index.wxss**/
	.user-container {
		padding: 50rpx 0;
		/*border: 1px solid black;*/
	}
	
	.userinfo {
	  display: flex;
	  flex-direction: column;
	  align-items: center;
	}
	
	.userinfo-avatar {
	  display:block; 
		overflow:hidden; 
		border-radius: 50%;
		width: 200rpx;
		height: 200rpx;
	}
	
	.userinfo-nickname {
	  color: #aaa;
	}
	
	.tab-container {
		/*border: 1px solid black;*/
		
		margin-top: 30rpx;
	}
	
	.tab-item {
		padding: 20rpx 30rpx;
	
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}
	
	.tab-item-hover {
		background-color: #e6e6e6;
	}
	
	.tab-icon {
		width: 30rpx;
		height: 30rpx;
	}
	
	.tab-text {
		display: inline-block;
		margin-left: 10rpx;
		color: #1e1e1e;
	}
	
	.tab-arrow {
		display: inline-block;
		width: 20rpx;
		height: 20rpx;
		border: 1px solid #cdcdcd;
	
		border-left: none;
		border-bottom: none;
	
		transform: rotate(45deg);
	}
	
	.border-bottom .icon{
	  font-size: 38rpx;
	  line-height: 38rpx;
	}
</style>
