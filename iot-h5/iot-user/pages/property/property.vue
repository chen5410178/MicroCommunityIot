<template>
	<view>
		<view class="block__title">物业信息</view>
		<view class="cu-list menu">
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">名称</text>
				</view>
				<view class="action">
					<text class="text-grey text-sm">{{storeName}}</text>
				</view>
			</view>
			<view class="cu-item">
				<view class="content">
					<text class="text-grey">联系电话</text>
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
	import {getUserProperty,getCommunityId} from '../../api/community/communityApi.js'
	export default {
		data() {
			return {
				storeName:'',
				link:''
			}
		},
		onLoad(options) {
			this._loadProperty();
		},
		methods: {
			_loadProperty(){
				getUserProperty({
					communityId:getCommunityId()
				}).then(_data=>{
					
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
