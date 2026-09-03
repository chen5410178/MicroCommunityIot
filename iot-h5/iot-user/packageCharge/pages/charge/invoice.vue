<template>
	<view class="iv-page">
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
			<text class="iv-select-all" v-if="unbilled.length" @click="selectAllInv">{{ allSelected ? '取消全选' : '全选' }}</text>
		</view>
		<view class="iv-list iv-mx">
			<view class="c-card iv-order" v-for="(o, i) in unbilled" :key="o.orderId" @click="toggleSel(o)">
				<view class="iv-check" :class="{ on: invSel[o.orderId] }">
					<text v-if="invSel[o.orderId]">✓</text>
				</view>
				<view style="flex:1;min-width:0;">
					<text class="c-text-bold" style="font-size:24rpx;font-weight:600;color:#1e293b;">{{ o.machineName || o.machineCode || '充电桩' }} <text class="c-text-sub" style="font-size:18rpx;">{{ o.portName || '' }}</text></text>
					<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ o.startTime || '' }} · {{ o.endTime || '' }} · 已完成</text>
				</view>
				<text class="iv-fee">¥{{ Number(o.amount || 0).toFixed(2) }}</text>
			</view>
			<view class="c-card iv-empty" v-if="!unbilled.length">
				<text class="c-text-sub">{{ communityTip }}</text>
			</view>
		</view>

		<!-- 已开票记录 -->
		<view class="iv-sec-head">
			<text class="c-title">已开票订单 <text class="c-text-sub" style="font-weight:400;">{{ billed.length }}</text> 单</text>
		</view>
		<view class="iv-list iv-mx">
			<view class="c-card iv-order" v-for="(o, i) in billed" :key="o.invoiceOrderId">
				<view class="iv-billed-ic" :class="{ fail: o.state === 'FAIL' }">{{ o.state === 'FAIL' ? '!' : '✓' }}</view>
				<view style="flex:1;min-width:0;">
					<text class="c-text-bold" style="font-size:24rpx;font-weight:600;color:#1e293b;">{{ o.buyerName || '—' }} <text class="c-text-sub" style="font-size:18rpx;">{{ o.headingType || '' }}</text></text>
					<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ stateText(o.state) }}
						<template v-if="o.state === 'DONE' && o.invoiceNo"> · 发票号 {{ o.invoiceNo }}</template>
						<template v-if="o.state === 'FAIL' && o.failReason"> · {{ o.failReason }}</template>
					</text>
					<text class="iv-billed-time" v-if="o.createTime">{{ formatTime(o.createTime) }}</text>
				</view>
				<text class="iv-fee">¥{{ Number(o.amount || 0).toFixed(2) }}</text>
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
					<text class="c-text-sub">接收手机号</text>
					<input v-model="invPhone" type="number" maxlength="11" placeholder="用于接收开票结果短信" />
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
				<text class="iv-sheet-tip">开票成功后短信推送至接收手机号 · 电子发票同步至邮箱</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import {
		listMyInvoiceOrders,
		getMyInvoiceHeading,
		saveInvoice
	} from '@/api/charge/invoiceApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'

	export default {
		data() {
			return {
				communityId: '',
				communityTip: '加载中...',
				unbilled: [],
				billed: [],
				invAmount: 0,
				invSel: {},
				invType: 'personal',
				invShow: false,
				invPersonName: '',
				invPersonId: '',
				invCompanyName: '',
				invCompanyTax: '',
				invPhone: '',
				invMail: '',
				toastMsg: ''
			}
		},
		computed: {
			selCount() {
				return Object.keys(this.invSel).filter(k => this.invSel[k]).length
			},
			selSum() {
				let sum = 0
				this.unbilled.forEach(o => {
					if (this.invSel[o.orderId]) sum += Number(o.amount || 0)
				})
				return sum
			},
			allSelected() {
				return this.unbilled.length > 0 && this.unbilled.every(o => this.invSel[o.orderId])
			}
		},
		onShow() {
			this._loadData()
		},
		methods: {
			async _loadData() {
				if (!hasLogin()) {
					this.unbilled = []
					this.billed = []
					this.invAmount = 0
					this.communityTip = '请先登录'
					this._toast('请先登录')
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				const community = uni.getStorageSync('currentCommunityInfo')
				if (!community || !community.communityId) {
					this.unbilled = []
					this.billed = []
					this.invAmount = 0
					this.communityTip = '请先选择小区后再申请开票'
					this._toast('请先切换小区')
					return
				}
				this.communityId = community.communityId
				try {
					const data = await listMyInvoiceOrders({
						communityId: this.communityId,
						bizCategory: '充电费'
					})
					this.unbilled = (data && data.unInvoiceOrders) || []
					this.billed = (data && data.invoiceOrders) || []
					this.invAmount = Number(data && data.totalAmount || 0)
					this.communityTip = this.communityId ? '暂无未开票订单' : '请先选择小区后再申请开票'
					// 清理已失效勾选
					const ids = this.unbilled.map(o => o.orderId)
					Object.keys(this.invSel).forEach(k => {
						if (ids.indexOf(k) < 0 && this.invSel[k]) {
							this.$set(this.invSel, k, false)
						}
					})
				} catch (e) {
					this.unbilled = []
					this.billed = []
					this.invAmount = 0
					this.communityTip = '暂无未开票订单'
					this._toast(typeof e === 'string' ? e : '查询开票订单失败')
				}
			},
			toggleSel(o) {
				this.$set(this.invSel, o.orderId, !this.invSel[o.orderId])
			},
			selectAllInv() {
				if (this.allSelected) {
					this.unbilled.forEach(o => this.$set(this.invSel, o.orderId, false))
				} else {
					this.unbilled.forEach(o => this.$set(this.invSel, o.orderId, true))
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
				this._resetForm()
				this.invShow = true
				// 按登录手机号查商城抬头回填（无则为空）
				getMyInvoiceHeading().then(data => {
					const heading = data && data.heading
					if (!heading) return
					if (heading.headingType === '公司') {
						this.invType = 'company'
						this.invCompanyName = heading.headingName || ''
						this.invCompanyTax = heading.taxNo || ''
					} else {
						this.invType = 'personal'
						this.invPersonName = heading.headingName || ''
					}
					if (heading.email) this.invMail = heading.email
				}).catch(() => {
					// 查询抬头失败不阻塞表单（显示为空）
				})
			},
			_resetForm() {
				const userInfo = uni.getStorageSync('userInfo') || {}
				this.invPersonName = ''
				this.invPersonId = ''
				this.invCompanyName = ''
				this.invCompanyTax = ''
				this.invMail = ''
				this.invPhone = userInfo.tel || ''
				this.invType = 'personal'
			},
			submitInvoice() {
				let headingName
				let taxNo = ''
				if (this.invType === 'personal') {
					headingName = this.invPersonName || ''
					if (!headingName.trim()) {
						this._toast('请输入个人姓名')
						return
					}
				} else {
					headingName = this.invCompanyName || ''
					taxNo = this.invCompanyTax || ''
					if (!headingName.trim()) {
						this._toast('请输入企业名称')
						return
					}
					if (!taxNo.trim()) {
						this._toast('请输入税号')
						return
					}
				}
				const phone = this.invPhone || ''
				if (!/^1\d{10}$/.test(phone)) {
					this._toast('请输入正确的接收手机号')
					return
				}
				const orderIds = this.unbilled.filter(o => this.invSel[o.orderId]).map(o => o.orderId)
				if (!orderIds.length) {
					this._toast('请先勾选需要开票的订单')
					return
				}
				saveInvoice({
					communityId: this.communityId,
					bizCategory: '充电费',
					orderIds: orderIds,
					headingType: this.invType === 'personal' ? '个人' : '公司',
					headingName: headingName.trim(),
					taxNo: taxNo.trim(),
					email: this.invMail || '',
					invoiceType: '普票',
					phone: phone
				}).then(data => {
					const state = data && data.state
					if (state === 'FAIL') {
						this._toast('开票失败：' + (data.failReason || '请稍后重试'))
						this.invShow = false
						this._loadData()
						return
					}
					if (state === 'DONE') {
						this._toast('开票成功 · 电子发票将推送至 ' + this.invMail || '接收手机号')
					} else {
						this._toast('开票申请已提交 · 开票结果稍后查看')
					}
					this.invSel = {}
					this.invShow = false
					this._loadData()
				}).catch(e => {
					this._toast(typeof e === 'string' ? e : '提交开票失败，请稍后重试')
				})
			},
			stateText(state) {
				if (state === 'DONE') return '已开票'
				if (state === 'WAIT') return '待开票'
				if (state === 'FAIL') return '开票失败'
				return state || '—'
			},
			formatTime(ts) {
				if (!ts) return ''
				const d = new Date(Number(ts))
				const mm = ('0' + (d.getMonth() + 1)).slice(-2)
				const dd = ('0' + d.getDate()).slice(-2)
				const hh = ('0' + d.getHours()).slice(-2)
				const mi = ('0' + d.getMinutes()).slice(-2)
				return mm + '-' + dd + ' ' + hh + ':' + mi
			},
			_toast(msg) {
				this.toastMsg = msg
				clearTimeout(this._toastTimer)
				this._toastTimer = setTimeout(() => {
					this.toastMsg = ''
				}, 2600)
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

	.iv-billed-ic.fail {
		background: rgba(239, 68, 68, .1);
		color: #dc2626;
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
