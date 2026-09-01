<template>
	<view class="home-page" :class="{ 'charge-theme-dark': themeDark }">
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
					<text class="home-car-label">充电车辆</text>
					<text class="home-car-plate">{{ vehicles.length ? vehicles[0].plate : '去添加' }}</text>
				</view>
			</view>
			<text class="home-bc-label">余额</text>
			<view class="home-bc-balance-row">
				<text class="home-bc-balance">¥{{ balance.toFixed(2) }}</text>
				<view class="home-recharge-btn" @click="goRecharge">去充值</view>
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
		queryOwnerAccount,
		getUserChargeCar
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
	import {
		adaptOrder
	} from '@/api/charge/orderAdapter.js'

	export default {
		components: {
			themeMenu
		},
		data() {
			return {
				cardDetailShow: false,
				askBuyShow: false,
				themeMenuShow: false,
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
				// #ifdef H5
				// H5 无法调用 uni.scanCode，提示用户使用微信扫一扫充电
				this._toast('请使用微信扫一扫充电')
				// #endif
				// #ifdef MP-WEIXIN
				// 小程序：进入扫码页，页面 onShow 自动调起微信原生扫一扫（uni.scanCode）
				uni.switchTab({ url: '/pages/charge/scanCharge' })
				// #endif
			},
			goMonth() {
				if (!hasLogin()) {
					this._toast('登录后才能使用')
					return
				}
				chargeStore.planFilter = null
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeMonth' })
			},
			goWallet() {
				if (!hasLogin()) {
					this._toast('登录后才能使用')
					return
				}
				uni.navigateTo({ url: '/packageCharge/pages/charge/wallet' })
			},
			goOrders() {
				if (!hasLogin()) {
					this._toast('登录后才能使用')
					return
				}
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeOrders' })
			},
			// 余额卡"去充值"→ 账户充值页面
			goRecharge() {
				if (!hasLogin()) {
					this._toast('登录后才能使用')
					return
				}
				uni.navigateTo({
					url: '/packageAccount/pages/account/preStoreAccount?communityId=' + getCommunityId()
				})
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
				if (!hasLogin()) {
					this._toast('登录后才能使用')
					return
				}
				uni.navigateTo({ url: '/packageCharge/pages/charge/addChargeCar' })
			},
			// 打开切换小区页面（独立页面，非弹层）
			goChangeCommunity() {
				uni.navigateTo({ url: '/packageProperty/pages/property/changeCommunity' })
			},
			// ================= 接口数据加载 =================
			_loadHomeData() {
				this.curCommId = this.curCommId || getCommunityId()
				this._loadCommunitys()
				this._loadBalance()
				this._loadCars()
				this._loadMonthOrders()
			},
			// 小区列表：先查缓存，有缓存不加载直接用；无缓存才查询并把第一个设为默认
			_loadCommunitys() {
				// 1) 先检查是否有当前小区缓存
				const cached = uni.getStorageSync('currentCommunityInfo')
				if (cached && cached.communityId) {
					// 有缓存：不加载小区列表，直接用缓存小区
					// （chargeStore.communities 为空时（如 H5 刷新后首次进入）用缓存构造展示列表，
					//   保证 curPiles() 的 machineListCommId 校验通过）
					if (!chargeStore.communities.length) {
						chargeStore.communities = [{
							communityId: cached.communityId,
							name: cached.communityName || '无',
							address: '',
							cityName: '',
							dist: 0
						}]
						chargeStore.setCommIdx(0)
					}
					this.curCommId = cached.communityId || getCommunityId()
					this.storeVersion++
					this._loadPiles()
					this._loadCard()
					return
				}
				// 2) 无缓存：加载小区列表，把第一个小区设置为默认小区
				queryCommunitys({
					page: 1,
					row: 50
				}).then(res => {
					if (!res || res.code != 0 || !res.data) throw new Error('加载失败')
					const list = res.data.map(c => this._adaptComm(c))
					chargeStore.communities = list
					if (!list.length) {
						// 查询无数据：缓存"无"小区（-1）
						saveCurrentCommunity({
							communityId: '-1',
							communityName: '无'
						})
						chargeStore.setCommIdx(0)
					} else {
						// 缓存第一个小区为默认小区
						saveCurrentCommunity(list[0])
						chargeStore.setCommIdx(0)
					}
					this.curCommId = (chargeStore.communities[chargeStore.commIdx] || list[0] || {}).communityId || getCommunityId()
					this.storeVersion++
					this._loadPiles()
					this._loadCard()
				}).catch(() => {
					chargeStore.communities = []
					// 接口失败：无缓存时缓存"无"小区，有缓存时保留原缓存
					if (!uni.getStorageSync('currentCommunityInfo')) {
						saveCurrentCommunity({
							communityId: '-1',
							communityName: '无'
						})
					}
					chargeStore.setCommIdx(0)
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
			// 我的车辆（需登录，未登录展示默认空；充电车牌已设置时置顶展示）
			_loadCars() {
				if (!hasLogin()) {
					chargeStore.vehicles = []
					return
				}
				const commId = this.curCommId || getCommunityId()
				const carP = getUserCar({
					communityId: commId
				}).then(list => (list || []).map(c => this._adaptCar(c))).catch(() => {
					this._toast('车辆加载失败')
					return []
				})
				const chargeCarP = getUserChargeCar({
					page: 1,
					row: 1
				}).then(res => (res && res.code == '0' && res.data) ? String(res.data).replace(/·/g, '') : '').catch(() => '')
				Promise.all([carP, chargeCarP]).then(([list, plate]) => {
					chargeStore.vehicles = list.filter(v => v.plate !== plate)
					if (plate) {
						chargeStore.vehicles.unshift({
							carId: '',
							plate: plate,
							type: 'car'
						})
					}
					chargeStore.saveVehicles()
					this.storeVersion++
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
					chargeStore.records = (list || []).map(o => adaptOrder(o))
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
		pointer-events: none;
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
		pointer-events: none;
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
