<template>
	<view>
		<view class="block__title">业主信息</view>
		<view class="cu-list menu">
			<view class="cu-item">
				<view class="content">
					<text class="cuIcon-profile text-green"></text>
					<text class="text-grey">姓名</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{ownerName}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="cuIcon-phone text-green"></text>
					<text class="text-grey">手机号</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{link}}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {getUserOwner} from '../../api/owner/ownerApi.js';
	import {hasLogin} from '../../api/user/sessionApi.js';
	import {getCommunityId} from '../../api/community/communityApi.js';
	export default {
		data() {
			return {
				ownerName:'',
				link:''
			}
		},
		onLoad(options) {
			if(!hasLogin()){
				uni.showToast({
					icon:'none',
					title:'用户未登录'
				});
				return;
			}
			this._loadOwnerInfo();
		},
		methods: {
			_loadOwnerInfo:function(){
				getUserOwner({
					communityId:getCommunityId()
				})
			}
		}
	}
</script>

<style lang="scss">
.block__title {
  margin: 0;
  font-weight: 400;
  font-size: 14px;
  color: rgba(69,90,100,.6);
  padding: 60rpx 30rpx 20rpx;
}


.button_up_blank{
  height: 40rpx;
}
</style>
