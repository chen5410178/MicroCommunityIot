<template>
	<view class="pd-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 桩信息卡 -->
		<view class="c-card pd-mx pd-mt">
			<view class="pd-pile-head">
				<view class="pd-pile-icon">
					<image src="/static/h5/images/charge/ic-bolt.png" class="pd-pile-icon-img" mode="aspectFit"></image>
				</view>
				<view class="pd-pile-info">
					<view class="pd-pile-name-row">
						<text class="c-title pd-pile-name">{{ machineName }}</text>
						<text class="c-badge" :class="pileFree ? 'c-badge-green' : 'c-badge-orange'">{{ pileFree ? '空闲' : '使用中' }}</text>
					</view>
					<text class="c-text-sub pd-pile-loc">{{ machineCode }}</text>
				</view>
				<view class="pd-pile-side">
					<text class="c-text-sub">{{ kind }}</text>
					<view class="pd-online" :class="online === false ? 'offline' : 'online'">
						<view class="pd-dot"></view>
						<text>{{ online === false ? '离线' : '在线' }}</text>
					</view>
				</view>
			</view>

			<!-- 收费标准 -->
			<view class="pd-price-row">
				<view class="pd-price-label">
					<view class="pd-price-ic">¥</view>
					<text class="c-text">收费标准</text>
				</view>
				<text class="pd-price-text">{{ priceText }}</text>
			</view>
		</view>

		<!-- 离线提示 -->
		<view class="pd-offline-tip" v-if="online === false">
			<text style="font-size:22rpx;line-height:1.6;">该充电桩<b>已离线</b> · 暂不可充电，请更换其他充电桩或稍后再试。</text>
		</view>

		<!-- 插座状态（只读） -->
		<view class="c-card pd-mx pd-mt">
			<view class="pd-sec-head">
				<text class="c-title">插座状态</text>
				<text class="c-text-sub">{{ slotTip }}</text>
			</view>
			<view class="pd-slot-grid">
				<view v-for="(s, k) in slots" :key="k" class="c-slot-btn" :class="{ busy: s.busy }">
					<text class="c-slot-num">{{ s.n }}</text>
					<text class="c-slot-sub">{{ s.busy ? '使用中' : '空闲' }}</text>
				</view>
			</view>
			<view class="pd-slot-state">
				<text class="c-text-sub">插座状态</text>
				<text class="c-text-bold pd-slot-state-text">{{ slotState }}</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		chgTypes,
		bikePriceText
	} from '@/api/mock/chargeMockData.js'
	import { getCommunityId } from '@/api/community/communityApi.js'
	import {
		getChargeMachines,
		getChargeMachinePort
	} from '@/api/machine/machineApi.js'

	export default {
		data() {
			return {
				machineId: '',
				machineName: '',
				machineCode: '',
				kind: chgTypes.bike.kind,
				online: true,
				pileFree: true,
				slots: [],
				slotTip: '0 个空闲 · 0 个使用中',
				slotState: '仅查看状态',
				priceText: bikePriceText(chgTypes.bike),
				themeDark: chargeStore.themeDark,
				toastMsg: ''
			}
		},
		onLoad(options) {
			this.machineId = options.machineId || ''
			if (!this.machineId) {
				this._toast('充电桩不存在')
				setTimeout(() => uni.navigateBack(), 1200)
				return
			}
			this._loadPile()
		},
		methods: {
			_loadPile() {
				const communityId = getCommunityId()
				// 桩信息
				getChargeMachines({ page: 1, row: 1, machineId: this.machineId, communityId }).then(_data => {
					const m = (_data.data || [])[0]
					if (!m) {
						this._toast('充电桩不存在')
						return
					}
					this.machineName = m.machineName
					this.machineCode = m.machineCode
					this.online = m.state !== 'OFFLINE'
					// 收费标准优先取接口 fees
					const fees = m.fees || []
					if (fees.length) {
						const f = fees[0]
						this.priceText = '电费 ¥' + (Number(f.maxEnergyPrice) || 0).toFixed(2) +
							'/度 + 服务费 ¥' + (Number(f.servicePrice) || 0).toFixed(2) + '/小时'
					}
				}).catch(() => {})
				// 插座列表
				getChargeMachinePort({ page: 1, row: 100, machineId: this.machineId, communityId }).then(_data => {
					const ports = _data.data || []
					const list = ports.map(p => ({ n: p.portCode, busy: p.state === 'WORKING' }))
					const free = list.filter(x => !x.busy).length
					this.slots = list
					this.pileFree = free > 0
					this.slotTip = free + ' 个空闲 · ' + (list.length - free) + ' 个使用中'
				}).catch(() => {})
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
	.pd-page {
		padding-bottom: 100rpx;
	}

	.pd-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.pd-mt {
		margin-top: 24rpx;
	}

	/* 卡片建立 BFC，防止最后一个子元素 margin-bottom 被折叠出卡片外 */
	.pd-page .c-card {
		overflow: hidden;
	}

	.pd-pile-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 36rpx 28rpx;
	}

	.pd-pile-icon {
		width: 100rpx;
		height: 100rpx;
		border-radius: 28rpx;
		background: rgba(16, 185, 129, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.pd-pile-icon-img {
		width: 52rpx;
		height: 52rpx;
	}

	.pd-pile-info {
		flex: 1;
		min-width: 0;
	}

	.pd-pile-name-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.pd-pile-name {
		font-size: 30rpx;
	}

	.pd-pile-loc {
		margin-top: 6rpx;
		display: block;
		font-size: 22rpx;
	}

	.pd-pile-side {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: 10rpx;
	}

	.pd-online {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 18rpx;
		font-weight: 600;
	}

	.pd-online.online {
		color: #059669;
	}

	.pd-online.offline {
		color: #64748b;
	}

	.pd-dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background: #10b981;
	}

	.pd-online.offline .pd-dot {
		background: #cbd5e1;
	}

	.pd-price-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 0 28rpx 40rpx;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .08);
		border: 1rpx solid rgba(16, 185, 129, .15);
		padding: 18rpx 24rpx;
	}

	.pd-price-label {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.pd-price-ic {
		width: 32rpx;
		height: 32rpx;
		border-radius: 50%;
		background: rgba(16, 185, 129, .2);
		color: #059669;
		font-size: 18rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.pd-price-text {
		font-size: 22rpx;
		font-weight: 700;
		color: #059669;
		text-align: right;
	}

	.pd-sec-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 28rpx 20rpx;
	}

	.pd-slot-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 16rpx;
		padding: 0 28rpx;
	}

	.pd-slot-grid .c-slot-btn {
		border-radius: 28rpx;
		padding: 32rpx 0;
		gap: 10rpx;
	}

	.pd-slot-grid .c-slot-num {
		font-size: 32rpx;
	}

	.pd-slot-grid .c-slot-sub {
		font-size: 20rpx;
	}

	.pd-slot-grid .c-slot-btn:not(.busy) .c-slot-sub {
		color: #10b981;
	}

	.pd-slot-state {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 22rpx 28rpx 28rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 18rpx 24rpx;
	}

	.pd-slot-state-text {
		font-size: 23rpx;
		font-weight: 600;
	}

	.pd-offline-tip {
		margin: 24rpx 28rpx 0;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		border: 1rpx solid rgba(239, 68, 68, .15);
		padding: 20rpx 24rpx;
		color: #ef4444;
	}

	/* ===== 黑夜主题覆盖（对齐原型 phone.theme-dark） ===== */
	.charge-theme-dark .pd-slot-state {
		background: #182338 !important;
	}

	.charge-theme-dark .pd-price-row {
		background: rgba(16, 185, 129, .14) !important;
		border-color: rgba(16, 185, 129, .28) !important;
	}

	.charge-theme-dark .pd-offline-tip {
		background: rgba(239, 68, 68, .12) !important;
	}

	.charge-theme-dark .pd-pile-icon {
		background: rgba(16, 185, 129, .18) !important;
	}
</style>
