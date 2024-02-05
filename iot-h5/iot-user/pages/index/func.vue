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
	</view>
</template>

<script>
	export default {
		data() {
			return {
				orders:[
					{
						icon:'/static/images/ac.png',
						name:'我的小区',
						url:'/'
					},
					{
						icon:'/static/images/ac.png',
						name:'申请记录',
						url:'/'
					}
				]
			}
		},
		methods: {
			
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
