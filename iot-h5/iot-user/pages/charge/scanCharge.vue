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

			<!-- 提示 -->
			<view class="scan-tip">
				<text class="scan-tip-main">对准充电桩上的二维码</text>
				<text class="scan-tip-sub">扫码后将自动识别桩位并开始充电配置</text>
				<view class="scan-recognizing">
					<image src="/static/h5/images/common/ic-plus.png" class="scan-rec-icon" mode="aspectFit"></image>
					<text class="scan-rec-text" id="scan-tip">{{ scanTip }}</text>
				</view>
			</view>

			<!-- 扫码测试：电动车 / 电瓶车 -->
			<view class="scan-test">
				<text class="scan-test-label">扫码测试 · 选择桩类型：</text>
				<view class="scan-test-row">
					<view class="scan-test-btn car" @click="scanTest('car')">
						<image src="/static/h5/images/charge/ic-car-light.png" class="scan-test-ic" mode="aspectFit"></image>
						<text>电动车桩</text>
					</view>
					<view class="scan-test-btn bike" @click="scanTest('bike')">
						<image src="/static/h5/images/charge/ic-bike-light.png" class="scan-test-ic" mode="aspectFit"></image>
						<text>电瓶车桩</text>
					</view>
				</view>
			</view>

			<!-- 手动输入 -->
			<view class="scan-manual">
				<text class="scan-type-text">当前识别类型：<text class="scan-type-val">{{ chgTypes[chgType].label }}桩</text> · 自动识别</text>
				<view class="scan-manual-btn" @click="manualPile">手动输入桩编号</view>
				<view class="scan-offline-btn" @click="scanOffline">模拟扫码离线桩 · 测试禁用体验</view>
			</view>
		</view>

		<!-- 手动输入桩编号弹层 -->
		<view class="c-mask" v-if="manualShow" @click="manualShow = false"></view>
		<view class="c-dialog" v-if="manualShow">
			<view class="c-title" style="text-align:center;">手动输入桩编号</view>
			<view class="c-form-input" style="margin-top:28rpx;">
				<input v-model="manualNo" placeholder="请输入桩编号，如 B12" maxlength="20" />
			</view>
			<view class="manual-pile-list" v-if="matchedPiles.length">
				<view class="manual-pile-item" v-for="(p, i) in matchedPiles" :key="i" @click="openMatchedPile(i)">
					<view>
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ p.name }}</text>
						<text class="c-text-sub" style="display:block;margin-top:4rpx;">{{ p.loc }}</text>
					</view>
					<text class="c-badge" :class="p.online === false ? 'c-badge-gray' : 'c-badge-green'">{{ p.online === false ? '离线' : '在线' }}</text>
				</view>
			</view>
			<view class="manual-pile-empty" v-else-if="manualNo && manualSearched">
				<text class="c-text-sub">未匹配到桩编号「{{ manualNo }}」</text>
			</view>
			<view class="c-btn-ghost" style="margin-top:28rpx;" @click="manualShow = false">取消</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		chgTypes
	} from '@/api/mock/chargeMockData.js'

	export default {
		data() {
			return {
				chgTypes,
				chgType: 'bike',
				scanTip: '正在识别…',
				scanTimer: null,
				manualShow: false,
				manualNo: '',
				manualSearched: false,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			matchedPiles() {
				if (!this.manualNo) return []
				const no = this.manualNo.replace(/\s/g, '').toLowerCase()
				return chargeStore.curPiles().filter(p => p.name.replace(/[^\w]/g, '').toLowerCase().indexOf(no) > -1 ||
					p.loc.replace(/[^\w]/g, '').toLowerCase().indexOf(no) > -1)
			}
		},
		onShow() {
			this.chgType = chargeStore.chgType
			this.scanTip = '正在识别…'
			this._startScan()
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
				// H5 端 uni.scanCode 不可用，提示引导使用微信扫一扫充电，保留手动输入桩编号兜底
				clearTimeout(this.scanTimer)
				this.scanTip = '请使用微信扫一扫充电'
				this._toast('请使用微信扫一扫充电')
			},
			// #endif
			// #ifdef MP-WEIXIN
			_startScan() {
				// 小程序真机扫码：调起扫码；失败/取消时降级为模拟识别
				clearTimeout(this.scanTimer)
				this.scanTip = '正在识别…'
				uni.scanCode({
					onlyFromCamera: false,
					scanType: ['qrCode', 'barCode'],
					success: (res) => {
						this.scanTip = '识别成功 · ' + this.chgTypes[this.chgType].label + '桩'
						this._matchScanResult(res.result)
					},
					fail: () => {
						// 模拟识别兜底
						this.scanTimer = setTimeout(() => {
							const p = chargeStore.curPiles()[0]
							this.scanTip = '识别成功 · ' + this.chgTypes[this.chgType].label + '桩 ' + (p ? p
								.name : '')
							this._toPile(0, true)
						}, 2200)
					}
				})
			},
			// #endif
			_matchScanResult(result) {
				// 扫码结果匹配桩编号：支持桩名/编号/位置关键字
				const no = String(result || '').replace(/\s/g, '')
				const list = chargeStore.curPiles()
				let idx = list.findIndex(p => p.name.replace(/[^\w]/g, '') === no || p.machineId === no)
				if (idx < 0) idx = list.findIndex(p => p.name.replace(/[^\w]/g, '').indexOf(no) > -1)
				if (idx < 0) idx = 0
				this._toPile(idx, true)
			},
			// 扫码测试：直接选择电动车 / 电瓶车桩进行模拟扫码
			scanTest(type) {
				clearTimeout(this.scanTimer)
				if (this.chgType !== type) {
					this.chgType = type
					chargeStore.setChgType(type)
				}
				this.scanTip = '正在识别 ' + this.chgTypes[type].label + '桩二维码…'
				this._toast('开始扫描' + this.chgTypes[type].label + '桩二维码')
				this.scanTimer = setTimeout(() => {
					const p = chargeStore.curPiles()[0]
					this.scanTip = '识别成功 · ' + this.chgTypes[type].label + '桩 ' + (p ? p.name : '')
					this._toast('已识别' + this.chgTypes[type].label + '桩 · ' + (p ? p.name : ''))
					this._toPile(0, true)
				}, 1600)
			},
			// 模拟扫码离线桩
			scanOffline() {
				clearTimeout(this.scanTimer)
				const list = chargeStore.curPiles()
				const offIdx = list.findIndex(p => p.online === false)
				if (offIdx < 0) {
					this.scanTip = '当前小区暂无离线' + this.chgTypes[this.chgType].label + '桩'
					this._toast('当前小区无离线桩 · 切换小区后再试')
					return
				}
				const p = list[offIdx]
				this.scanTip = '正在识别 ' + this.chgTypes[this.chgType].label + '桩二维码…'
				this._toast('开始扫描' + this.chgTypes[this.chgType].label + '桩二维码')
				this.scanTimer = setTimeout(() => {
					this.scanTip = '识别成功 · ' + this.chgTypes[this.chgType].label + '桩 ' + p.name
					this._toast('已识别' + this.chgTypes[this.chgType].label + '桩 · ' + p.name)
					this._toPile(offIdx, true)
				}, 1600)
			},
			// 手动输入桩编号
			manualPile() {
				this.manualShow = true
				this.manualNo = ''
				this.manualSearched = false
			},
			openMatchedPile(i) {
				const list = chargeStore.curPiles()
				const realIdx = chargeStore.curPiles().indexOf(this.matchedPiles[i])
				this.manualShow = false
				this._toPile(realIdx >= 0 ? realIdx : 0, true)
			},
			_toPile(pileIdx, fromScan) {
				const piles = chargeStore.curPiles()
				const p = piles[pileIdx] || {}
				const url = this.chgType === 'car'
					? '/packageCharge/pages/charge/machineToCarCharge?machineId=' + (p.machineId || '') + '&communityId=' +
						chargeStore.curCommId()
					: '/packageCharge/pages/charge/machineToCharge?machineId=' + (p.machineId || '') + '&communityId=' +
						chargeStore.curCommId()
				uni.navigateTo({
					url: url
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

	.scan-tip {
		position: absolute;
		left: 0;
		right: 0;
		top: 62%;
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 0 40rpx;
	}

	.scan-tip-main {
		color: #fff;
		font-size: 26rpx;
		font-weight: 500;
	}

	.scan-tip-sub {
		color: rgba(255, 255, 255, .4);
		font-size: 20rpx;
		margin-top: 8rpx;
	}

	.scan-recognizing {
		display: flex;
		align-items: center;
		gap: 10rpx;
		margin-top: 20rpx;
		color: rgba(255, 255, 255, .6);
		font-size: 20rpx;
	}

	.scan-rec-icon {
		width: 24rpx;
		height: 24rpx;
	}

	.scan-test {
		position: absolute;
		left: 0;
		right: 0;
		bottom: 300rpx;
		padding: 0 40rpx;
	}

	.scan-test-label {
		color: rgba(255, 255, 255, .5);
		font-size: 20rpx;
		display: block;
		text-align: center;
		margin-bottom: 20rpx;
	}

	.scan-test-row {
		display: flex;
		gap: 20rpx;
	}

	.scan-test-btn {
		flex: 1;
		border-radius: 24rpx;
		background: rgba(255, 255, 255, .1);
		border: 1rpx solid rgba(52, 211, 153, .4);
		color: #6ee7b7;
		font-size: 24rpx;
		font-weight: 600;
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
	}

	.scan-test-ic {
		width: 30rpx;
		height: 30rpx;
	}

	.scan-test-btn.bike {
		border-color: rgba(96, 165, 250, .4);
		color: #93c5fd;
	}

	.scan-manual {
		position: absolute;
		left: 0;
		right: 0;
		bottom: 80rpx;
		padding: 0 40rpx;
	}

	.scan-type-text {
		color: rgba(255, 255, 255, .5);
		font-size: 20rpx;
		display: block;
		text-align: center;
		margin-bottom: 16rpx;
	}

	.scan-type-val {
		color: #34d399;
		font-weight: 600;
	}

	.scan-manual-btn {
		border-radius: 24rpx;
		background: rgba(255, 255, 255, .1);
		border: 1rpx solid rgba(255, 255, 255, .15);
		color: #fff;
		font-size: 24rpx;
		font-weight: 500;
		height: 92rpx;
		line-height: 92rpx;
		text-align: center;
	}

	.scan-offline-btn {
		border-radius: 24rpx;
		background: rgba(239, 68, 68, .1);
		border: 1rpx solid rgba(248, 113, 113, .4);
		color: #fca5a5;
		font-size: 24rpx;
		font-weight: 500;
		height: 92rpx;
		line-height: 92rpx;
		text-align: center;
		margin-top: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 12rpx;
	}

	.manual-pile-list {
		margin-top: 24rpx;
		max-height: 400rpx;
		overflow-y: auto;
	}

	.manual-pile-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.manual-pile-empty {
		margin-top: 24rpx;
		text-align: center;
		padding: 20rpx 0;
	}
</style>
