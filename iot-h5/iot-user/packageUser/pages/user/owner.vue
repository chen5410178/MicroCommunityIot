<template>
	<view>
		<view class="block__title">业主信息</view>
		<view class="cu-list menu">
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">姓名</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{ownerName}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">手机号</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{link}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">类型</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{ownerTypeName}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">地址</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{address}}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {getUserOwner} from '@/api/owner/ownerApi.js';

	import {getCommunityId} from '@/api/community/communityApi.js';
	export default {
		data() {
			return {
				ownerName:'',
				link:'',
				ownerTypeName:'',
				address:'',
			}
		},
		onLoad(options) {
			
			this._loadOwnerInfo();
		},
		methods: {
			_loadOwnerInfo:function(){
				let _that =this;
				getUserOwner({
					communityId:getCommunityId()
				}).then(_data=>{
					if(_data && _data.length>0){
						_that.ownerName = _data[0].name;
						_that.link = _data[0].link;
						_that.ownerTypeName = _data[0].ownerTypeName;
						_that.address = _data[0].address;
					}
				});
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
