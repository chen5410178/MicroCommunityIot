<template>
	<view>
		<view class="cu-modal bottom-modal" :class="showModel?'show':''">
			<view class="cu-dialog " style="height: 700upx;">
				<view class="loading-container" v-if="startState == 'starting'">
					<view class="circular"></view>
				</view>
				<view v-else class="start-error">
					<text class="cuIcon-roundclosefill "></text>
				</view>
				<view class="start-msg">{{msg}}</view>
				<view class="charge-info" v-if="startState != 'fail'">启动中，请勿拔枪！</view>
				<view class="cu-bar btn-group" style="margin-top: 30px;" v-if="startState == 'fail'">
					<button @click="_closeChargeModal" class="cu-btn bg-blue shadow-blur round lg">关闭</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		startCarCharge,
		heartbeatCarCharge
	} from '@/api/machine/machineApi.js';
	export default {
		name: "startCarCharge",
		data() {
			return {
				showModel: false,
				msg: '开启中...',
				startState: 'starting',
				second: 0,
				setTimer: {},
				orderId: ''
			};
		},
		methods: {
			startCharge(param) {
				this.showModel = true;
				let _that = this;
				startCarCharge(param)
					.then(_data => {
						if (_data.code != 0) {
							_that.msg = _data.msg;
							_that.startState = 'fail';
							return;
						}
						_that.orderId = _data.data;
						_that.heartbeatStart();
					})
			},
			_closeChargeModal: function() {
				this.showModel = false;
				this.msg = '开启中...';
				this.startState = 'starting';
				this.second = 0;
				if (this.setTimer) {
					clearInterval(this.setTimer);
				}
			},
			heartbeatStart: function() {
				let _that = this;
				let setTimer = setInterval(
					() => {
						let second = _that.second + 1;
						_that.second = second;
						_that.msg = "等待充电桩就绪... " + second + '秒';
						if (_that.second % 5 == 0) {
							_that.heartbeatCharge();
						}
						if (_that.second > 300) {
							_that.msg = '等待充电桩就绪超时';
							_that.startState = 'fail';
							clearInterval(setTimer);
						}
					}, 1000);

			},
			heartbeatCharge: function() {
				let _that = this;
				heartbeatCarCharge({
						orderId: _that.orderId
					})
					.then(_data => {
						if (_data.code != 0) {
							_that.msg = _data.msg;
							_that.startState = 'fail';
							if (_that.setTimer) {
								clearInterval(_that.setTimer);
							}
							return;
						}

						let _order = _data.data;
						//todo 充电失败
						if (_order.state == '3003') {
							_that.msg = _order.remark;
							_that.startState = 'fail';
							if (_that.setTimer) {
								clearInterval(_that.setTimer);
							}
							return;
						}
						//todo 开启中
						if (_order.state == '1000') {
							return;
						}
						// todo 充电中,结束中，充电完成
						//if (_order.state == '1001') {
						_that._closeChargeModal();
						uni.navigateTo({
							url: '/pages/charge/chargeCarOrder?orderId=' + _data.data +
								'&communityId=' + this.communityId
						});
						//	return;
						//}
					});
			}
		}
	}
</script>

<style lang="scss">
	.loading-container {
		display: flex;
		justify-content: center;
		align-items: center;
		margin-top: 100upx;
		margin-bottom: 40upx;

	}

	.circular {
		width: 240upx;
		height: 240upx;
		border-radius: 50%;
		border: 14upx solid #ccc;
		border-top-color: #007bff;
		animation: rotate 1s linear infinite;
	}

	@keyframes rotate {
		from {
			transform: rotate(0deg);
		}

		to {
			transform: rotate(360deg);
		}
	}

	.start-msg {
		margin-top: 20upx;
		font-size: 32upx;
	}

	.start-error {
		font-size: 240upx;
		color: red;
		line-height: 1.0;
		margin-top: 100upx;
	}

	.charge-info {
		color: red;
		margin-top: 20upx;
	}
</style>