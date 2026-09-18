<template>
	<view>
		<!-- 其他小区余额行（其他小区有余额才展示） -->
		<view class="ob-row" :class="{ alert: alert }" v-if="show" @click="open">
			<view class="ob-left">
				<view class="ob-dot" v-if="alert"></view>
				<view class="ob-ic"></view>
				<text class="ob-label">其他小区余额</text>
				<text class="ob-money">¥{{ sum.toFixed(2) }}</text>
			</view>
			<view class="ob-right">
				<text>点击切换</text>
				<text class="ob-chevron">›</text>
			</view>
		</view>

		<!-- 切换小区弹层（只列出有余额的小区） -->
		<view class="c-mask" v-if="sheetShow" @click="close"></view>
		<view class="c-sheet" v-if="sheetShow">
			<view class="ob-sheet-head">
				<text class="c-title" style="font-size:28rpx;">切换小区</text>
				<text class="ob-sheet-close" @click="close">关闭</text>
			</view>
			<view class="ob-sheet-body">
				<text class="ob-sheet-tip">{{ tip }}</text>
				<view class="ob-sheet-list">
					<view class="ob-sheet-item" :class="{ active: item.communityId === curId() }"
						v-for="(item, i) in list" :key="item.communityId || i" @click="pick(item)">
						<view style="min-width:0;">
							<view class="ob-sheet-name-row">
								<text class="ob-sheet-name">{{ item.communityName || '未知小区' }}</text>
								<text class="ob-sheet-badge cur" v-if="item.communityId === curId()">当前</text>
								<text class="ob-sheet-badge warn" v-else>有余额</text>
							</view>
							<text class="ob-sheet-sub">账户余额</text>
						</view>
						<text class="ob-sheet-money">¥{{ item.amount.toFixed(2) }}</text>
					</view>
				</view>
				<text class="ob-sheet-empty" v-if="!list.length">暂无其他小区余额</text>
				<view class="ob-sheet-all" @click="goAll">查看全部小区 ›</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		queryAppUserAccounts
	} from '@/api/user/userApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import {
		getCommunityId
	} from '@/api/community/communityApi.js'

	export default {
		name: 'other-balance',
		props: {
			// 当前小区ID（首页传入，未就绪时用本地缓存兜底）
			communityId: {
				type: String,
				default: ''
			},
			// 当前小区余额
			balance: {
				type: Number,
				default: 0
			}
		},
		data() {
			return {
				sheetShow: false,
				// 有余额的小区（含当前小区），金额倒序
				list: [],
				// 其他小区余额合计（不含当前小区）
				sum: 0,
				// 其他小区有余额才展示该行
				show: false,
				// 当前小区余额为 0 且其他小区有余额：提醒态
				alert: false,
				tip: ''
			}
		},
		methods: {
			// 首页 onShow / 切换小区后调用，重新查询并一次性算好展示数据
			load() {
				if (!hasLogin()) {
					this._fill([])
					return
				}
				queryAppUserAccounts({}).then(list => {
					this._fill(list || [])
				}).catch(() => {
					this._fill([])
				})
			},
			// list: account.queryAppUserAccounts 返回的账户数组
			_fill(accounts) {
				const cur = this.curId()
				const list = []
				let sum = 0
				;(accounts || []).forEach(a => {
					const communityId = a.communityId || a.partId || ''
					const amount = parseFloat(a.amount) || 0
					if (!communityId || amount <= 0) return
					if (communityId !== cur) sum += amount
					list.push({
						communityId: communityId,
						communityName: a.communityName || '',
						amount: amount
					})
				})
				list.sort((a, b) => b.amount - a.amount)
				this.list = list
				this.sum = sum
				this.show = sum > 0
				this.alert = this.balance <= 0 && sum > 0
				this.tip = this.alert
					? '当前小区余额为 0.00，其他小区还有余额 ¥' + sum.toFixed(2) + '，可能选错了小区，点下方切换。'
					: '你在多个小区有账户，余额分别独立。切换小区可查看对应余额与充电桩。'
			},
			curId() {
				return this.communityId || getCommunityId()
			},
			open() {
				this.sheetShow = true
			},
			close() {
				this.sheetShow = false
			},
			// 选中小区：当前小区仅关闭弹层，其他小区交由首页完成切换并重新查询
			pick(item) {
				if (!item || !item.communityId) return
				this.close()
				if (item.communityId === this.curId()) return
				this.$emit('switch', item)
			},
			// 弹层底部入口：查看全部小区（可切换到无余额小区）
			goAll() {
				this.close()
				uni.navigateTo({
					url: '/packageProperty/pages/property/changeCommunity'
				})
			}
		}
	}
</script>

<style>
	/* 其他小区余额行 */
	.ob-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 16rpx;
		height: 72rpx;
		padding: 0 22rpx;
		border-radius: 22rpx;
		background: rgba(255, 255, 255, .16);
	}

	.ob-row.alert {
		background: rgba(255, 214, 102, .26);
		box-shadow: inset 0 0 0 1rpx rgba(255, 225, 150, .55);
	}

	.ob-left {
		display: flex;
		align-items: center;
		gap: 12rpx;
		min-width: 0;
	}

	.ob-dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background: #ffe07a;
		flex-shrink: 0;
		animation: obPulse 1.6s infinite;
	}

	@keyframes obPulse {
		0% {
			box-shadow: 0 0 0 0 rgba(255, 224, 122, .55);
		}
		70% {
			box-shadow: 0 0 0 14rpx rgba(255, 224, 122, 0);
		}
		100% {
			box-shadow: 0 0 0 0 rgba(255, 224, 122, 0);
		}
	}

	.ob-ic {
		width: 26rpx;
		height: 20rpx;
		border: 2rpx solid rgba(255, 255, 255, .92);
		border-radius: 6rpx;
		position: relative;
		flex-shrink: 0;
	}

	.ob-ic::after {
		content: '';
		position: absolute;
		left: 3rpx;
		right: 3rpx;
		top: 5rpx;
		height: 2rpx;
		background: rgba(255, 255, 255, .92);
	}

	.ob-label {
		color: #fff;
		font-size: 24rpx;
	}

	.ob-money {
		color: #ffe9a0;
		font-size: 26rpx;
		font-weight: 700;
	}

	.ob-row.alert .ob-money {
		color: #fff6cf;
	}

	.ob-right {
		display: flex;
		align-items: center;
		gap: 6rpx;
		font-size: 22rpx;
		color: rgba(255, 255, 255, .92);
		flex-shrink: 0;
	}

	.ob-row.alert .ob-right {
		color: #fff;
		font-weight: 600;
	}

	.ob-chevron {
		font-size: 26rpx;
	}

	/* 切换小区弹层 */
	.ob-sheet-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 36rpx 16rpx;
	}

	.ob-sheet-close {
		font-size: 20rpx;
		color: #94a3b8;
		padding: 8rpx;
	}

	.ob-sheet-body {
		padding: 0 36rpx 60rpx;
		max-height: 62%;
		overflow-y: auto;
	}

	.ob-sheet-tip {
		display: block;
		font-size: 22rpx;
		color: #94a3b8;
		line-height: 1.7;
	}

	.ob-sheet-list {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
		margin-top: 20rpx;
	}

	.ob-sheet-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16rpx;
		border: 1rpx solid #eef1f5;
		border-radius: 24rpx;
		padding: 22rpx 24rpx;
	}

	.ob-sheet-item.active {
		border-color: #10b981;
		background: #f2fbf6;
	}

	.ob-sheet-name-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.ob-sheet-name {
		font-size: 28rpx;
		font-weight: 600;
		color: #0f172a;
	}

	.ob-sheet-badge {
		font-size: 18rpx;
		font-weight: 600;
		border-radius: 8rpx;
		padding: 2rpx 10rpx;
	}

	.ob-sheet-badge.cur {
		color: #059669;
		background: rgba(16, 185, 129, .12);
	}

	.ob-sheet-badge.warn {
		color: #d97706;
		background: rgba(249, 115, 22, .12);
	}

	.ob-sheet-sub {
		display: block;
		font-size: 20rpx;
		color: #94a3b8;
		margin-top: 8rpx;
	}

	.ob-sheet-money {
		font-size: 30rpx;
		font-weight: 700;
		color: #059669;
		flex-shrink: 0;
	}

	.ob-sheet-empty {
		display: block;
		text-align: center;
		font-size: 24rpx;
		color: #94a3b8;
		padding: 40rpx 0;
	}

	.ob-sheet-all {
		margin-top: 24rpx;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
		border-radius: 24rpx;
		border: 1rpx solid #e2e8f0;
		color: #64748b;
		font-size: 26rpx;
		font-weight: 600;
	}

	/* 黑夜主题 */
	.charge-theme-dark .ob-sheet-item {
		background: #1e293b !important;
		border-color: #334155 !important;
	}

	.charge-theme-dark .ob-sheet-item.active {
		background: rgba(16, 185, 129, .12) !important;
		border-color: #10b981 !important;
	}

	.charge-theme-dark .ob-sheet-name {
		color: #e2e8f0 !important;
	}

	.charge-theme-dark .ob-sheet-all {
		background: #1e293b !important;
		border-color: #334155 !important;
		color: #e2e8f0 !important;
	}
</style>
