<template>
	<view class="scan-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="scan-bg">
			<view class="scan-frame">
				<view class="corner c1"></view>
				<view class="corner c2"></view>
				<view class="corner c3"></view>
				<view class="corner c4"></view>
				<view class="scan-line"></view>
			</view>

			<!-- 扫码按钮 -->
			<view class="scan-again-btn" @click="_startScan">
				<image :src="imgUrl + '/h5/images/charge/ic-scan.png'" class="scan-again-ic" mode="aspectFit"></image>
				<text class="scan-again-text">请点击扫码</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'

	export default {
		data() {
			return {
				chgType: chargeStore.chgType,
				scanTimer: null,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onLoad() {
			// 进入扫码页自动唤起微信扫一扫（仅进入时自动扫一次）
			this._startScan()
		},
		onShow() {
			// 【关键】onShow 禁止调起 uni.scanCode！
			// 原因：扫码成功 → switchTab 首页 → navigateTo 充电页后，从充电页返回时
			//       会先触发本页 onShow，若此处再自动扫码，将出现"返回回来又自动弹扫一扫、
			//       跳回扫码页"的死循环，用户无法正常关闭/返回。
			// onShow 仅做必要的状态同步（如 chgType），不做任何扫码动作
			this.chgType = chargeStore.chgType
		},
		onHide() {
			clearTimeout(this.scanTimer)
		},
		onUnload() {
			clearTimeout(this.scanTimer)
		},
		methods: {
			// #ifdef H5
			_startScan() {
				// H5 端 uni.scanCode 不可用，提示引导使用微信扫一扫充电
				clearTimeout(this.scanTimer)
				this._toast('请使用微信扫一扫充电')
			},
			// #endif
			// #ifdef MP-WEIXIN
			_startScan() {
				// 小程序真机扫码：调起扫码
				clearTimeout(this.scanTimer)
				uni.scanCode({
					onlyFromCamera: false,
					scanType: ['qrCode', 'barCode'],
					success: (res) => {
						this._matchScanResult(res.result)
					},
					fail: () => {
						// 用户取消扫码：停留在本页，可点击「扫一扫」按钮重新扫码
						this._toast('已取消扫码')
					}
				})
			},
			// #endif
			_matchScanResult(result) {
				// 扫码结果匹配桩编号：支持桩名/编号/位置关键字
				const no = String(result || '').replace(/\s/g, '')
				const list = chargeStore.curPiles()
				if (!list.length) {
					this._toast('暂无可识别的充电桩，请先回首页加载数据')
					return
				}
				let idx = list.findIndex(p => p.name.replace(/[^\w]/g, '') === no || p.machineId === no)
				if (idx < 0) idx = list.findIndex(p => p.name.replace(/[^\w]/g, '').indexOf(no) > -1)
				if (idx < 0) idx = 0
				this._toPile(idx, true)
			},
			_toPile(pileIdx, fromScan) {
				const piles = chargeStore.curPiles()
				const p = piles[pileIdx] || {}
				const url = this.chgType === 'car'
					? '/packageCharge/pages/charge/machineToCarCharge?machineId=' + (p.machineId || '') + '&communityId=' +
						chargeStore.curCommId()
					: '/packageCharge/pages/charge/machineToCharge?machineId=' + (p.machineId || '') + '&communityId=' +
						chargeStore.curCommId()
				// 先切回首页 tab，再打开充电页：从充电页返回时回到首页
				uni.switchTab({
					url: '/pages/index/index',
					success: () => {
						// switchTab 完成后延时打开充电页，避免时序问题
						setTimeout(() => {
							uni.navigateTo({
								url: url
							})
						}, 300)
					}
				})
			},
			_toast(msg) {
				this.toastMsg = msg
				clearTimeout(this._toastTimer)
				this._toastTimer = setTimeout(() => {
					this.toastMsg = ''
				}, 2000)
			}
		}
	}
</script>

<style>
	.scan-page {
		height: 100vh;
		background: #000;
		position: relative;
		overflow: hidden;
	}

	.scan-bg {
		position: relative;
		height: 100%;
		background: radial-gradient(circle at 50% 42%, #1a2b22 0%, #0a120e 55%, #050805 100%);
	}

	.scan-frame {
		position: absolute;
		left: 50%;
		top: 36%;
		transform: translate(-50%, -50%);
	}

	.scan-again-btn {
		position: absolute;
		left: 50%;
		top: 50%;
		transform: translate(-50%, -50%);
		width: 420rpx;
		padding: 44rpx 0;
		border-radius: 32rpx;
		background: rgba(255, 255, 255, .08);
		border: 1rpx solid rgba(255, 255, 255, .2);
		color: #fff;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 24rpx;
	}

	.scan-again-ic {
		width: 180rpx;
		height: 180rpx;
	}

	.scan-again-text {
		color: #fff;
		font-size: 28rpx;
		font-weight: 500;
	}
</style>
