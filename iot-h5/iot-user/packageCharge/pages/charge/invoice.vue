<template>
	<view class="iv-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 可开票金额卡 -->
		<view class="iv-hero">
			<view class="iv-hero-head">
				<text class="iv-title">电子发票</text>
				<text class="iv-tag">按订单勾选</text>
			</view>
			<text class="iv-label">累计可开票金额（未开票订单）</text>
			<text class="iv-amount">¥{{ invAmount.toFixed(2) }}</text>
			<text class="iv-note">勾选未开票充电订单申请开票 · 免费订单不参与</text>
		</view>

		<!-- 未开票订单 -->
		<view class="iv-sec-head">
			<text class="c-title">未开票订单 <text style="color:#059669;">{{ unbilled.length }}</text> 单</text>
			<text class="iv-select-all" @click="selectAllInv">{{ allSelected ? '取消全选' : '全选' }}</text>
		</view>
		<view class="iv-list iv-mx">
			<view class="c-card iv-order" v-for="(o, i) in unbilled" :key="i" @click="toggleSel(o)">
				<view class="iv-check" :class="{ on: invSel[o.orderIdx] }">
					<text v-if="invSel[o.orderIdx]">✓</text>
				</view>
				<view style="flex:1;min-width:0;">
					<text class="c-text-bold" style="font-size:24rpx;font-weight:600;color:#1e293b;">{{ o.r.pile }} <text class="c-text-sub" style="font-size:18rpx;">{{ o.r.date }}</text></text>
					<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ o.r.way }} · {{ o.r.mins }} 分钟 · {{ o.r.st }}</text>
				</view>
				<text class="iv-fee">¥{{ o.r.fee.toFixed(2) }}</text>
			</view>
			<view class="c-card iv-empty" v-if="!unbilled.length">
				<text class="c-text-sub">暂无未开票订单</text>
			</view>
		</view>

		<!-- 已开票订单 -->
		<view class="iv-sec-head">
			<text class="c-title">已开票订单 <text class="c-text-sub" style="font-weight:400;">{{ billed.length }}</text> 单</text>
		</view>
		<view class="iv-list iv-mx">
			<view class="c-card iv-order" v-for="(o, i) in billed" :key="i">
				<view class="iv-billed-ic">✓</view>
				<view style="flex:1;min-width:0;">
					<text class="c-text-bold" style="font-size:24rpx;font-weight:600;color:#1e293b;">{{ o.r.pile }} <text class="c-text-sub" style="font-size:18rpx;">{{ o.r.date }}</text></text>
					<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ o.r.invTitle || '个人 · 李明' }} · 发票号 {{ o.r.invNo || '—' }}</text>
					<text class="iv-billed-time">{{ o.r.invTime || '' }} 已开具 · 已推送微信卡包</text>
				</view>
				<text class="iv-fee">¥{{ o.r.fee.toFixed(2) }}</text>
			</view>
			<view class="c-card iv-empty" v-if="!billed.length">
				<text class="c-text-sub">暂无已开票订单</text>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="iv-bar" v-if="selCount > 0">
			<view class="iv-bar-box">
				<text class="iv-bar-txt">已选 <text style="color:#10b981;font-weight:700;">{{ selCount }}</text> 单 · 合计 <text style="color:#10b981;font-weight:700;">¥{{ selSum.toFixed(2) }}</text></text>
				<view class="iv-bar-btn" @click="openInvoice">申请开票</view>
			</view>
		</view>

		<!-- 开票申请弹层 -->
		<view class="c-mask" v-if="invShow" @click="invShow = false"></view>
		<view class="c-sheet" v-if="invShow">
			<view class="iv-sheet-head">
				<text class="c-title" style="font-size:28rpx;">申请开票</text>
				<text class="iv-sheet-close" @click="invShow = false">关闭</text>
			</view>
			<view class="iv-sheet-body">
				<view class="iv-type-tabs">
					<view class="iv-type-tab" :class="{ active: invType === 'personal' }" @click="invType = 'personal'">个人抬头</view>
					<view class="iv-type-tab" :class="{ active: invType === 'company' }" @click="invType = 'company'">企业抬头</view>
				</view>

				<view v-if="invType === 'personal'">
					<view class="c-form-input iv-form-row">
						<text class="c-text-sub">姓名</text>
						<input v-model="invPersonName" placeholder="请输入开票人姓名" />
					</view>
					<view class="c-form-input iv-form-row">
						<text class="c-text-sub">身份证号</text>
						<input v-model="invPersonId" placeholder="选填 · 用于税务核验" />
					</view>
				</view>
				<view v-else>
					<view class="c-form-input iv-form-row">
						<text class="c-text-sub">企业名称</text>
						<input v-model="invCompanyName" placeholder="与营业执照一致" />
					</view>
					<view class="c-form-input iv-form-row">
						<text class="c-text-sub">税号</text>
						<input v-model="invCompanyTax" placeholder="纳税人识别号" />
					</view>
				</view>
				<view class="c-form-input iv-form-row">
					<text class="c-text-sub">邮箱</text>
					<input v-model="invMail" placeholder="接收电子发票" />
				</view>

				<view class="iv-sum-row">
					<text style="font-size:22rpx;color:#b45309;">开票金额（勾选订单）</text>
					<text style="font-size:28rpx;font-weight:700;color:#d97706;">¥{{ selSum.toFixed(2) }}</text>
				</view>
				<view class="c-btn-primary" @click="submitInvoice">提交开票申请</view>
				<text class="iv-sheet-tip">电子发票开具后自动推送至微信卡包与邮箱</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'

	export default {
		data() {
			return {
				invSel: {},
				invType: 'personal',
				invShow: false,
				invPersonName: '',
				invPersonId: '',
				invCompanyName: '',
				invCompanyTax: '',
				invMail: '',
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			unbilled() {
				return chargeStore.records.map((r, i) => ({ r, i })).filter(o => !o.r.inv && o.r.fee > 0).map(o => ({
					r: o.r,
					orderIdx: o.i
				}))
			},
			billed() {
				return chargeStore.records.map((r, i) => ({ r, i })).filter(o => o.r.inv).map(o => ({
					r: o.r,
					orderIdx: o.i
				}))
			},
			invAmount() {
				return this.unbilled.reduce((s, o) => s + o.r.fee, 0)
			},
			selCount() {
				return Object.keys(this.invSel).filter(k => this.invSel[k]).length
			},
			selSum() {
				let sum = 0
				this.unbilled.forEach(o => {
					if (this.invSel[o.orderIdx]) sum += o.r.fee
				})
				return sum
			},
			allSelected() {
				return this.unbilled.length > 0 && this.unbilled.every(o => this.invSel[o.orderIdx])
			}
		},
		methods: {
			toggleSel(o) {
				if (this.invSel[o.orderIdx]) {
					this.$set(this.invSel, o.orderIdx, false)
				} else {
					this.$set(this.invSel, o.orderIdx, true)
				}
			},
			selectAllInv() {
				if (this.allSelected) {
					this.unbilled.forEach(o => this.$set(this.invSel, o.orderIdx, false))
				} else {
					this.unbilled.forEach(o => this.$set(this.invSel, o.orderIdx, true))
				}
			},
			openInvoice() {
				if (!hasLogin()) {
					this._toast('请先登录')
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				if (this.selCount === 0) {
					this._toast('请先勾选需要开票的订单')
					return
				}
				this.invMail = ''
				this.invPersonName = '李明'
				this.invPersonId = ''
				this.invCompanyName = ''
				this.invCompanyTax = ''
				this.invShow = true
			},
			submitInvoice() {
				let title
				if (this.invType === 'personal') {
					if (!this.invPersonName.trim()) {
						this._toast('请输入个人姓名')
						return
					}
					title = '个人 · ' + this.invPersonName.trim() + (this.invPersonId.trim() ? '（' + this.invPersonId.trim() + '）' : '')
				} else {
					if (!this.invCompanyName.trim()) {
						this._toast('请输入企业名称')
						return
					}
					if (!this.invCompanyTax.trim()) {
						this._toast('请输入税号')
						return
					}
					title = '企业 · ' + this.invCompanyName.trim() + '（' + this.invCompanyTax.trim() + '）'
				}
				if (!this.invMail.trim()) {
					this._toast('请输入接收邮箱')
					return
				}
				const now = new Date()
				const mm = ('0' + (now.getMonth() + 1)).slice(-2)
				const dd = ('0' + now.getDate()).slice(-2)
				const hh = ('0' + now.getHours()).slice(-2)
				const mi = ('0' + now.getMinutes()).slice(-2)
				const no = 'INV' + now.getFullYear() + mm + dd + String(Math.floor(Math.random() * 9000) + 1000)
				this.unbilled.forEach(o => {
					if (this.invSel[o.orderIdx]) {
						chargeStore.records[o.orderIdx].inv = 1
						chargeStore.records[o.orderIdx].invNo = no
						chargeStore.records[o.orderIdx].invTime = mm + '-' + dd + ' ' + hh + ':' + mi
						chargeStore.records[o.orderIdx].invTitle = title
					}
				})
				chargeStore.saveRecords()
				this.invSel = {}
				this.invShow = false
				this._toast('开票申请已提交 · 电子发票将推送至 ' + this.invMail.trim())
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
	.iv-page {
		padding-bottom: 160rpx;
	}

	.iv-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.iv-hero {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #fbbf24, #f97316);
		padding: 36rpx;
		box-shadow: 0 12rpx 30rpx rgba(249, 115, 22, .2);
	}

	.iv-hero-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.iv-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.iv-tag {
		color: #fff;
		font-size: 18rpx;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 6rpx 18rpx;
	}

	.iv-label {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 24rpx;
	}

	.iv-amount {
		display: block;
		color: #fff;
		font-size: 56rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.iv-note {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 18rpx;
		margin-top: 14rpx;
	}

	.iv-sec-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 32rpx 28rpx 16rpx;
	}

	.iv-select-all {
		font-size: 20rpx;
		color: #059669;
		font-weight: 600;
	}

	.iv-list {
		background: #fff;
		border-radius: 28rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, .05);
	}

	.iv-order {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 24rpx;
		border-radius: 0;
		box-shadow: none;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.iv-order:last-child {
		border-bottom: none;
	}

	.iv-check {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		border: 2rpx solid #cbd5e1;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 22rpx;
		color: #fff;
		flex-shrink: 0;
	}

	.iv-check.on {
		background: #10b981;
		border-color: #10b981;
	}

	.iv-fee {
		font-size: 24rpx;
		font-weight: 700;
		color: #0f172a;
		flex-shrink: 0;
	}

	.iv-billed-ic {
		width: 64rpx;
		height: 64rpx;
		border-radius: 20rpx;
		background: rgba(16, 185, 129, .1);
		color: #059669;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
		font-weight: 700;
		flex-shrink: 0;
	}

	.iv-billed-time {
		display: block;
		font-size: 18rpx;
		color: #059669;
		margin-top: 4rpx;
	}

	.iv-empty {
		padding: 60rpx 0;
		text-align: center;
		border-radius: 0;
		box-shadow: none;
	}

	.iv-bar {
		position: fixed;
		left: 28rpx;
		right: 28rpx;
		bottom: 40rpx;
		z-index: 200;
	}

	.iv-bar-box {
		background: rgba(15, 23, 42, .95);
		border-radius: 28rpx;
		padding: 22rpx 26rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		box-shadow: 0 16rpx 40rpx rgba(15, 23, 42, .3);
	}

	.iv-bar-txt {
		font-size: 22rpx;
		color: #fff;
		line-height: 1.4;
	}

	.iv-bar-btn {
		background: #10b981;
		color: #fff;
		font-size: 24rpx;
		font-weight: 700;
		border-radius: 20rpx;
		padding: 18rpx 36rpx;
		margin-left: 20rpx;
		flex-shrink: 0;
	}

	.iv-sheet-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 36rpx 16rpx;
	}

	.iv-sheet-close {
		font-size: 20rpx;
		color: #94a3b8;
		padding: 8rpx;
	}

	.iv-sheet-body {
		padding: 0 36rpx 60rpx;
	}

	.iv-type-tabs {
		display: flex;
		gap: 8rpx;
		background: #f1f5f9;
		border-radius: 20rpx;
		padding: 8rpx;
	}

	.iv-type-tab {
		flex: 1;
		height: 68rpx;
		line-height: 68rpx;
		text-align: center;
		font-size: 24rpx;
		font-weight: 600;
		border-radius: 16rpx;
		color: #64748b;
	}

	.iv-type-tab.active {
		background: #fff;
		color: #059669;
		box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, .08);
	}

	.iv-form-row {
		margin-top: 20rpx;
	}

	.iv-sum-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(249, 115, 22, .08);
		border: 1rpx solid rgba(249, 115, 22, .15);
		padding: 20rpx 24rpx;
	}

	.iv-sheet-tip {
		display: block;
		text-align: center;
		font-size: 20rpx;
		color: #94a3b8;
		margin-top: 20rpx;
	}
</style>
