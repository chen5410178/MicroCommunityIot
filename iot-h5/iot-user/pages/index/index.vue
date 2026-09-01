<template>
	<view class="home-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 顶部导航（模拟胶囊 + 主题/消息） -->
		<view class="home-nav">
			<text class="home-nav-title">充电服务</text>
			<view class="home-nav-right">
				<view class="home-nav-ic" @click="openMsg" style="position:relative;">
					<image src="/static/h5/images/common/ic-bell.png" class="home-nav-ic-img" mode="aspectFit"></image>
					<view class="home-msg-badge" v-if="unread > 0">{{ unread > 99 ? '99+' : unread }}</view>
				</view>
				<view class="home-capsule">
					<view class="home-capsule-dots">
						<text class="home-dot"></text><text class="home-dot"></text><text class="home-dot"></text>
					</view>
					<view class="home-capsule-divider"></view>
					<view class="home-capsule-theme" @click="themeMenuShow = !themeMenuShow">
						<image v-if="themeDark" src="/static/h5/images/common/ic-sun.png" class="home-capsule-theme-img" mode="aspectFit"></image>
						<image v-else src="/static/h5/images/common/ic-moon-dark.png" class="home-capsule-theme-img" mode="aspectFit"></image>
					</view>
				</view>
			</view>
		</view>

		<!-- 在充订单提示条 -->
		<view class="home-running-badge" v-if="runningOrder" @click="openRunningOrder">
			<view class="home-rb-head">
				<view class="home-rb-title-row">
					<view class="c-pulse-dot" style="width:14rpx;height:14rpx;border-radius:50%;background:#fff;"></view>
					<text class="home-rb-title">订单充电中</text>
				</view>
				<text class="home-rb-slot">{{ rbSlotText }}</text>
			</view>
			<view class="home-rb-foot">
				<text class="home-rb-text">已充 {{ rbTime }} · 费用 ¥{{ rbFee }} · 点击查看</text>
				<text class="home-rb-link">在充订单 ›</text>
			</view>
		</view>

		<!-- 小区切换 + 余额卡 -->
		<view class="home-balance-card">
			<image src="/static/h5/images/charge/ic-bolt-white.png" class="home-bc-decor1" mode="aspectFit"></image>
			<view class="home-bc-decor2"></view>
			<view class="home-bc-head">
				<view class="home-comm-btn" @click="goChangeCommunity">
					<image src="/static/h5/images/common/ic-pin-white.png" class="home-bc-ic" mode="aspectFit"></image>
					<text class="home-comm-name">{{ curCommunity.name || '选择小区' }}</text>
					<text class="home-comm-dist" v-if="curCommunity.dist">{{ fmtDist(curCommunity.dist) }}</text>
					<image src="/static/h5/images/common/ic-chevron-down-white.png" class="home-bc-arrow" mode="aspectFit"></image>
				</view>
				<view class="home-car-btn" @click="editHomeCar">
					<image src="/static/h5/images/charge/ic-car-white.png" class="home-bc-ic" mode="aspectFit"></image>
					<text class="home-car-label">我的车辆</text>
					<text class="home-car-plate">{{ vehicles.length ? vehicles[0].plate : '去添加' }}</text>
				</view>
			</view>
			<text class="home-bc-label">余额</text>
			<view class="home-bc-balance-row">
				<text class="home-bc-balance">¥{{ balance.toFixed(2) }}</text>
				<view class="home-recharge-btn" @click="goWallet">去充值</view>
			</view>
			<view class="home-bc-stats">
				<view class="home-bc-stat" @click="openMonthRecords">
					<text class="home-bc-stat-label">本月充电</text>
					<text class="home-bc-stat-val">{{ monthRecords.length }} 次 · ¥{{ monthFee.toFixed(2) }}</text>
				</view>
				<view class="home-bc-stat" @click="openMyCard">
					<text class="home-bc-stat-label">我的月卡</text>
					<text class="home-bc-stat-val">{{ myCard ? myCard.name : '未开通' }}</text>
				</view>
			</view>
		</view>

		<!-- 快捷入口 -->
		<view class="home-quick c-card">
			<view class="home-quick-item" @click="goScan">
				<view class="home-quick-ic green">
					<image src="/static/h5/images/charge/ic-scan.png" class="home-quick-ic-img" mode="aspectFit"></image>
				</view>
				<text class="home-quick-text">扫码充电</text>
			</view>
			<view class="home-quick-item" @click="goMonth">
				<view class="home-quick-ic orange">
					<image src="/static/h5/images/charge/ic-calendar.png" class="home-quick-ic-img" mode="aspectFit"></image>
				</view>
				<text class="home-quick-text">充电月卡</text>
			</view>
			<view class="home-quick-item" @click="goWallet">
				<view class="home-quick-ic blue">
					<image src="/static/h5/images/charge/ic-wallet.png" class="home-quick-ic-img" mode="aspectFit"></image>
				</view>
				<text class="home-quick-text">钱包余额</text>
			</view>
			<view class="home-quick-item" @click="goOrders">
				<view class="home-quick-ic violet">
					<image src="/static/h5/images/charge/ic-chart.png" class="home-quick-ic-img" mode="aspectFit"></image>
				</view>
				<text class="home-quick-text">充电订单</text>
			</view>
		</view>

		<!-- 充电类型切换 -->
		<view class="home-type-tabs c-card">
			<view class="home-type-tab" :class="{ active: chgType === 'bike' }" @click="switchType('bike')">
				<image v-if="chgType === 'bike'" src="/static/h5/images/charge/ic-bike-white.png" class="home-type-tab-ic" mode="aspectFit"></image>
				<image v-else src="/static/h5/images/charge/ic-bike.png" class="home-type-tab-ic" mode="aspectFit"></image>
				<text>电瓶车充电桩</text>
			</view>
			<view class="home-type-tab" :class="{ active: chgType === 'car' }" @click="switchType('car')">
				<image v-if="chgType === 'car'" src="/static/h5/images/charge/ic-car-white.png" class="home-type-tab-ic" mode="aspectFit"></image>
				<image v-else src="/static/h5/images/charge/ic-car.png" class="home-type-tab-ic" mode="aspectFit"></image>
				<text>电动车充电桩</text>
			</view>
		</view>

		<!-- 小区充电桩 -->
		<view class="home-pile-head">
			<text class="c-title">小区充电桩</text>
			<text class="c-text-sub">{{ curPiles.length ? freeCount + ' 个可用' : (pilesLoading ? '加载中…' : '暂无数据') }}</text>
		</view>
		<view class="home-piles">
			<view v-if="!curPiles.length && !pilesLoading" class="c-card home-pile-empty">
				<image src="/static/h5/images/charge/ic-plug.png" class="home-empty-ic" mode="aspectFit"></image>
				<text class="c-text-sub" style="margin-top:12rpx;">暂无{{ chgType === 'car' ? '电动车' : '电瓶车' }}充电桩</text>
			</view>
			<view class="c-card home-pile" v-for="(p, i) in curPiles" :key="p.machineId || i" @click="openPile(i)">
				<view class="home-pile-ic" :class="p.free ? (chgType === 'car' ? 'car' : 'bike') : 'busy'">
					<image v-if="chgType === 'car'" src="/static/h5/images/charge/ic-car.png" class="home-pile-ic-img" mode="aspectFit"></image>
					<image v-else src="/static/h5/images/charge/ic-bike.png" class="home-pile-ic-img" mode="aspectFit"></image>
				</view>
				<view style="flex:1;min-width:0;">
					<view class="home-pile-name-row">
						<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#0f172a;">{{ p.name }}</text>
						<text class="home-pile-state" :class="p.online === false ? 'off' : 'on'">{{ p.online === false ? '离线' : '在线' }}</text>
					</view>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ p.loc }}</text>
				</view>
				<view style="text-align:right;">
					<text class="home-pile-num" style="font-size:20rpx;font-weight:600;"><text :style="{ color: p.used > 0 ? '#d97706' : '#cbd5e1' }">{{ p.used }} 使用中</text> · <text :style="{ color: p.free ? (chgType === 'car' ? '#2563eb' : '#059669') : '#94a3b8' }">{{ p.sockets - p.used }} 空闲</text></text>
				</view>
			</view>
		</view>

		<!-- 小区切换弹层 -->
		<view class="c-mask" v-if="commShow" @click="commShow = false"></view>
		<view class="c-sheet" v-if="commShow">
			<view class="home-sheet-head">
				<text class="c-title" style="font-size:28rpx;">切换小区</text>
				<text class="home-sheet-close" @click="commShow = false">关闭</text>
			</view>
			<view class="home-sheet-body">
				<view class="home-locate-btn" @click="locateComm">
					<image src="/static/h5/images/common/ic-pin-green.png" class="home-locate-ic" mode="aspectFit"></image>
					<view style="flex:1;text-align:left;">
						<text class="home-locate-title">{{ locating ? '正在定位…' : (locateTxt || '定位附近小区') }}</text>
						<text class="home-locate-sub">{{ locating ? '获取当前位置中，请稍候' : (locateSub || '根据当前位置自动匹配最近小区') }}</text>
					</view>
					<text class="home-locate-action">{{ locating ? '定位中' : (locateTxt ? '重新定位' : '定位') }}</text>
				</view>
				<text class="home-sheet-tip">小区列表 · 切换后充电桩列表与月卡适用范围随小区变化</text>
			<view class="home-comm-list">
				<view v-if="!sortedComms.length" class="home-comm-empty">
					<image src="/static/h5/images/common/ic-home.png" class="home-comm-empty-ic" mode="aspectFit"></image>
					<text class="c-text-sub" style="margin-top:12rpx;">暂无小区数据</text>
				</view>
				<view v-for="(c, i) in sortedComms" :key="i" class="home-comm-item" :class="{ current: i === commIdx }"
					@click="pickComm(i)">
					<image src="/static/h5/images/common/ic-pin.png" class="home-comm-ic" mode="aspectFit"></image>
					<view style="flex:1;min-width:0;">
						<view class="home-comm-name-row">
							<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#1e293b;">{{ c.name }}</text>
							<text class="home-comm-current-tag" v-if="i === commIdx">当前</text>
						</view>
						<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ c.address || c.cityName || '' }}{{ c.dist ? ' · 距您 ' + fmtDist(c.dist) : '' }}</text>
					</view>
					<view class="home-go-btn" @click.stop="goComm(i)">去这儿</view>
				</view>
			</view>
			</view>
		</view>

		<!-- 导航模拟弹层 -->
		<view class="c-mask" v-if="navShow" @click="navShow = false"></view>
		<view class="c-dialog" v-if="navShow" style="text-align:center;">
			<text class="c-text-sub">正在导航至</text>
			<text class="c-text-bold" style="font-size:30rpx;font-weight:700;color:#0f172a;margin-top:8rpx;display:block;">{{ navComm.name }}</text>
			<text class="home-nav-dist" v-if="navComm.dist">距您 {{ fmtDist(navComm.dist) }}</text>
			<view class="home-nav-map">
				<view class="home-nav-route"></view>
				<image src="/static/h5/images/charge/ic-car-nav.png" class="home-nav-car" :style="{ left: navProgress + '%' }" mode="aspectFit"></image>
				<text class="home-nav-demo">模拟导航路线 · 演示环境</text>
			</view>
			<view class="home-nav-progress-row">
				<text class="c-text-sub" style="font-size:18rpx;">出发</text>
				<text class="c-text-sub" style="font-size:18rpx;">{{ navEta }}</text>
				<text class="c-text-sub" style="font-size:18rpx;">到达</text>
			</view>
			<view class="home-nav-bar-wrap">
				<view class="home-nav-bar" :style="{ width: navProgress + '%' }"></view>
			</view>
			<text class="home-nav-status">{{ navStatus }}</text>
			<view class="home-nav-btn" :class="{ done: navDone }" @click="closeNav">{{ navDone ? '完成' : '导航中…' }}</view>
		</view>

		<!-- 月卡详情弹窗 -->
		<view class="c-mask" v-if="cardDetailShow" @click="cardDetailShow = false"></view>
		<view class="c-dialog" v-if="cardDetailShow">
			<text class="c-text-sub">我的充电月卡</text>
			<view class="home-card-detail">
				<view class="home-card-detail-head">
					<text class="home-card-detail-name">{{ myCard.name }}</text>
					<text class="home-card-detail-type">{{ myCard.type === 'month' ? '畅充月卡' : '次卡' }}</text>
				</view>
				<view class="home-card-detail-left">
					<text class="home-card-detail-num">{{ myCard.left }}</text>
					<text class="home-card-detail-unit">{{ myCard.type === 'month' ? '天剩余' : '次剩余' }}</text>
				</view>
				<text class="home-card-detail-expire">有效期至 {{ myCard.expire }}</text>
			</view>
			<text class="c-text-sub" style="margin-top:20rpx;display:block;line-height:1.6;">{{ cardRule }}</text>
			<view class="home-card-detail-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="cardDetailShow = false">返回</view>
				<view class="c-btn-ghost" style="flex:1;" @click="goCardOrders">月卡订单</view>
				<view class="home-card-renew-btn" style="flex:1;" @click="renewCard">续费</view>
			</view>
		</view>

		<!-- 未开通月卡引导 -->
		<view class="c-mask" v-if="askBuyShow" @click="askBuyShow = false"></view>
		<view class="c-dialog" v-if="askBuyShow" style="text-align:center;">
			<view class="home-ask-ic">
				<image src="/static/h5/images/charge/ic-calendar.png" class="home-ask-ic-img" mode="aspectFit"></image>
			</view>
			<text class="c-text-bold" style="font-size:28rpx;font-weight:700;color:#0f172a;margin-top:20rpx;display:block;">尚未开通充电月卡</text>
			<text class="c-text-sub" style="margin-top:12rpx;display:block;line-height:1.6;">开通月卡后充电更省钱 · 是否前往购买？</text>
			<view class="home-ask-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="askBuyShow = false">取消</view>
				<view class="home-ask-go-btn" style="flex:1;" @click="goAskBuy">前往购买</view>
			</view>
		</view>

		<!-- 主题菜单 -->
		<theme-menu :show.sync="themeMenuShow" />

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
		<view style="height:40rpx;"></view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import themeMenu from '@/components/charge/theme-menu.vue'
	import {
		queryOwnerAccount
	} from '@/api/user/userApi.js'
	import {
		getUserCar
	} from '@/api/car/carApi.js'
	import {
		getChargeMachines,
		getChargeMachinePort,
		getUserChargeMonthOrder,
		getChargeMachineOrder
	} from '@/api/machine/machineApi.js'
	import {
		queryCommunitys,
		getCommunityId,
		saveCurrentCommunity
	} from '@/api/community/communityApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'

	export default {
		components: {
			themeMenu
		},
		data() {
			return {
				commShow: false,
				navShow: false,
				navComm: {},
				navProgress: 0,
				navEta: '',
				navStatus: '正在导航…',
				navDone: false,
				cardDetailShow: false,
				askBuyShow: false,
				themeMenuShow: false,
				locating: false,
				locateTxt: '',
				locateSub: '',
				toastMsg: '',
				curCommId: '',
				pilesLoading: false,
				storeVersion: 0
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			curCommunity() {
				this.storeVersion
				return chargeStore.curCommunity()
			},
			sortedComms() {
				this.storeVersion
				const list = chargeStore.communities.slice()
				return list.some(c => c.dist) ? list.sort((a, b) => (a.dist || 0) - (b.dist || 0)) : list
			},
			commIdx() {
				this.storeVersion
				return chargeStore.commIdx
			},
			chgType() {
				this.storeVersion
				return chargeStore.chgType
			},
			curPiles() {
				this.storeVersion
				return chargeStore.curPiles()
			},
			freeCount() {
				this.storeVersion
				return this.curPiles.filter(p => p.free).length
			},
			balance() {
				this.storeVersion
				return chargeStore.balance
			},
			myCard() {
				this.storeVersion
				return chargeStore.myCard
			},
			vehicles() {
				this.storeVersion
				return chargeStore.vehicles
			},
			runningOrder() {
				this.storeVersion
				return chargeStore.runningOrder
			},
			unread() {
				this.storeVersion
				return chargeStore.messages.filter(m => !m.read).length
			},
			rbSlotText() {
				const r = this.runningOrder
				if (!r) return ''
				return (r.type === 'car' ? '枪 ' : '插座 ') + (r.slot || '—') + ' · ' + (r.type === 'car' ? '7kW 慢充' : '220W')
			},
			rbTime() {
				const sec = Math.max(0, Math.floor((Date.now() - chargeStore.chgStart) / 1000))
				return ('0' + Math.floor(sec / 60)).slice(-2) + ':' + ('0' + sec % 60).slice(-2)
			},
			rbFee() {
				return chargeStore.chgFee.toFixed(2)
			},
			monthRecords() {
				this.storeVersion
				return chargeStore.records.filter(r => this._isThisMonth(r.date) && r.state === '2002')
			},
			monthFee() {
				return this.monthRecords.reduce((s, r) => s + (r.fee || 0), 0)
			},
			cardRule() {
				return this.myCard && this.myCard.type === 'month'
					? '月卡规则：畅充月卡每日首次充电前 4 小时免费，超出部分按 ¥0.25/小时计费；仅适用于电瓶车充电桩。'
					: '月卡规则：次卡按次抵扣，单次上限 8 小时；仅适用于电瓶车充电桩。'
			}
		},
		onShow() {
			// 主题恢复
			this._applyTheme()
			// 在充订单实时刷新
			if (chargeStore.runningOrder) {
				chargeStore.simulateTick()
			}
			if (this._timer) clearInterval(this._timer)
			this._timer = setInterval(() => {
				chargeStore.simulateTick()
			}, 1000)
			// 首页数据加载：余额/车辆/订单/小区/桩/月卡
			this._loadHomeData()
		},
		onHide() {
			if (this._timer) clearInterval(this._timer)
		},
		onUnload() {
			if (this._timer) clearInterval(this._timer)
		},
		methods: {
			_applyTheme() {
				const pages = getCurrentPages()
				const page = pages[pages.length - 1]
				if (page && page.$el) {
					page.$el.classList.toggle('charge-theme-dark', chargeStore.themeDark)
				}
			},
			_isThisMonth(dateStr) {
				if (!dateStr) return false
				if (dateStr.indexOf('今天') === 0 || dateStr.indexOf('昨天') === 0) return true
				const m = /^(\d{2})-/.exec(dateStr)
				return m ? +m[1] === new Date().getMonth() + 1 : true
			},
			fmtDist(m) {
				if (!m && m !== 0) return ''
				return m >= 1000 ? (m / 1000).toFixed(1) + ' km' : m + ' m'
			},
			switchType(t) {
				if (t === chargeStore.chgType) return
				chargeStore.setChgType(t)
				this.storeVersion++
				this._toast('已切换至' + (t === 'car' ? '电动车' : '电瓶车') + '充电桩')
				this._loadPiles()
			},
			openPile(i) {
				const p = this.curPiles[i]
				if (!p) return
				const url = chargeStore.chgType === 'car'
					? '/packageCharge/pages/charge/carPileDetail?machineId=' + (p.machineId || '') + '&pileIdx=' + i
					: '/packageCharge/pages/charge/bikePileDetail?machineId=' + (p.machineId || '') + '&pileIdx=' + i
				uni.navigateTo({
					url: url
				})
			},
			goScan() {
				uni.switchTab({ url: '/pages/charge/scanCharge' })
			},
			goMonth() {
				chargeStore.planFilter = null
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeMonth' })
			},
			goWallet() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/wallet' })
			},
			goOrders() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeOrders' })
			},
			openMsg() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/message' })
			},
			openRunningOrder() {
				uni.navigateTo({
					url: '/packageCharge/pages/charge/orderDetail?mode=running'
				})
			},
			openMonthRecords() {
				chargeStore.recRange = 'month'
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeOrders' })
			},
			openMyCard() {
				if (!this.myCard) {
					this.askBuyShow = true
					return
				}
				this.cardDetailShow = true
			},
			goAskBuy() {
				this.askBuyShow = false
				this.goMonth()
			},
			goCardOrders() {
				this.cardDetailShow = false
				chargeStore.planFilter = null
				uni.navigateTo({
					url: '/packageCharge/pages/charge/chargeMonth?tab=orders'
				})
			},
			renewCard() {
				this.cardDetailShow = false
				chargeStore.planFilter = this.myCard.type
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeMonth' })
			},
			editHomeCar() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/carService' })
			},
			pickComm(i) {
				const list = chargeStore.communities
				if (!list[i]) return
				chargeStore.setCommIdx(i)
				this.commShow = false
				this.curCommId = list[i].communityId || this.curCommId
				this.storeVersion++
				this._toast('已切换至「' + list[i].name + '」· 充电桩列表已更新')
				this._loadPiles()
				this._loadCard()
			},
			locateComm() {
				if (this.locating) return
				const list = chargeStore.communities
				if (!list.length) {
					this._toast('暂无小区数据')
					return
				}
				if (!list.some(c => c.dist)) {
					this._toast('无距离数据，请手动选择小区')
					return
				}
				this.locating = true
				this.locateTxt = ''
				setTimeout(() => {
					this.locating = false
					const nearest = chargeStore.communities.reduce((best, c, i) => (c.dist < chargeStore.communities[best]
						.dist ? i : best), 0)
					this.locateTxt = '已定位 · ' + chargeStore.communities[nearest].name
					this.locateSub = '距您 ' + this.fmtDist(chargeStore.communities[nearest].dist) + ' · 已自动选择最近小区'
					chargeStore.setCommIdx(nearest)
					this.curCommId = chargeStore.communities[nearest].communityId || this.curCommId
					this.storeVersion++
					this._loadPiles()
					this._loadCard()
					this._toast('已定位至「' + chargeStore.communities[nearest].name + '」· 距您 ' + this.fmtDist(chargeStore
						.communities[nearest].dist))
				}, 900)
			},
			goComm(i) {
				if (this.navShow) return
				this.navComm = chargeStore.communities[i]
				this.navProgress = 0
				this.navEta = this.navComm && this.navComm.dist ? '预计 ' + Math.max(2, Math.round(this.navComm.dist / 120)) +
					' 分钟' : '即将到达'
				this.navStatus = '正在导航…'
				this.navDone = false
				this.navShow = true
				setTimeout(() => {
					this.navProgress = 100
				}, 60)
				setTimeout(() => {
					this.navStatus = '已到达 · ' + this.navComm.name
					this.navDone = true
					this._toast('导航结束 · 已到达「' + this.navComm.name + '」')
				}, 3000)
			},
			closeNav() {
				if (!this.navDone) return
				this.navShow = false
			},
			// ================= 接口数据加载 =================
			_loadHomeData() {
				this.curCommId = this.curCommId || getCommunityId()
				this._loadCommunitys()
				this._loadBalance()
				this._loadCars()
				this._loadMonthOrders()
			},
			// 小区列表（成功后联动桩列表与月卡）
			_loadCommunitys() {
				queryCommunitys({
					page: 1,
					row: 50
				}).then(res => {
					if (!res || res.code != 0 || !res.data) throw new Error('加载失败')
					const list = res.data.map(c => this._adaptComm(c))
					chargeStore.communities = list
					// commIdx 越界时定位：优先登录小区，否则取第一个
					if (chargeStore.commIdx >= list.length) {
						const curId = getCommunityId()
						const idx = list.findIndex(c => c.communityId === curId)
						chargeStore.setCommIdx(idx >= 0 ? idx : 0)
					}
					this.curCommId = list.length ? (list[chargeStore.commIdx] || list[0]).communityId : getCommunityId()
					this.storeVersion++
					this._loadPiles()
					this._loadCard()
				}).catch(() => {
					chargeStore.communities = []
					this.curCommId = getCommunityId()
					this.storeVersion++
					this._toast('小区列表加载失败')
					this._loadPiles()
					this._loadCard()
				})
			},
			// 余额（需登录，未登录展示默认值 0）
			_loadBalance() {
				if (!hasLogin()) {
					chargeStore.setBalance(0)
					return
				}
				const commId = this.curCommId || getCommunityId()
				queryOwnerAccount({
					communityId: commId
				}).then(list => {
					const amt = list && list.length ? parseFloat(list[0].amount || 0) : 0
					chargeStore.setBalance(isNaN(amt) ? 0 : amt)
					this.storeVersion++
				}).catch(() => {
					chargeStore.setBalance(0)
					this.storeVersion++
					this._toast('余额加载失败')
				})
			},
			// 我的车辆（需登录，未登录展示默认空）
			_loadCars() {
				if (!hasLogin()) {
					chargeStore.vehicles = []
					return
				}
				const commId = this.curCommId || getCommunityId()
				getUserCar({
					communityId: commId
				}).then(list => {
					chargeStore.vehicles = (list || []).map(c => this._adaptCar(c))
					chargeStore.saveVehicles()
					this.storeVersion++
				}).catch(() => {
					chargeStore.vehicles = []
					this.storeVersion++
					this._toast('车辆加载失败')
				})
			},
			// 本月充电（需登录，未登录展示默认空，统计由 monthRecords/monthFee 完成）
			_loadMonthOrders() {
				if (!hasLogin()) {
					chargeStore.records = []
					return
				}
				getChargeMachineOrder({
					page: 1,
					row: 50
				}).then(list => {
					chargeStore.records = (list || []).map(o => this._adaptOrder(o))
					chargeStore.saveRecords()
					this.storeVersion++
				}).catch(() => {
					chargeStore.records = []
					this.storeVersion++
					this._toast('充电订单加载失败')
				})
			},
			// 充电桩列表（按当前小区 + 类型）
			_loadPiles() {
				const commId = this.curCommId || getCommunityId()
				if (!commId || commId === '-1') return
				const seq = (this._pileSeq = (this._pileSeq || 0) + 1)
				this.pilesLoading = true
				getChargeMachines({
					communityId: commId,
					chargeType: chargeStore.chgType === 'car' ? '1002' : '1001',
					page: 1,
					row: 100
				}).then(res => {
					if (seq !== this._pileSeq) return
					if (!res || res.code != 0 || !res.data) throw new Error('加载失败')
					const list = res.data.map(m => this._adaptMachine(m))
					chargeStore.setMachineList(list, commId)
					this.storeVersion++
					this._loadPilePorts(list, commId, seq)
				}).catch(() => {
					if (seq !== this._pileSeq) return
					chargeStore.setMachineList([], commId)
					this.storeVersion++
					this._toast('充电桩加载失败，请重试')
				}).then(() => {
					this.pilesLoading = false
				})
			},
			// 充电桩端口（插座/枪）统计：失败时显示 0 使用中 / portCount 空闲
			_loadPilePorts(list, commId, seq) {
				if (!list || !list.length) return
				if (list.length > 10) return
				list.forEach(m => {
					getChargeMachinePort({
						communityId: commId,
						machineId: m.machineId,
						page: 1,
						row: 100
					}).then(res => {
						if (seq !== this._pileSeq || chargeStore.machineListCommId !== commId) return
						if (!res || res.code != 0 || !res.data || !res.data.length) return
						const busy = res.data.filter(p => p.state !== 'FREE').length
						const ports = res.data.map(p => ({
							n: p.portName || p.portCode || '',
							busy: p.state !== 'FREE'
						}))
						m.used = busy
						m.free = busy < (m.sockets || 1)
						m.slots = ports
						m.guns = ports
						chargeStore.setMachineList(chargeStore.machineList.slice(), commId)
						this.storeVersion++
					}).catch(() => {
						if (seq !== this._pileSeq || chargeStore.machineListCommId !== commId) return
						m.used = 0
						m.free = true
						this.storeVersion++
					})
				})
			},
			// 我的月卡（需登录，未登录展示默认"未开通"，取未过期的最新一条）
			_loadCard() {
				if (!hasLogin()) {
					chargeStore.setMyCard(null)
					return
				}
				const commId = this.curCommId || getCommunityId()
				getUserChargeMonthOrder({
					communityId: commId,
					page: 1,
					row: 30
				}).then(list => {
					const now = new Date()
					now.setHours(0, 0, 0, 0)
					const valid = (list || [])
						.map(o => ({ o: o, t: this._parseDate(o.endTime) }))
						.filter(x => x.t && x.t.getTime() >= now.getTime())
						.sort((a, b) => b.t.getTime() - a.t.getTime())
					chargeStore.setMyCard(valid.length ? this._adaptCard(valid[0].o) : null)
					this.storeVersion++
				}).catch(() => {
					chargeStore.setMyCard(null)
					this.storeVersion++
					this._toast('月卡加载失败')
				})
			},
			// ================= 字段适配 =================
			_adaptComm(c) {
				return {
					communityId: c.communityId || '',
					name: c.name || '',
					address: c.address || '',
					cityName: c.cityName || '',
					dist: c.dist || 0
				}
			},
			_adaptMachine(m) {
				const portCount = parseInt(m.portCount || 0, 10) || 0
				return {
					machineId: m.machineId || '',
					name: m.machineName || '',
					code: m.machineCode || '',
					online: m.state !== 'OFFLINE',
					sockets: portCount,
					used: 0,
					free: true,
					loc: (m.communityName || '') + (m.machineCode ? ' · ' + m.machineCode : ''),
					type: m.chargeType === '1002' ? 'car' : 'bike',
					chargeType: m.chargeType || '',
					slots: [],
					guns: []
				}
			},
			_adaptOrder(o) {
				const state = o.state || ''
				const hours = parseFloat(o.chargeHours || 0)
				return {
					orderId: o.orderId || '',
					pile: o.machineName || '',
					date: this._fmtTime(o.createTime),
					mins: isNaN(hours) ? 0 : Math.round(hours * 60),
					fee: parseFloat(o.amount || 0) || 0,
					way: '余额',
					st: state === '2002' ? '已完成' : state === '1001' ? '充电中' : state === '3003' ? '充电失败' : (o.stateName ||
						''),
					state: state,
					type: o.chargeType === '1002' ? 'car' : 'bike',
					kwh: parseFloat(o.energy || 0) || 0,
					held: 0
				}
			},
			_adaptCard(c) {
				const end = this._parseDate(c.endTime)
				const days = end ? Math.max(0, Math.ceil((end.getTime() - Date.now()) / 86400000)) : 0
				return {
					name: c.cardName || '',
					type: 'month',
					left: days,
					expire: c.endTime ? String(c.endTime).slice(0, 10) : '',
					orderId: c.orderId || '',
					cardId: c.cardId || '',
					endTime: c.endTime || '',
					startTime: c.startTime || ''
				}
			},
			_adaptCar(c) {
				return {
					carId: c.carId || '',
					plate: c.carNum || '',
					type: 'car'
				}
			},
			_parseDate(s) {
				if (!s) return null
				let t = s
				if (typeof s === 'string') t = s.replace(/-/g, '/')
				const d = new Date(t)
				return isNaN(d.getTime()) ? null : d
			},
			_fmtTime(t) {
				if (!t) return ''
				if (typeof t === 'number') {
					const d = new Date(t < 1e12 ? t * 1000 : t)
					if (isNaN(d.getTime())) return String(t)
					return this._pad(d.getMonth() + 1) + '-' + this._pad(d.getDate()) + ' ' + this._pad(d.getHours()) + ':' + this
						._pad(d.getMinutes())
				}
				const s = String(t)
				const m = /^(\d{4})[-/](\d{2})[-/](\d{2})[ T](\d{2}):(\d{2})/.exec(s)
				if (m) return m[2] + '-' + m[3] + ' ' + m[4] + ':' + m[5]
				return s
			},
			_pad(n) {
				return ('0' + n).slice(-2)
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
	.home-page {
		padding-bottom: 40rpx;
	}

	/* 导航 */
	.home-nav {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 16rpx 28rpx 0;
	}

	.home-nav-title {
		font-size: 30rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.home-nav-right {
		display: flex;
		align-items: center;
		gap: 16rpx;
	}

	.home-nav-ic {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f1f5f9;
	}

	.home-nav-ic-img {
		width: 32rpx;
		height: 32rpx;
	}

	.home-capsule-theme-img {
		width: 26rpx;
		height: 26rpx;
	}

	.home-msg-badge {
		position: absolute;
		top: -4rpx;
		right: -4rpx;
		min-width: 28rpx;
		height: 28rpx;
		padding: 0 8rpx;
		border-radius: 999rpx;
		background: #ef4444;
		color: #fff;
		font-size: 16rpx;
		font-weight: 700;
		display: flex;
		align-items: center;
		justify-content: center;
		border: 2rpx solid #fff;
	}

	.home-capsule {
		display: flex;
		align-items: center;
		gap: 12rpx;
		background: #fff;
		border: 1rpx solid rgba(0, 0, 0, .08);
		border-radius: 999rpx;
		padding: 6rpx 14rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, .08);
	}

	.home-capsule-dots {
		display: flex;
		gap: 6rpx;
	}

	.home-dot {
		width: 8rpx;
		height: 8rpx;
		border-radius: 50%;
		background: #111;
	}

	.home-capsule-divider {
		width: 1rpx;
		height: 28rpx;
		background: rgba(0, 0, 0, .12);
	}

	.home-capsule-theme {
		padding: 4rpx;
	}

	/* 在充订单提示条 */
	.home-running-badge {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #fbbf24, #f97316);
		padding: 24rpx;
		color: #fff;
		box-shadow: 0 10rpx 24rpx rgba(249, 115, 22, .25);
	}

	.home-rb-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.home-rb-title-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.home-rb-title {
		font-size: 24rpx;
		font-weight: 700;
	}

	.home-rb-slot {
		font-size: 18rpx;
		background: rgba(255, 255, 255, .25);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.home-rb-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 14rpx;
	}

	.home-rb-text {
		font-size: 20rpx;
		color: rgba(255, 255, 255, .85);
	}

	.home-rb-link {
		font-size: 20rpx;
		font-weight: 700;
	}

	/* 余额卡 */
	.home-balance-card {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		padding: 28rpx;
		position: relative;
		overflow: hidden;
		box-shadow: 0 12rpx 30rpx rgba(16, 185, 129, .2);
	}

	.home-bc-decor1 {
		position: absolute;
		right: -40rpx;
		top: -40rpx;
		width: 160rpx;
		height: 160rpx;
		opacity: .12;
		transform: rotate(12deg);
	}

	.home-bc-ic {
		width: 22rpx;
		height: 22rpx;
	}

	.home-bc-arrow {
		width: 18rpx;
		height: 18rpx;
	}

	.home-bc-decor2 {
		position: absolute;
		right: -20rpx;
		bottom: -80rpx;
		width: 280rpx;
		height: 280rpx;
		border-radius: 50%;
		border: 20rpx solid rgba(255, 255, 255, .08);
	}

	.home-bc-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.home-comm-btn {
		display: flex;
		align-items: center;
		gap: 8rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 10rpx 22rpx;
		color: #fff;
	}

	.home-comm-name {
		font-size: 24rpx;
		font-weight: 600;
	}

	.home-comm-dist {
		font-size: 18rpx;
		color: rgba(255, 255, 255, .75);
	}

	.home-car-btn {
		display: flex;
		align-items: center;
		gap: 8rpx;
		background: rgba(255, 255, 255, .15);
		border-radius: 999rpx;
		padding: 10rpx 22rpx;
		color: #fff;
	}

	.home-car-label {
		font-size: 20rpx;
		font-weight: 600;
	}

	.home-car-plate {
		font-size: 18rpx;
		font-weight: 700;
		background: #1d4ed8;
		border-radius: 8rpx;
		padding: 4rpx 10rpx;
		letter-spacing: 2rpx;
	}

	.home-bc-label {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 28rpx;
	}

	.home-bc-balance-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 6rpx;
	}

	.home-bc-balance {
		color: #fff;
		font-size: 56rpx;
		font-weight: 700;
		line-height: 1;
	}

	.home-recharge-btn {
		font-size: 20rpx;
		font-weight: 700;
		color: #059669;
		background: #fff;
		border-radius: 999rpx;
		padding: 12rpx 28rpx;
	}

	.home-bc-stats {
		display: flex;
		gap: 16rpx;
		margin-top: 24rpx;
	}

	.home-bc-stat {
		flex: 1;
		background: rgba(255, 255, 255, .1);
		border-radius: 20rpx;
		padding: 16rpx 22rpx;
	}

	.home-bc-stat-label {
		display: block;
		color: rgba(255, 255, 255, .6);
		font-size: 18rpx;
	}

	.home-bc-stat-val {
		display: block;
		color: #fff;
		font-size: 24rpx;
		font-weight: 700;
		margin-top: 6rpx;
	}

	/* 快捷入口 */
	.home-quick {
		margin: 24rpx 28rpx 0;
		display: flex;
		justify-content: space-around;
		padding: 24rpx 0;
	}

	.home-quick-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 10rpx;
	}

	.home-quick-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.home-quick-ic-img {
		width: 44rpx;
		height: 44rpx;
	}

	.home-quick-ic.green {
		background: rgba(16, 185, 129, .1);
	}

	.home-quick-ic.orange {
		background: rgba(249, 115, 22, .1);
	}

	.home-quick-ic.blue {
		background: rgba(59, 130, 246, .1);
	}

	.home-quick-ic.violet {
		background: rgba(139, 92, 246, .1);
	}

	.home-quick-text {
		font-size: 20rpx;
		font-weight: 600;
		color: #334155;
	}

	/* 类型切换 */
	.home-type-tabs {
		margin: 24rpx 28rpx 0;
		display: flex;
		padding: 8rpx;
	}

	.home-type-tab {
		flex: 1;
		height: 76rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 8rpx;
		font-size: 24rpx;
		font-weight: 600;
		color: #64748b;
		border-radius: 20rpx;
		transition: all .2s;
	}

	.home-type-tab-ic {
		width: 28rpx;
		height: 28rpx;
		flex-shrink: 0;
	}

	.home-type-tab.active {
		background: #10b981;
		color: #fff;
		box-shadow: 0 4rpx 10rpx rgba(16, 185, 129, .35);
	}

	/* 桩列表 */
	.home-pile-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 32rpx 28rpx 16rpx;
	}

	.home-piles {
		margin: 0 28rpx;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.home-pile {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 24rpx;
	}

	.home-pile-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 60rpx 0;
	}

	.home-pile-ic {
		width: 80rpx;
		height: 80rpx;
		border-radius: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.home-pile-ic-img {
		width: 40rpx;
		height: 40rpx;
	}

	.home-empty-ic {
		width: 56rpx;
		height: 56rpx;
	}

	.home-pile-ic.bike {
		background: rgba(16, 185, 129, .1);
	}

	.home-pile-ic.car {
		background: rgba(59, 130, 246, .1);
	}

	.home-pile-ic.busy {
		background: #f1f5f9;
		opacity: .6;
	}

	.home-pile-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.home-pile-state {
		font-size: 16rpx;
		font-weight: 600;
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.home-pile-state.on {
		background: rgba(16, 185, 129, .1);
		color: #059669;
		border: 1rpx solid rgba(16, 185, 129, .25);
	}

	.home-pile-state.off {
		background: #f1f5f9;
		color: #64748b;
		border: 1rpx solid #e2e8f0;
	}

	/* 小区切换弹层 */
	.home-sheet-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 28rpx 36rpx 16rpx;
	}

	.home-sheet-close {
		font-size: 20rpx;
		color: #94a3b8;
		padding: 8rpx;
	}

	.home-sheet-body {
		padding: 0 36rpx 60rpx;
	}

	.home-locate-btn {
		display: flex;
		align-items: center;
		gap: 16rpx;
		border: 1rpx solid rgba(16, 185, 129, .3);
		background: rgba(16, 185, 129, .05);
		border-radius: 20rpx;
		padding: 20rpx 24rpx;
	}

	.home-locate-ic {
		width: 30rpx;
		height: 30rpx;
		flex-shrink: 0;
	}

	.home-locate-title {
		display: block;
		font-size: 24rpx;
		font-weight: 700;
		color: #047857;
	}

	.home-locate-sub {
		display: block;
		font-size: 18rpx;
		color: rgba(16, 185, 129, .8);
		margin-top: 4rpx;
	}

	.home-locate-action {
		font-size: 20rpx;
		color: #059669;
		font-weight: 600;
	}

	.home-sheet-tip {
		display: block;
		font-size: 20rpx;
		color: #94a3b8;
		margin: 20rpx 0 12rpx;
	}

	.home-comm-list {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.home-comm-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 60rpx 0;
	}

	.home-comm-empty-ic {
		width: 60rpx;
		height: 60rpx;
	}

	.home-comm-item {
		display: flex;
		align-items: center;
		gap: 16rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 20rpx;
		padding: 20rpx;
	}

	.home-comm-ic {
		width: 30rpx;
		height: 30rpx;
		flex-shrink: 0;
	}

	.home-comm-item.current {
		border-color: #10b981;
		background: rgba(16, 185, 129, .05);
	}

	.home-comm-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.home-comm-current-tag {
		font-size: 16rpx;
		color: #059669;
		background: rgba(16, 185, 129, .1);
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.home-go-btn {
		font-size: 20rpx;
		font-weight: 700;
		color: #059669;
		border: 1rpx solid rgba(16, 185, 129, .3);
		background: rgba(16, 185, 129, .05);
		border-radius: 16rpx;
		padding: 10rpx 20rpx;
		flex-shrink: 0;
	}

	/* 导航模拟 */
	.home-nav-dist {
		display: block;
		font-size: 20rpx;
		color: #059669;
		font-weight: 600;
		margin-top: 8rpx;
	}

	.home-nav-map {
		position: relative;
		margin-top: 32rpx;
		border-radius: 20rpx;
		background: linear-gradient(180deg, #ecfdf5, #eff6ff);
		border: 1rpx solid #f1f5f9;
		height: 200rpx;
		overflow: hidden;
	}

	.home-nav-route {
		position: absolute;
		left: 24rpx;
		right: 24rpx;
		top: 24rpx;
		bottom: 24rpx;
		border: 2rpx dashed rgba(16, 185, 129, .3);
		border-radius: 16rpx;
	}

	.home-nav-car {
		position: absolute;
		top: 50%;
		transform: translate(-50%, -50%);
		width: 44rpx;
		height: 44rpx;
		transition: left 3s linear;
		z-index: 2;
	}

	.home-nav-demo {
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
		bottom: 12rpx;
		font-size: 16rpx;
		color: #059669;
	}

	.home-nav-progress-row {
		display: flex;
		justify-content: space-between;
		margin-top: 24rpx;
	}

	.home-nav-bar-wrap {
		height: 12rpx;
		border-radius: 999rpx;
		background: #f1f5f9;
		overflow: hidden;
		margin-top: 8rpx;
	}

	.home-nav-bar {
		height: 100%;
		border-radius: 999rpx;
		background: linear-gradient(90deg, #34d399, #059669);
		transition: width 3s linear;
	}

	.home-nav-status {
		display: block;
		font-size: 22rpx;
		font-weight: 600;
		color: #334155;
		margin-top: 20rpx;
	}

	.home-nav-btn {
		margin-top: 20rpx;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
		border-radius: 20rpx;
		background: #10b981;
		color: #fff;
		font-size: 24rpx;
		font-weight: 700;
		opacity: .5;
	}

	.home-nav-btn.done {
		opacity: 1;
	}

	/* 月卡详情 */
	.home-card-detail {
		margin-top: 20rpx;
		border-radius: 24rpx;
		background: linear-gradient(135deg, #fb923c, #f97316);
		padding: 28rpx;
	}

	.home-card-detail-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.home-card-detail-name {
		color: #fff;
		font-size: 28rpx;
		font-weight: 700;
	}

	.home-card-detail-type {
		color: #fff;
		font-size: 16rpx;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 4rpx 14rpx;
	}

	.home-card-detail-left {
		display: flex;
		align-items: baseline;
		margin-top: 20rpx;
	}

	.home-card-detail-num {
		color: #fff;
		font-size: 56rpx;
		font-weight: 700;
		line-height: 1;
	}

	.home-card-detail-unit {
		color: rgba(255, 255, 255, .75);
		font-size: 20rpx;
		margin-left: 8rpx;
	}

	.home-card-detail-expire {
		display: block;
		color: rgba(255, 255, 255, .75);
		font-size: 18rpx;
		margin-top: 12rpx;
	}

	.home-card-detail-btns {
		display: flex;
		gap: 16rpx;
		margin-top: 24rpx;
	}

	.home-card-renew-btn {
		background: #f97316;
		color: #fff;
		font-size: 24rpx;
		font-weight: 700;
		border-radius: 24rpx;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
	}

	.home-ask-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 50%;
		background: rgba(249, 115, 22, .1);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 16rpx auto 0;
	}

	.home-ask-ic-img {
		width: 44rpx;
		height: 44rpx;
	}

	.home-ask-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}

	.home-ask-go-btn {
		background: #f97316;
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
		border-radius: 24rpx;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
	}

	/* 黑夜主题首页覆盖 */
	.charge-theme-dark .home-nav-title {
		color: #e2e8f0 !important;
	}

	.charge-theme-dark .home-capsule {
		background: #1e293b !important;
		border-color: rgba(255, 255, 255, .1) !important;
	}

	.charge-theme-dark .home-capsule-divider {
		background: rgba(255, 255, 255, .14) !important;
	}

	.charge-theme-dark .home-dot {
		background: #e2e8f0 !important;
	}

	.charge-theme-dark .home-quick-text {
		color: #cbd5e1 !important;
	}
</style>
