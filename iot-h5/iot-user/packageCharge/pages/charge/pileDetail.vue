<template>
	<view class="pd-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 桩信息卡 -->
		<view class="c-card pd-mx pd-mt">
			<view class="pd-pile-head">
				<view class="pd-pile-icon">⚡</view>
				<view class="pd-pile-info">
					<view class="pd-pile-name-row">
						<text class="c-title">{{ pile.name }}</text>
						<text class="c-badge" :class="pile.free ? 'c-badge-green' : 'c-badge-orange'">{{ pile.free ? '空闲' : '使用中' }}</text>
					</view>
					<text class="c-text-sub pd-pile-loc">{{ pile.loc }}</text>
				</view>
				<view class="pd-pile-side">
					<text class="c-text-sub">{{ cfg.kind }}</text>
					<view class="pd-online" :class="pile.online === false ? 'offline' : 'online'">
						<view class="pd-dot"></view>
						<text>{{ pile.online === false ? '离线' : '在线' }}</text>
					</view>
				</view>
			</view>

			<!-- 收费标准：仅电瓶车桩显示 -->
			<view class="pd-price-row" v-if="chgType === 'bike'">
				<view class="pd-price-label">
					<view class="pd-price-ic">¥</view>
					<text class="c-text">收费标准</text>
				</view>
				<text class="pd-price-text">{{ bikePriceText(cfg) }}</text>
			</view>

			<!-- 选择充电枪（电动车） -->
			<view class="pd-gun-box" v-if="chgType === 'car' && guns.length > 0">
				<view class="pd-sec-head">
					<text class="c-title">{{ fromScan ? '选择充电枪' : '充电枪状态' }}</text>
					<text class="c-text-sub">{{ gunTip }}</text>
				</view>
				<view class="pd-gun-list">
					<view v-for="(g, k) in guns" :key="k" class="c-gun-btn" :class="{ active: curGun === k, disabled: g.busy }"
						@click="pickGun(k)">
						<text style="font-size:28rpx;font-weight:700;">{{ g.n }} 枪</text>
						<text style="font-size:18rpx;" :style="{ color: g.busy ? '#f87171' : '#10b981' }">{{ g.busy ? '使用中' : '空闲' }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 分时电价（电动车 · 只读） -->
		<view class="c-card pd-mx pd-mt" v-if="chgType === 'car'">
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
			<view class="pd-tariff-current" :style="{ background: curTariff.color + '18' }">
				<view class="pd-tc-left">
					<view class="pd-tc-name-row">
						<view class="pd-tc-dot" :style="{ background: curTariff.color }"></view>
						<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#1e293b;">{{ curTariff.name }}段</text>
						<text class="pd-tc-now-tag">当前</text>
					</view>
					<text class="c-text-sub" style="margin-top:6rpx;display:block;">{{ curTariff.time }}</text>
				</view>
				<view class="pd-tc-rate" :style="{ color: curTariff.color }">
					<text style="font-size:44rpx;font-weight:700;">¥{{ curTariff.rate.toFixed(2) }}</text>
					<text class="c-text-sub">/度</text>
					<text class="pd-tc-rate-sub">电费 ¥{{ curTariff.elec.toFixed(2) }} + 服务费 ¥{{ curTariff.serv.toFixed(2) }}</text>
				</view>
			</view>
			<view v-if="tariffMore" class="pd-tariff-more">
				<view class="pd-tariff-more-item" v-for="(t, i) in otherTariffs" :key="i">
					<view class="pd-tc-dot" :style="{ background: t.color }"></view>
					<text class="c-text-bold" style="font-size:22rpx;font-weight:700;color:#334155;width:80rpx;">{{ t.name }}段</text>
					<text class="c-text-sub" style="flex:1;">{{ t.time }}</text>
					<view style="text-align:right;">
						<text class="c-text-bold" style="font-size:22rpx;font-weight:700;color:#334155;">¥{{ t.rate.toFixed(2) }}/度</text>
						<text class="c-text-sub" style="display:block;">电 ¥{{ t.elec.toFixed(2) }} + 服 ¥{{ t.serv.toFixed(2) }}</text>
					</view>
				</view>
			</view>
			<text class="pd-tariff-note">费率由物业 PC 后台统一设置 · 小程序端实时同步显示</text>
		</view>

		<!-- 离线提示 -->
		<view class="pd-offline-tip" v-if="fromScan && pile.online === false">
			<text style="font-size:22rpx;line-height:1.6;">该充电桩<b>已离线</b> · 暂不可充电，请更换其他充电桩或稍后再试。</text>
		</view>

		<!-- 选择插座（电瓶车） -->
		<view class="c-card pd-mx pd-mt" v-if="chgType === 'bike'">
			<view class="pd-sec-head">
				<text class="c-title">选择插座</text>
				<text class="c-text-sub">{{ slotTip }}</text>
			</view>
			<view class="pd-slot-grid">
				<view v-for="(s, k) in pile.slots || []" :key="k" class="c-slot-btn" :class="{ busy: s.busy, active: curSlot === k }"
					@click="pickSlot(k)">
					<text class="c-slot-num">{{ s.n }}</text>
					<text class="c-slot-sub" :class="s.busy ? '' : ''" style="color:#10b981;">{{ s.busy ? '使用中' : '空闲' }}</text>
				</view>
			</view>
			<view class="pd-slot-state">
				<text class="c-text-sub">插座状态</text>
				<text class="c-text-bold" style="font-size:22rpx;font-weight:600;color:#334155;">{{ slotState }}</text>
			</view>
		</view>

		<!-- 充电配置：电瓶车按时长 / 电动车按金额 -->
		<view class="c-card pd-mx pd-mt" v-if="!readonlyMode">
			<view class="pd-sec-head">
				<text class="c-title">{{ chgType === 'car' ? '充电金额' : '充电时长' }}</text>
				<text class="c-text-sub">{{ chgType === 'car' ? '按金额充电 · 剩余退还' : '充满自停更省心' }}</text>
			</view>
			<!-- 电瓶车：时长选择 -->
			<view class="pd-chips" v-if="chgType === 'bike'">
				<view v-for="(h, i) in durOptions" :key="i" class="c-chip" :class="{ active: dur === h }" @click="setDur(h)">
					{{ h === 0 ? '充满自停' : h + ' 小时' }}
				</view>
			</view>
			<!-- 电动车：金额选择 -->
			<view v-else>
				<view class="pd-chips">
					<view v-for="(a, i) in amtOptions" :key="i" class="c-chip" :class="{ active: amtActive === a }" @click="setAmt(a)">
						{{ a === 0 ? '其他金额' : '¥' + a }}
					</view>
				</view>
				<view class="pd-amt-custom" v-if="amtCustomShow">
					<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#334155;">¥</text>
					<input v-model="amtInput" type="digit" placeholder="输入 1~500 元" style="flex:1;font-size:24rpx;font-weight:600;color:#0f172a;" />
					<view class="pd-amt-confirm" @click="confirmAmt">确认</view>
				</view>
			</view>
		</view>

		<!-- 预扣金额（电瓶车） -->
		<view class="c-card pd-mx pd-mt" v-if="chgType === 'bike' && fromScan && pile.online !== false">
			<view class="pd-sec-head">
				<text class="c-title">预扣金额</text>
				<text class="c-text-sub">按 220W 最高功率 · {{ dur > 0 ? dur + ' 小时' : '充满自停按 8 小时' }}估算</text>
			</view>
			<text class="c-price-lg">¥{{ deposit.toFixed(2) }}</text>
			<text class="c-text-sub" style="display:block;margin-top:12rpx;">启动后预扣 · 结束后按实际结算 · 剩余自动退还</text>
		</view>

		<!-- 支付方式 -->
		<view class="c-card pd-mx pd-mt" v-if="!readonlyMode">
			<text class="c-title">支付方式</text>
			<view class="pd-pay-row" :class="{ checked: payWay === 'balance' }" @click="payWay = 'balance'">
				<view class="pd-pay-radio" :class="{ on: payWay === 'balance' }"></view>
				<text class="pd-pay-ic" style="color:#10b981;">💳</text>
				<text class="c-text-bold" style="flex:1;font-size:24rpx;font-weight:600;color:#334155;">余额</text>
				<text class="c-text-sub">余额 ¥{{ balance.toFixed(2) }}</text>
				<view class="pd-recharge-btn" @click.stop="goWallet">充值</view>
			</view>
			<view class="pd-pay-row" :class="{ checked: payWay === 'card' }" v-if="chgType === 'bike'" @click="payWay = 'card'">
				<view class="pd-pay-radio" :class="{ on: payWay === 'card' }"></view>
				<text class="pd-pay-ic" style="color:#f59e0b;">📇</text>
				<text class="c-text-bold" style="flex:1;font-size:24rpx;font-weight:600;color:#334155;">充电月卡</text>
				<text class="c-text-sub">{{ cardTip }}</text>
				<view class="pd-card-btn" @click.stop="goMonth">{{ hasCard ? '续费' : '去购买' }}</view>
			</view>
		</view>

		<!-- 立即充电 -->
		<view class="pd-mx pd-mt" v-if="!readonlyMode" style="padding-bottom:60rpx;">
			<view class="c-btn-primary" @click="startCharge">立即充电</view>
			<text class="c-text-sub pd-start-tip" v-if="chgType === 'bike'">请您选择充电插座号和充电时间后点击「立即充电」，启动后按设置最大功率预扣金额 · 剩余自动退还</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		chgTypes,
		bikePriceText,
		calcBikeDeposit,
		durOptions,
		amtOptions,
		defaultTariffs,
		tariffAt
	} from '@/api/mock/chargeMockData.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'

	export default {
		data() {
			return {
				chgTypes,
				durOptions,
				amtOptions,
				chgType: 'bike',
				pile: {},
				fromScan: false,
				curSlot: null,
				curGun: null,
				dur: 0,
				chgAmt: 20,
				customAmt: null,
				amtInput: '',
				amtCustomShow: false,
				payWay: 'balance',
				tariffMore: false,
				tariffs: JSON.parse(JSON.stringify(defaultTariffs)),
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			cfg() {
				return this.chgTypes[this.chgType]
			},
			guns() {
				return this.pile.guns || []
			},
			gunTip() {
				if (!this.guns.length) return ''
				const free = this.guns.filter(g => !g.busy).length
				return this.fromScan ? (free + ' 枪空闲') : (free + ' 枪空闲 · ' + (this.guns.length - free) + ' 枪使用中')
			},
			slotTip() {
				const slots = this.pile.slots || []
				const free = slots.filter(s => !s.busy).length
				return this.fromScan ? (free + ' 个空闲 · 点击选择') : (free + ' 个空闲 · ' + (slots.length - free) + ' 个使用中')
			},
			slotState() {
				if (!this.fromScan) return '仅查看状态'
				const slots = this.pile.slots || []
				const firstFree = slots.findIndex(s => !s.busy)
				return this.curSlot !== null ? '已选择 ' + this.pile.slots[this.curSlot].n + ' 号插座' : (firstFree >= 0 ? '请点击选择空闲插座' : '插座已满 · 请更换充电桩')
			},
			deposit() {
				return calcBikeDeposit(this.dur)
			},
			balance() {
				return chargeStore.balance
			},
			myCard() {
				return chargeStore.myCard
			},
			hasCard() {
				return !!(this.myCard && this.myCard.left > 0)
			},
			cardTip() {
				return this.myCard ? (this.myCard.name + ' · 剩余 ' + (this.myCard.type === 'month' ? this.myCard.left + ' 天' : this.myCard
					.left + ' 次')) : '未开通 · 去购买'
			},
			readonlyMode() {
				return !this.fromScan || this.pile.online === false
			},
			curTariff() {
				return tariffAt(new Date().getHours(), this.tariffs)
			},
			otherTariffs() {
				return this.tariffs.filter(t => t !== this.curTariff)
			},
			amtActive() {
				return this.customAmt ? 0 : this.chgAmt
			}
		},
		onLoad(options) {
			this.chgType = options.chgType || chargeStore.chgType || 'bike'
			chargeStore.setChgType(this.chgType)
			this.fromScan = options.fromScan == 1
			const machineId = options.machineId || ''
			const pileIdx = parseInt(options.pileIdx || 0)
			const piles = chargeStore.curPiles()
			let pile = null
			// 优先按 machineId 查找（首页/扫码进入）
			if (machineId) {
				pile = piles.find(p => p.machineId === machineId) || null
			}
			// 未命中则按原 pileIdx 兜底
			if (!pile && piles.length) {
				pile = piles[pileIdx] || piles[0] || null
			}
			if (!pile) {
				this._toast('充电桩不存在')
				setTimeout(() => uni.navigateBack(), 1200)
				return
			}
			this.pile = pile
			this._initDefault()
		},
		methods: {
			bikePriceText,
			_initDefault() {
				if (this.chgType === 'car') {
					this.curGun = null
					const guns = this.pile.guns || []
					const firstFree = guns.findIndex(g => !g.busy)
					if (this.fromScan && this.pile.online !== false && firstFree >= 0) this.curGun = firstFree
				} else {
					this.curSlot = null
				}
				// 电瓶车扫码充电：已开通月卡默认选中月卡支付
				if (this.chgType === 'bike' && this.hasCard) {
					this.payWay = 'card'
				} else {
					this.payWay = 'balance'
				}
			},
			pickSlot(k) {
				if (!this.fromScan || this.pile.online === false) return
				if (this.pile.slots[k].busy) return
				this.curSlot = k
			},
			pickGun(k) {
				if (!this.fromScan || this.pile.online === false) return
				if (this.pile.guns[k].busy) return
				this.curGun = k
			},
			setDur(h) {
				if (this.readonlyMode) return
				this.dur = h
			},
			setAmt(a) {
				if (this.readonlyMode) return
				if (a > 0) {
					this.customAmt = null
					this.chgAmt = a
					this.amtCustomShow = false
				} else {
					this.amtCustomShow = true
					this.amtInput = ''
				}
			},
			confirmAmt() {
				const v = parseFloat(this.amtInput)
				if (!v || v < 1 || v > 500) {
					this._toast('请输入 1~500 之间的金额')
					return
				}
				this.customAmt = Math.round(v * 100) / 100
				this.chgAmt = this.customAmt
				this.amtCustomShow = false
				this._toast('已选择自定义金额 ¥' + this.chgAmt.toFixed(2))
			},
			goWallet() {
				uni.navigateTo({ url: '/packageCharge/pages/charge/wallet' })
			},
			goMonth() {
				chargeStore.planFilter = null
				uni.navigateTo({ url: '/packageCharge/pages/charge/chargeMonth' })
			},
			startCharge() {
				if (!hasLogin()) {
					this._toast('请先登录')
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				if (this.pile.online === false) {
					this._toast('该充电桩已离线 · 暂不可充电')
					return
				}
				if (this.chgType === 'bike' && this.curSlot === null) {
					this._toast('请先选择充电插座')
					return
				}
				if (this.chgType === 'car' && this.payWay === 'card') {
					this._toast('月卡仅限电瓶车 · 请使用余额支付')
					this.payWay = 'balance'
					return
				}
				if (this.payWay === 'card' && !this.myCard) {
					this._toast('暂未开通充电月卡 · 请先购买')
					uni.navigateTo({ url: '/packageCharge/pages/charge/chargeMonth' })
					return
				}
				if (this.payWay === 'card' && this.myCard.left <= 0) {
					this._toast('月卡次数已用完 · 请续费')
					return
				}
				const deposit = this.chgType === 'car' ? this.chgAmt : this.deposit
				if (this.payWay === 'balance' && chargeStore.balance < deposit) {
					this._toast('余额不足 ¥' + deposit.toFixed(2) + ' · 请先充值')
					uni.navigateTo({ url: '/packageCharge/pages/charge/wallet' })
					return
				}
				// 组装启动参数 → 进入充电中页
				const params = {
					chgType: this.chgType,
					pileIdx: chargeStore.curPiles().indexOf(this.pile),
					slot: this.chgType === 'bike' && this.curSlot !== null ? this.pile.slots[this.curSlot].n : '',
					gun: this.chgType === 'car' && this.curGun !== null ? this.pile.guns[this.curGun].n : '',
					dur: this.chgType === 'bike' ? this.dur : 0,
					amt: this.chgType === 'car' ? this.chgAmt : 0,
					payWay: this.payWay,
					deposit: deposit,
					fromScan: 1
				}
				uni.navigateTo({
					url: '/packageCharge/pages/charge/charging?params=' + encodeURIComponent(JSON.stringify(params))
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

	.pd-pile-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx;
	}

	.pd-pile-icon {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		background: rgba(16, 185, 129, .1);
		color: #059669;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 40rpx;
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

	.pd-pile-loc {
		margin-top: 6rpx;
		display: block;
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
		margin: 0 28rpx 28rpx;
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

	.pd-slot-grid {
		display: grid;
		grid-template-columns: repeat(5, 1fr);
		gap: 14rpx;
		padding: 0 28rpx;
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

	.pd-chips {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		padding: 0 28rpx 28rpx;
	}

	.pd-amt-custom {
		display: flex;
		align-items: center;
		gap: 14rpx;
		margin: 0 28rpx 28rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 18rpx 24rpx;
	}

	.pd-amt-confirm {
		font-size: 22rpx;
		font-weight: 700;
		color: #fff;
		background: #10b981;
		border-radius: 999rpx;
		padding: 10rpx 28rpx;
	}

	.pd-pay-row {
		display: flex;
		align-items: center;
		gap: 18rpx;
		margin: 0 28rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 20rpx;
		padding: 22rpx 24rpx;
		margin-bottom: 20rpx;
	}

	.pd-pay-row.checked {
		border-color: #10b981;
		background: rgba(16, 185, 129, .05);
	}

	.pd-pay-radio {
		width: 32rpx;
		height: 32rpx;
		border-radius: 50%;
		border: 2rpx solid #cbd5e1;
	}

	.pd-pay-radio.on {
		border-color: #10b981;
		background: #10b981;
		box-shadow: inset 0 0 0 6rpx #fff;
	}

	.pd-pay-ic {
		font-size: 36rpx;
	}

	.pd-recharge-btn {
		font-size: 20rpx;
		font-weight: 700;
		color: #fff;
		background: #10b981;
		border-radius: 999rpx;
		padding: 10rpx 26rpx;
	}

	.pd-card-btn {
		font-size: 20rpx;
		font-weight: 700;
		color: #f97316;
		background: rgba(249, 115, 22, .1);
		border: 1rpx solid rgba(249, 115, 22, .3);
		border-radius: 999rpx;
		padding: 10rpx 26rpx;
	}

	.pd-start-tip {
		display: block;
		text-align: center;
		margin-top: 16rpx;
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
		padding: 24rpx;
	}

	.pd-tc-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
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
	}

	.pd-tc-rate-sub {
		font-size: 16rpx;
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

	.pd-tariff-note {
		display: block;
		font-size: 16rpx;
		color: #cbd5e1;
		margin: 20rpx 28rpx 28rpx;
	}

	.pd-offline-tip {
		margin: 24rpx 28rpx 0;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		border: 1rpx solid rgba(239, 68, 68, .15);
		padding: 20rpx 24rpx;
		color: #ef4444;
	}
</style>
