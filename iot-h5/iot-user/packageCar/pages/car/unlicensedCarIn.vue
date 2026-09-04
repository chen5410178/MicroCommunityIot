<template>
	<view>
		<view class="plate-context">
			<view class="ak_row_black">
			</view>
			<view class="ak_row_title">
				<text>{{communityName}}</text>
			</view>

			<view class="tips_block">
			</view>

			<view class="tips">
				<text>自动生成车牌号</text>
			</view>
			<view class="plate-num-display">{{carNum || '加载中...'}}</view>

			<view class="plat-btn-black"></view>
			<view class="cu-bar btn-group" style="margin-top: 30px;">
				<button @click="_saveCarNum" :disabled="!carNum || submitting"
					class="cu-btn bg-green shadow-blur round lg">立即入场</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		customCarInOut,
		getOrGenerateUnlicensedCarNum
	} from '@/api/car/carApi.js'
	import {
		isNotNull
	} from '@/lib/java110/utils/StringUtil.js'
	import {
		getCommunityWechatAppId,
		refreshUserOpenId
	} from '@/api/user/userApi.js'
	import {
		isWxOrAli
	} from '@/lib/java110/utils/EnvUtil.js'
	import {
		formatTimeNow
	} from '@/lib/java110/utils/DateUtil.js'
	export default {
		data() {
			return {
				carNum: '',
				communityId: '',
				communityName: '',
				appId: '',
				machineId: '',
				openId: '',
				submitting: false,
			}
		},
		mounted() {
			// #ifdef H5
			if (isWxOrAli() == "ALIPAY") {
				const oScript = document.createElement('script');
				oScript.type = 'text/javascript';
				oScript.src = 'https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.min.js';
				document.body.appendChild(oScript);
			}
			// #endif
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.machineId = options.machineId;
			this.appId = options.appId;
			this.openId = options.openId || '';
			uni.setStorageSync("wAppId", this.appId);
			if (isNotNull(this.openId)) {
				this._loadUnlicensedCarNum();
				return;
			}
			if (isWxOrAli() == 'ALIPAY') {
				this._refreshAliPayOpenId();
			} else {
				this._refreshWechatOpenId();
			}
		},
		methods: {
			_saveCarNum: function() {
				if (this.submitting) {
					return;
				}
				let _that = this;
				this.submitting = true;
				customCarInOut({
					communityId: _that.communityId,
					carNum: _that.carNum,
					machineId: _that.machineId,
					type: '1101',
					unlicense: 'T'
				}).then(_data => {
					_that.submitting = false;
					if (_data.data.code != 0) {
						uni.showToast({
							icon: 'none',
							title: _data.data.msg ||'车牌号提交失败！'
						})
						return;
					}
					// 进场时间：提交成功时刻（方案A）
					const inTime = formatTimeNow();
					uni.redirectTo({
						url: '/packageCar/pages/car/entrySuccess?carNum=' + encodeURIComponent(_that.carNum) +
							'&inTime=' + encodeURIComponent(inTime)
					})
				}).catch(() => {
					_that.submitting = false;
				})
			},
			_refreshWechatOpenId: function() {
				let _that = this;
				if (!this.appId || this.appId.indexOf('wx') < 0) {
					getCommunityWechatAppId({
						communityId: this.communityId,
						objType: '1100'
					}).then(_data => {
						if (_data.data && _data.data.indexOf('wx') >= 0) {
							_that.appId = _data.data;
							_that._doRefreshWechatOpenId();
						} else {
							uni.showToast({ icon: 'none', title: '未配置公众号' });
						}
					});
					return;
				}
				this._doRefreshWechatOpenId();
			},
			_doRefreshWechatOpenId: function() {
				let _redirectUrl = window.location.href;
				refreshUserOpenId({
					redirectUrl: _redirectUrl,
					wAppId: this.appId,
				}).then(_data => {
					if (_data.code == 0) {
						window.location.href = _data.data.openUrl;
					}
				});
			},
			_refreshAliPayOpenId: function() {
				ap.getAuthCode({
					appId: this.appId,
					scopes: ['auth_base'],
				}, function(res) {
					uni.showToast({
						title: JSON.stringify(res),
						icon: 'none'
					})
				});
			},
			_loadUnlicensedCarNum: function() {
				let _that = this;
				let _openType = isWxOrAli() == 'ALIPAY' ? 'ALIPAY' : 'WECHAT';
				getOrGenerateUnlicensedCarNum({
					openId: this.openId,
					openType: _openType,
					communityId: this.communityId
				}).then(_data => {
					_that.carNum = _data.carNum;
				});
			}
		}
	}
</script>

<style>
	.pages {
		padding: 20rpx 20rpx;
	}

	.pages .plate-context {
		background-color: #fff;
		padding: 40rpx 20rpx 80rpx 20rpx;
	}

	.ak_row_title {
		padding-left: 20rpx;
		font-size: 32rpx;
		font-weight: 700;
	}

	.tips_block {
		height: 60rpx;
	}

	.tips {
		text-align: center;
		margin: 60rpx 0;
		font-size: 12pt;
		color: #888888;
	}

	.plate-num-display {
		text-align: center;
		font-size: 48rpx;
		font-weight: 700;
		color: #333;
		padding: 40rpx 0;
		letter-spacing: 8rpx;
	}

	.plat-btn-black {
		height: 100rpx;
	}
</style>
