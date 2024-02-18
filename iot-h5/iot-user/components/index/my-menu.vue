<template>
	<view>
		<view class="serve_box">
			<view class="serve_title">基础服务</view>
			<view class="serve_list">
				<view class="list">
					<view class="item" v-for="(item,index) in serve_list" :key="index" @tap="to_serve(item)">
						<image :src="item.src"></image>
						<view class="text">{{item.name}}</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {hasLogin} from '../../api/user/sessionApi.js';
	export default {
		name: "myMenu",
		data() {
			return {
				serve_list: [{
						name: '业主信息',
						src: this.imgUrl+'/h5/images/serve/my1.png',
						href: '/pages/user/owner'
					},
					{
						name: '我的物业',
						src: this.imgUrl+'/h5/images/serve/my2.png',
						href: '/pages/property/property'
					},
					{
						name: '我的房屋',
						src: this.imgUrl+'/h5/images/serve/my3.png',
						href: '/pages/room/room'
					},
					{
						name: '停车月卡记录',
						src: this.imgUrl+'/h5/images/serve/my4.png',
						href: '/pages/car/carMonthOrder'
					},
					{
						name: '充电月卡记录',
						src: this.imgUrl+'/h5/images/serve/my5.png',
						href: '/pages/fee/payFeeDetail'
					},
					{
						name: '充电订单',
						src: this.imgUrl+'/h5/images/serve/my5.png',
						href: '/pages/fee/payFeeDetail'
					},
				]
			};
		},
		created() {

		},
		methods: {
			// 我的服务跳转
			to_serve(v) {
				if(!hasLogin()){
					uni.navigateTo({
						url:'/pages/user/login'
					})
					return;
				}
				this.vc.navigateTo({
					url: v.href,
				})
			},
		}
	}
</script>

<style lang="less">
	

	.serve_box {
		margin-top:20upx;
		.serve_title {
			line-height: 42upx;
			font-size: 30upx;
			font-weight: 600;
			color: #333;
			padding: 20upx;
		}

		.serve_list {
			padding: 40upx 20upx 0;
			background: #fff;
		}

		.item {
			width: 25%;
			display: inline-block;
			text-align: center;
			margin-bottom: 40upx;
		}

		image {
			height: 44upx;
			width: 44upx;
		}

		.text {
			color: #333;
			font-size: 28upx;
		}
	}
</style>
