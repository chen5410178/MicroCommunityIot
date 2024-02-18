<template>
	<view>
		<view class="real_list">
			<view class="title">开门服务</view>
			<view class="list">
				<view class="item" v-for="(item,index) in orders" :key="index" @tap="to(item)">
					<image :src="item.icon"></image>
					<view class="text">{{item.name}}</view>
				</view>
			</view>
		</view>
		<view class="real_list">
			<view class="title">充值服务</view>
			<view class="list">
				<view class="item" v-for="(item,index) in charges" :key="index" @tap="to(item)">
					<image :src="item.icon"></image>
					<view class="text">{{item.name}}</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {hasLogin} from '@/api/user/sessionApi.js';
import { getCommunityId } from '../../api/community/communityApi.js';
import {getRoomId,getRoomName} from '@/api/community/roomApi.js';
	import {
		getAppUser
	} from '../../api/user/appUser.js';
	
	import {getUserId,getUserTel} from '@/api/user/userApi.js'
	
	export default {
		data() {
			return {
				orders:[
					
					{
						icon:'/static/images/ac.png',
						name:'门禁人脸',
						url:'/pages/user/personFaceCollection'
					},
					{
						icon:'/static/images/ac.png',
						name:'人脸下发',
						url:'/pages/user/faceDownloadAc'
					},
					{
						icon:'/static/images/ac.png',
						name:'访客记录',
						url:'/pages/visit/ownerVisit'
					}
				],
				charges:[
					{
						icon:'/static/images/ac.png',
						name:'水电充值',
						url:'chargeMeter'
					},
					{
						icon:'/static/images/ac.png',
						name:'停车月卡',
						url:'buyMonthCarCard'
					},
					{
						icon:'/static/images/ac.png',
						name:'充电月卡',
						url:'buyMonthChardCard'
					}
				]
			}
		},
		methods: {
			to:function(_item){
				if(!hasLogin()){
					uni.navigateTo({
						url:'/pages/user/login'
					});
					return;
				}
				
				let _that = this;
				getAppUser().then(_data => {
					_that.toUrl(_item);
				}, err => {
					uni.showToast({
						icon:'none',
						title:'未认证房屋'
					})
				})
			},
			toUrl:function(_item){
				let _url = _item.url;
				if(_item.url == 'chargeMeter'){
					_url = "/pages/meter/selectMeter?communityId="+getCommunityId()
					+"&createStaffId="+getUserId()+"&roomId="+getRoomId()+"&roomName="+getRoomName()
					uni.navigateTo({
						url:_url
					});
					return ;
				}
				if(_item.url == 'buyMonthCarCard'){
					_url = "/pages/car/selectCar"
					uni.navigateTo({
						url:_url
					});
					return ;
				}
				if(_item.url == 'buyMonthChardCard'){
					_url = "/pages/charge/prestoreChargeCard?communityId="+getCommunityId()+"&personTel="+getUserTel()
					uni.navigateTo({
						url:_url
					});
					return ;
				}
				uni.navigateTo({
					url:_item.url
				})
			}
		}
	}
</script>

<style lang="less">
	.real_list,
	.live_list {
		background: #fff;
		padding: 0 40upx;
		margin-top: 20upx;

		.title {
			line-height: 32upx;
			background: #fff;
			font-size: 30upx;
			font-weight: 600;
			color: #333;
			padding: 40upx 0;
			margin-top: 10px;
		}

		.item {
			width: 25%;
			display: inline-block;
			text-align: center;
			margin-bottom: 40upx;
		}

		image {
			height: 68upx;
			width: 68upx;
			margin-bottom: 8upx;
		}

		.text {
			color: #333;
			font-size: 28upx;
		}
	}
</style>
