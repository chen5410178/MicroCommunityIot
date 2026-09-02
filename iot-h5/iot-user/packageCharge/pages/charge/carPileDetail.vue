<template>
	<view class="pd-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 桩信息卡 -->
		<view class="c-card pd-mx pd-mt">
			<view class="pd-pile-head">
				<view class="pd-pile-icon">
					<image :src="imgUrl + '/h5/images/charge/ic-bolt.png'" class="pd-pile-icon-img" mode="aspectFit"></image>
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

			<!-- 充电枪状态（只读） -->
			<view class="pd-gun-box" v-if="guns.length > 0">
				<view class="pd-sec-head">
					<text class="c-title">充电枪状态</text>
					<text class="c-text-sub">{{ gunTip }}</text>
				</view>
				<view class="pd-gun-list">
					<view v-for="(g, k) in guns" :key="k" class="c-gun-btn" :class="{ disabled: g.busy }">
						<text style="font-size:32rpx;font-weight:700;">{{ g.n }} 枪</text>
						<text style="font-size:20rpx;" :style="{ color: g.busy ? '#f87171' : '#10b981' }">{{ g.busy ? '使用中' : '空闲' }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 离线提示 -->
		<view class="pd-offline-tip" v-if="online === false">
			<text style="font-size:22rpx;line-height:1.6;">该充电桩<b>已离线</b> · 暂不可充电，请更换其他充电桩或稍后再试。</text>
		</view>

		<!-- 分时电价（只读） -->
		<view class="c-card pd-mx pd-mt">
			<view class="pd-sec-head">
				<view class="pd-tariff-title-row">
					<text class="c-title">分时电价</text>
					<text class="pd-tariff-now" :style="{ background: curTariff.color }">当前{{ curTariff.name }}</text>
				</view>
				<view class="pd-tariff-more-btn" @click="tariffMore = !tariffMore">
					<text class="c-text-sub">{{ tariffMore ? '收起' : '查看全部时段' }}</text>
					<text class="pd-arrow" :class="{ open: tariffMore }">▾</text>
				</view>
			</view>
			<view class="pd-tariff-current">
				<view class="pd-tc-left">
					<view class="pd-tc-name-row">
						<view class="pd-tc-dot" :style="{ background: curTariff.color }"></view>
						<text class="c-text-bold pd-tc-name-text">{{ curTariff.name }}段</text>
						<text class="pd-tc-now-tag">当前</text>
					</view>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ curTariff.time }}</text>
				</view>
				<view class="pd-tc-rate" :style="{ color: curTariff.color }">
					<view style="display:flex;align-items:baseline;gap:4rpx;">
						<text style="font-size:48rpx;font-weight:700;line-height:1;">¥{{ curTariff.rate.toFixed(2) }}</text>
						<text class="c-text-sub">/度</text>
					</view>
					<text class="pd-tc-rate-sub">电费 ¥{{ curTariff.elec.toFixed(2) }} + 服务费 ¥{{ curTariff.serv.toFixed(2) }}</text>
				</view>
			</view>
			<view v-if="tariffMore" class="pd-tariff-more">
				<view class="pd-tariff-more-item" v-for="(t, i) in otherTariffs" :key="i">
					<view class="pd-tc-dot" :style="{ background: t.color }"></view>
					<text class="c-text-bold pd-tm-name">{{ t.name }}段</text>
					<text class="c-text-sub pd-tm-time">{{ t.time }}</text>
					<view style="text-align:right;">
						<text class="c-text-bold pd-tm-rate">¥{{ t.rate.toFixed(2) }}/度</text>
						<text class="c-text-sub pd-tm-sub" style="display:block;">电 ¥{{ t.elec.toFixed(2) }} + 服 ¥{{ t.serv.toFixed(2) }}</text>
					</view>
				</view>
			</view>
			<text class="pd-tariff-note">费率由物业 PC 后台统一设置 · 小程序端实时同步显示</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		chgTypes,
		defaultTariffs,
		tariffAt
	} from '@/api/mock/chargeMockData.js'
	import { getCommunityId } from '@/api/community/communityApi.js'
	import {
		getChargeMachines,
		getChargeMachinePort
	} from '@/api/machine/machineApi.js'

	// 尖/峰/平/谷 时段颜色
	const COLOR_MAP = {
		'尖': '#ef4444',
		'峰': '#f97316',
		'平': '#eab308',
		'谷': '#22c55e'
	}

	export default {
		data() {
			return {
				machineId: '',
				machineName: '',
				machineCode: '',
				kind: chgTypes.car.kind,
				online: true,
				pileFree: true,
				guns: [],
				gunTip: '',
				tariffMore: false,
				tariffs: JSON.parse(JSON.stringify(defaultTariffs)),
				curTariff: {},
				otherTariffs: [],
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
						setTimeout(() => uni.navigateBack(), 1200)
						return
					}
					this.machineName = m.machineName
					this.machineCode = m.machineCode
					this.online = m.state !== 'OFFLINE'
					// 分时电价优先取接口 prices，无数据时保持 defaultTariffs 兜底
					const prices = m.prices || []
					if (prices.length) {
						this.tariffs = prices.map(p => ({
							name: p.cpaTypeName || '平段',
							time: (p.startTime || '').slice(0, 5) + '-' + (p.endTime || '').slice(0, 5),
							elec: Number(p.price) || 0,
							serv: Number(p.servicePrice) || 0,
							rate: (Number(p.price) || 0) + (Number(p.servicePrice) || 0),
							color: COLOR_MAP[p.cpaTypeName] || '#eab308',
							startTime: p.startTime || '',
							endTime: p.endTime || ''
						}))
					}
					this._calcTariffs()
				}).catch(() => {})
				// 充电枪列表
				getChargeMachinePort({ page: 1, row: 100, machineId: this.machineId, communityId }).then(_data => {
					const ports = _data.data || []
					const list = ports.map(p => ({ n: p.portCode, busy: p.state === 'WORKING' }))
					const free = list.filter(x => !x.busy).length
					this.guns = list
					this.pileFree = free > 0
					this.gunTip = free + ' 枪空闲 · ' + (list.length - free) + ' 枪使用中'
				}).catch(() => {})
			},
			// 计算当前时段与展开列表（onLoad 一次性算好，详情页短时只读不刷新）
			_calcTariffs() {
				const tariffs = this.tariffs || []
				const now = new Date()
				const cur = now.getHours() * 60 + now.getMinutes()
				let hit = null
				for (let i = 0; i < tariffs.length; i++) {
					if (this._inRange(tariffs[i], cur)) {
						hit = tariffs[i]
						break
					}
				}
				this.curTariff = hit || tariffAt(now.getHours(), tariffs) || tariffs[0] || {}
				this.otherTariffs = tariffs.filter(t => t !== this.curTariff)
			},
			// 当前分钟数是否落在 startTime ~ endTime 区间（支持跨天，如 23:00-07:00）
			_inRange(t, cur) {
				const start = t.startTime || ''
				const end = t.endTime || ''
				if (!start || !end || start.indexOf(':') < 0 || end.indexOf(':') < 0) return false
				const s = parseInt(start.slice(0, 2)) * 60 + parseInt(start.slice(3, 5))
				const e = parseInt(end.slice(0, 2)) * 60 + parseInt(end.slice(3, 5))
				if (s <= e) return cur >= s && cur < e
				return cur >= s || cur < e
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
		padding-bottom: 60rpx;
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

	.pd-gun-box {
		margin: 0 28rpx 28rpx;
	}

	.pd-sec-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 28rpx 20rpx;
	}

	.pd-gun-list {
		display: flex;
		gap: 20rpx;
		padding: 0 28rpx 20rpx;
	}

	.pd-gun-list .c-gun-btn {
		border-radius: 32rpx;
		padding: 24rpx 0;
		gap: 6rpx;
	}

	.pd-offline-tip {
		margin: 24rpx 28rpx 0;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		border: 1rpx solid rgba(239, 68, 68, .15);
		padding: 20rpx 24rpx;
		color: #ef4444;
	}

	.pd-tariff-title-row {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.pd-tariff-now {
		font-size: 18rpx;
		font-weight: 700;
		color: #fff;
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.pd-tariff-more-btn {
		display: flex;
		align-items: center;
		gap: 6rpx;
	}

	.pd-arrow {
		font-size: 20rpx;
		color: #10b981;
		transition: transform .2s;
	}

	.pd-arrow.open {
		transform: rotate(180deg);
	}

	.pd-tariff-current {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 0 28rpx;
		border-radius: 20rpx;
		background: #f1f5f9;
		padding: 24rpx;
	}

	.pd-tc-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.pd-tc-name-text {
		font-size: 26rpx;
		font-weight: 700;
	}

	.pd-tc-dot {
		width: 18rpx;
		height: 18rpx;
		border-radius: 50%;
		flex-shrink: 0;
	}

	.pd-tc-now-tag {
		font-size: 16rpx;
		color: #fff;
		background: #10b981;
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.pd-tc-rate {
		text-align: right;
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: 8rpx;
	}

	.pd-tc-rate-sub {
		font-size: 20rpx;
	}

	.pd-tariff-more {
		margin: 20rpx 28rpx 0;
		display: flex;
		flex-direction: column;
		gap: 14rpx;
	}

	.pd-tariff-more-item {
		display: flex;
		align-items: center;
		gap: 16rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 16rpx 20rpx;
	}

	.pd-tm-name {
		font-size: 23rpx;
		font-weight: 700;
		width: 80rpx;
	}

	.pd-tm-time {
		flex: 1;
		font-size: 20rpx;
	}

	.pd-tm-rate {
		font-size: 24rpx;
		font-weight: 700;
	}

	.pd-tm-sub {
		font-size: 18rpx;
	}

	.pd-tariff-note {
		display: block;
		font-size: 16rpx;
		color: #cbd5e1;
		margin: 20rpx 28rpx 28rpx;
	}

	/* ===== 黑夜主题覆盖（对齐原型 phone.theme-dark） ===== */
	.charge-theme-dark .pd-tariff-current {
		background: #182338 !important;
	}

	.charge-theme-dark .pd-tariff-more-item {
		background: #182338 !important;
	}

	.charge-theme-dark .pd-offline-tip {
		background: rgba(239, 68, 68, .12) !important;
	}

	.charge-theme-dark .pd-pile-icon {
		background: rgba(16, 185, 129, .18) !important;
	}

	.charge-theme-dark .pd-tariff-note {
		color: #64748b !important;
	}
</style>
