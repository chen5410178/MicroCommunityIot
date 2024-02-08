<template>
	<view>
		<view v-for="(item,index) in faces" :key="index"
			class="bg-white margin-bottom-xs margin-right-xs radius margin-left-xs padding">
			<view class="flex padding-bottom-xs solid-bottom justify-between">
				<view>{{item.personName}}</view>
				<view class="text-gray">{{item.stateName}}</view>
			</view>
			<view class="flex margin-top justify-between">
				<view class="text-gray">手机号</view>
				<view class="text-gray">{{item.personTel}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">申请时间</view>
				<view class="text-gray">{{item.createTime}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">申请房屋</view>
				<view class="text-gray">{{item.roomName}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">审核说明</view>
				<view class="text-gray">{{item.remark || '无'}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">人脸</view>
				<view class="text-gray">
				<image class="person-face" :src="item.faceUrl"></image>
				</view>
			</view>
		</view>
		<view v-if="!faces || faces.length < 1">
			<no-data-page></no-data-page>
		</view>
	</view>
</template>

<script>
	import noDataPage from '@/components/no-data-page/no-data-page.vue';
	import {getOwnerFaceDownload} from '../../api/owner/ownerApi.js';
	import {getCommunityId} from '../../api/community/communityApi.js';
	export default {
		data() {
			return {
				faces:[]
			}
		},
		components:{
			noDataPage
		},
		onLoad() {
			this._loadData();
		},
		methods: {
			_loadData:function(){
				let _that =this;
				getOwnerFaceDownload({
					page:1,
					row:30,
					communityId:getCommunityId()
				}).then(_data=>{
					_that.faces = _data;
				})
			}
		}
	}
</script>


<style lang="scss">
	.person-face{
		width: 120upx;
		height: 120upx;
		border-radius: 10upx;
	}
</style>
