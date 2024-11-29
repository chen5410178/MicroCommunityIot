<template>
	<view class=" margin-left-sm margin-right-sm">
		<view class=" bg-white padding-sm border-radius">
			<view class="flex justify-between">
				<view class="margin-left-sm">￥<text class="price-num">{{_computeTotalPrice()}}</text>/度</view>
				<view class="price-more" @click="_viewPrices()">查看其他时段价格</view>
			</view>
			<view class="flex justify-between cur-price">
				<view class="text-center">
					<view>{{curTime}}</view>
					<view>当前时段</view>
				</view>
				<view class="text-center">
					<view>￥{{price}}/度</view>
					<view>电费</view>
				</view>
				<view class="text-center">
					<view>￥{{servicePrice}}/度</view>
					<view>服务费</view>
				</view>
			</view>
		</view>
		<view class="cu-modal bottom-modal" :class="showModel?'show':''">
			<view class="cu-dialog padding-left padding-right padding-bottom-lg" >
				<view class="flex justify-between price-title">
					<view>充电价格明细</view>
					<view @click="_closeChargeModal"><text class="cuIcon-roundclose "></text></view>
				</view>
				<view class="flex justify-between text-center table-th">
					<view class="price-item">当前时段</view>
					<view class="price-item">电费</view>
					<view class="price-item">服务费</view>
				</view>
				<view class="flex justify-between text-center" v-for="(item,index) in prices" :key="index">
					<view class="price-item">{{item.startTime}}~{{item.endTime}}</view>
					<view class="price-item">{{item.price}}</view>
					<view class="price-item">{{item.servicePrice}}</view>
				</view>
			</view>
		</view>
		<!-- <view class=" mtc-hours-title">
			<view>充电服务价格明细</view>
		</view>
		<view class="flex justify-start mtc-machine-hours">
			<view class="grid margin-bottom text-center" v-for="(item,index) in prices" :key="index">
				<view class="flex flex-wrap">
					<view class="margin-xs padding-sm radius">{{index+1}}</view>
					<view class="margin-xs padding-sm radius">{{item.startTime}} ~ {{item.endTime}}</view>
					<view class="margin-xs padding-sm radius">{{item.price}}</view>
					<view class="margin-xs padding-sm radius">{{item.servicePrice}}</view>
				</view>
			</view>

		</view> -->
	</view>
</template>

<script>
	export default {
		name: "carChargePrice",
		data() {
			return {
				curTime: '17:00-23:00',
				price: '0.8209',
				servicePrice: '0.1',
				showModel:false
			};
		},
		props: {
			prices: {
				type: Array,
				require: true
			}
		},
		methods: {
			_computeTotalPrice: function() {
				let _totalPrice = parseFloat(this.price) + parseFloat(this.servicePrice);
				return _totalPrice.toFixed(2);
			},
			_viewPrices:function(){
				this.showModel = true;
			},
			_closeChargeModal:function(){
				this.showModel =false;
			},
			initPrice: function(_prices) {
				const currentDate = new Date();
				const currentHour = currentDate.getHours();
				const currentMinute = currentDate.getMinutes();
				const currentTotalMinutes = currentHour * 60 + currentMinute;
				let _that =this;
				_prices.forEach(_p => {
					if(_that.isTimeBetween(_p.startTime,_p.endTime)){
						_that.curTime = _p.startTime+"~"+_p.endTime;
						_that.price = _p.price;
						_that.servicePrice = _p.servicePrice;
					}
				})
			},
			isTimeBetween:function (startTime, endTime) {
			    // 获取当前时间的小时和分钟
			    const currentDate = new Date();
			    const currentHour = currentDate.getHours();
			    const currentMinute = currentDate.getMinutes();
			    const currentTotalMinutes = currentHour * 60 + currentMinute;

			    // 将开始时间和结束时间转换为分钟数
			    const [startHour, startMinute] = startTime.split(':').map(Number);
			    const startTotalMinutes = startHour * 60 + startMinute;
			
			    const [endHour, endMinute] = endTime.split(':').map(Number);
			    let endTotalMinutes;
			
			    // 判断是否跨天
			    if (endHour < startHour || (endHour === startHour && endMinute < startMinute)) {
			        // 如果跨天，先计算当天结束时间对应的分钟数
			        const endTodayTotalMinutes = (24 + endHour) * 60 + endMinute;
			        // 再计算下一天开始时间对应的分钟数（即开始时间原本对应的分钟数）
			        const startTomorrowTotalMinutes = startHour * 60 + startMinute;
			
			        // 判断当前时间是否在跨天的时间范围内
			        if (currentTotalMinutes >= startTotalMinutes && currentTotalMinutes <= endTodayTotalMinutes) {
			            return true;
			        } else if (currentTotalMinutes >= 0 && currentTotalMinutes <= startTomorrowTotalMinutes) {
			            return true;
			        } else {
			            return false;
			        }
			    } else {
			        endTotalMinutes = endHour * 60 + endMinute;
			        const currentTotalMinutes = currentHour * 60 + currentMinute;
			
			        // 判断当前时间是否在指定时间范围内
			        return currentTotalMinutes >= startTotalMinutes && currentTotalMinutes <= endTotalMinutes;
			    }
			}
		}
	}
</script>

<style lang="scss">
	.price-num {
		font-size: 46upx;
		color: #F39053;
		font-weight: 800;
	}

	.price-more {
		color: #0FBA82;
		line-height: 3;
		text-decoration: underline;
	}

	.cur-price {
		margin-top: 32upx;
		font-size: 26upx;
		color: #777;
		margin-right: 10upx;
		margin-left: 10upx;
	}
	.price-title{
		font-size: 32upx;
		margin-top: 26upx;
		font-weight: 800;
		color: #555;
		text{
			color: red;
		}
	}
	
	.table-th{
		font-size: 32upx;
		margin-top: 30upx;
		color: #333;
	}
	
	.price-item{
		width: 33.33%;
		margin-top:20upx;
	}
	
	
	.mtc-hours-title {
		background-color: #fff;
		font-size: 28upx;
		color: #000;
		padding-left: 20upx;
		padding-right: 20upx;
		margin: 20upx 20upx 0upx 20upx;
		border-top-left-radius: 20upx;
		border-top-right-radius: 20upx;
		height: 60upx;
		line-height: 60upx;
		border-bottom: 2upx;
	}

	.mtc-machine-hours {
		background-color: #fff;
		padding-left: 20upx;
		padding-right: 20upx;
		padding-bottom: 20upx;
		margin: 2upx 20upx 0upx 20upx;

		flex-wrap: wrap;

		.hours-item {
			width: 33.33%;
			text-align: center;

			.item-1 {
				border: 1px solid #000;
				width: 90%;
				margin: 0upx auto;
				margin-top: 25upx;
				border-radius: 15upx;
				color: #000;

				.port-name {
					font-size: 40upx;
				}
			}

			.item-1-active {
				border: 1px solid #0FBA82;
				background-color: #0FBA82;
				color: #fff;
			}
		}
	}
</style>