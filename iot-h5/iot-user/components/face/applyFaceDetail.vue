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
	import noDataPage from '../no-data-page/no-data-page.vue';
	import {listPersonFace} from '@/api/owner/ownerApi.js';
	export default {
		name:"applyFaceDetail",
		data() {
			return {
				faces:[]
			};
		},
		props: {
			communityId: {
				type: String,
			},
			openId:{
				type:String,
			}
		},
		components:{
			noDataPage
		},
		methods:{
			loadApplyFaceDetail:function(){
				let _that =this;
				listPersonFace({
					communityId:this.communityId,
					openId:this.openId,
					page:1,
					row:50
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