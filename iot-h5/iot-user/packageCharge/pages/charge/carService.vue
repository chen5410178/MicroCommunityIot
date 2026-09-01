<template>
	<view class="cs-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="cs-head">
			<text class="c-title">车辆服务 <text class="c-text-sub" style="font-weight:400;">共 {{ vehicles.length }} 辆</text></text>
		</view>

		<!-- 车牌搜索（两框输入） -->
		<view class="cs-mx">
			<view class="cs-search-row">
				<view class="cs-prov-box" @click="openProvPanel">
					<input :value="searchProv" readonly placeholder="省" class="cs-prov-input" />
				</view>
				<view class="cs-rest-box">
					<text class="cs-search-ic">🔍</text>
					<input :value="searchRest" readonly placeholder="输入字母数字" class="cs-rest-input" @click="openSearchKey" />
					<text class="cs-suggest-toggle" @click="toggleSearchSuggest">▾</text>
				</view>
			</view>
			<view class="cs-search-btn" @click="searchCar">搜索</view>

			<!-- 省份选择面板 -->
			<view class="cs-prov-panel" v-if="provPanel">
				<text class="cs-prov-panel-label">请选择省份汉字（车牌第一位）</text>
				<view class="cs-prov-grid">
					<view v-for="(c, i) in KB_PROVINCES" :key="i" class="cs-prov-cell" @click="pickProv(c)">{{ c }}</view>
				</view>
			</view>

			<!-- 历史车牌下拉 -->
			<view class="cs-suggest" v-if="suggestShow">
				<view v-for="(p, i) in suggestList" :key="i" class="cs-suggest-item">
					<view class="cs-suggest-main" @click="pickSearchPlate(p)">
						<text style="font-size:18rpx;color:#94a3b8;">🚗</text>
						<text class="cs-suggest-plate">{{ p }}</text>
						<text class="cs-suggest-tag">历史</text>
					</view>
					<text class="cs-suggest-del" @click="delHistory(p)">✕</text>
				</view>
			</view>
		</view>

		<!-- 搜索结果 -->
		<view class="cs-mx cs-mt" v-if="searchResult">
			<view v-if="hitCar" class="c-card cs-hit">
				<view class="cs-hit-head">
					<view class="cs-hit-ic">🚗</view>
					<view style="flex:1;min-width:0;">
						<view class="cs-hit-plate-row">
							<text class="cs-hit-plate">{{ hitCar.plate }}</text>
							<text class="cs-hit-tag" :class="hitCar.expire ? 'on' : 'off'">{{ hitCar.expire ? '月租生效中' : '未开通月租' }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:8rpx;display:block;">{{ hitCar.model }} · {{ hitCar.comm }}{{ hitCar.expire ? ' · 有效期至 ' + hitCar.expire : '' }}</text>
					</view>
				</view>
				<view class="cs-hit-btn" @click="goDetailByPlate(searchKw)">进入月卡续费</view>
			</view>
			<view v-else class="cs-unbound">
				<text class="cs-unbound-title">车牌「{{ searchKw }}」未绑定车辆</text>
				<view class="cs-unbound-btn" @click="goDetailByPlate(searchKw)">月卡续费 · {{ isNewEnergyPlate ? '新能源' : '燃油' }}</view>
			</view>
		</view>

		<view class="cs-tip cs-mx cs-mt">
			<text style="font-size:20rpx;line-height:1.6;color:#3b82f6;">输入车牌号即可查询月卡并直接续费</text>
		</view>

		<!-- 月租续费订单 -->
		<view class="cs-order-head">
			<text class="c-title">月租续费订单</text>
			<text class="c-text-sub">共 {{ rentOrders.length }} 笔</text>
		</view>
		<view class="cs-orders cs-mx">
			<view class="c-card cs-order" v-for="(o, i) in rentOrders" :key="i" @click="openRentOrder(i)">
				<view class="cs-order-head-row">
					<text class="c-text-bold" style="font-size:24rpx;font-weight:700;color:#0f172a;">{{ o.plate }} · {{ o.plan }}</text>
					<view style="display:flex;align-items:center;gap:8rpx;">
						<text style="font-size:26rpx;font-weight:700;color:#3b82f6;">¥{{ o.fee }}</text>
						<text style="color:#cbd5e1;font-size:22rpx;">›</text>
					</view>
				</view>
				<text class="c-text-sub" style="margin-top:10rpx;display:block;">有效期 {{ o.from }} → {{ o.to }} · {{ o.time }} 支付</text>
				<view class="cs-order-foot">
					<text class="cs-order-st">{{ o.st }}</text>
					<text class="c-text-sub" style="font-size:18rpx;">{{ o.months }} 个月</text>
				</view>
			</view>
			<view v-if="!rentOrders.length" class="c-card cs-order-empty">
				<text class="c-text-sub">暂无续费订单，续费后这里将展示记录</text>
			</view>
		</view>

		<!-- 车牌键盘 -->
		<plate-keyboard :show.sync="keyShow" :split-mode="true" @change="onKeyChange" />

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import plateKeyboard from '@/packageCharge/components/charge/plate-keyboard.vue'
	import {
		KB_PROVINCES,
		getPlateHistory,
		savePlateHistory,
		delPlateHistory,
		isPlateValid,
		isNewEnergy
	} from '@/packageCharge/api/mock/carPlateData.js'

	export default {
		components: {
			plateKeyboard
		},
		data() {
			return {
				KB_PROVINCES,
				searchProv: '',
				searchRest: '',
				provPanel: false,
				suggestShow: false,
				keyShow: false,
				searchKw: '',
				searchResult: false,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			vehicles() {
				return chargeStore.vehicles
			},
			rentOrders() {
				return chargeStore.rentOrders
			},
			hitCar() {
				if (!this.searchKw) return null
				const bare = this.searchKw.replace(/·/g, '')
				return this.vehicles.find(c => c.plate.replace(/·/g, '') === bare) || null
			},
			isNewEnergyPlate() {
				return isNewEnergy(this.searchKw)
			},
			suggestList() {
				return getPlateHistory().slice(0, 6)
			}
		},
		onLoad() {
			// 从车辆详情返回时刷新
		},
		onShow() {
			// 返回时保留搜索状态
		},
		methods: {
			openProvPanel() {
				this.suggestShow = false
				this.keyShow = false
				this.provPanel = !this.provPanel
			},
			pickProv(c) {
				this.searchProv = c
				this.provPanel = false
				this.keyShow = true
				this._keyChange()
			},
			openSearchKey() {
				this.provPanel = false
				this.suggestShow = false
				this.keyShow = true
			},
			onKeyChange(e) {
				this.searchProv = e.prov
				this.searchRest = e.rest
				this._keyChange()
			},
			_keyChange() {
				const kw = (this.searchProv + this.searchRest).replace(/·/g, '')
				if (kw) this.renderSuggest(kw)
			},
			renderSuggest(kw) {
				const hist = getPlateHistory().filter(p => p.indexOf(kw) > -1 || kw.indexOf(p) > -1)
				this.suggestShow = hist.length > 0 && !this.keyShow
			},
			toggleSearchSuggest() {
				if (this.suggestShow) {
					this.suggestShow = false
				} else {
					this.provPanel = false
					this.suggestShow = true
				}
			},
			pickSearchPlate(p) {
				const bare = p.replace(/·/g, '')
				this.searchProv = bare.charAt(0)
				this.searchRest = bare.slice(1)
				this.suggestShow = false
				this.searchKw = bare
				this.searchResult = true
				savePlateHistory(p)
			},
			delHistory(p) {
				delPlateHistory(p)
				this._toast('已删除历史车牌 ' + p)
				this.suggestShow = false
			},
			searchCar() {
				this.keyShow = false
				this.provPanel = false
				this.suggestShow = false
				const kw = (this.searchProv + this.searchRest).replace(/·/g, '')
				if (!kw) {
					this._toast('请输入车牌号码')
					return
				}
				if (!this.searchProv) {
					this._toast('请先选择省份')
					return
				}
				this.searchKw = kw
				this.searchResult = true
				savePlateHistory(kw)
			},
			goDetailByPlate(bare) {
				uni.navigateTo({
					url: '/packageCharge/pages/charge/carDetail?plate=' + encodeURIComponent(bare)
				})
			},
			openRentOrder(i) {
				uni.navigateTo({
					url: '/packageCharge/pages/charge/rentOrderDetail?idx=' + i
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
	.cs-page {
		padding-bottom: 60rpx;
	}

	.cs-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.cs-mt {
		margin-top: 20rpx;
	}

	.cs-head {
		margin: 28rpx 28rpx 0;
	}

	.cs-search-row {
		display: flex;
		gap: 16rpx;
	}

	.cs-prov-box {
		width: 130rpx;
		flex-shrink: 0;
		display: flex;
		align-items: center;
		justify-content: center;
		border: 1rpx solid #e2e8f0;
		background: #f8fafc;
		border-radius: 20rpx;
	}

	.cs-prov-input {
		width: 100%;
		text-align: center;
		font-size: 26rpx;
		font-weight: 700;
		color: #0f172a;
		letter-spacing: 4rpx;
		height: 88rpx;
	}

	.cs-rest-box {
		flex: 1;
		display: flex;
		align-items: center;
		gap: 12rpx;
		border: 1rpx solid #e2e8f0;
		background: #f8fafc;
		border-radius: 20rpx;
		padding: 0 22rpx;
		height: 88rpx;
	}

	.cs-search-ic {
		font-size: 26rpx;
	}

	.cs-rest-input {
		flex: 1;
		font-size: 24rpx;
		font-weight: 700;
		color: #0f172a;
		letter-spacing: 4rpx;
	}

	.cs-suggest-toggle {
		color: #94a3b8;
		font-size: 22rpx;
		padding: 8rpx;
	}

	.cs-search-btn {
		margin-top: 20rpx;
		border-radius: 20rpx;
		background: #3b82f6;
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		box-shadow: 0 8rpx 20rpx rgba(59, 130, 246, .25);
	}

	.cs-prov-panel {
		margin-top: 20rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 20rpx;
		padding: 20rpx;
		box-shadow: 0 10rpx 30rpx rgba(15, 23, 42, .08);
	}

	.cs-prov-panel-label {
		display: block;
		font-size: 18rpx;
		color: #94a3b8;
		margin-bottom: 12rpx;
	}

	.cs-prov-grid {
		display: grid;
		grid-template-columns: repeat(9, 1fr);
		gap: 10rpx;
	}

	.cs-prov-cell {
		height: 64rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 14rpx;
		background: #f8fafc;
		font-size: 24rpx;
		font-weight: 700;
		color: #1e293b;
		border: 1rpx solid #f1f5f9;
	}

	.cs-prov-cell:active {
		background: #eff6ff;
	}

	.cs-suggest {
		margin-top: 16rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 20rpx;
		overflow: hidden;
		box-shadow: 0 10rpx 30rpx rgba(15, 23, 42, .1);
	}

	.cs-suggest-item {
		display: flex;
		align-items: center;
		padding: 20rpx 24rpx;
		border-bottom: 1rpx solid #f8fafc;
	}

	.cs-suggest-item:last-child {
		border-bottom: none;
	}

	.cs-suggest-main {
		flex: 1;
		display: flex;
		align-items: center;
		gap: 14rpx;
		min-width: 0;
	}

	.cs-suggest-plate {
		flex: 1;
		font-size: 24rpx;
		font-weight: 700;
		color: #334155;
		letter-spacing: 4rpx;
	}

	.cs-suggest-tag {
		font-size: 18rpx;
		color: #cbd5e1;
	}

	.cs-suggest-del {
		color: #cbd5e1;
		font-size: 20rpx;
		padding: 10rpx;
	}

	.cs-hit {
		padding: 26rpx;
	}

	.cs-hit-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.cs-hit-ic {
		width: 80rpx;
		height: 80rpx;
		border-radius: 24rpx;
		background: rgba(59, 130, 246, .1);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 36rpx;
	}

	.cs-hit-plate-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
		flex-wrap: wrap;
	}

	.cs-hit-plate {
		background: #1d4ed8;
		color: #fff;
		font-size: 20rpx;
		font-weight: 700;
		border-radius: 10rpx;
		padding: 6rpx 14rpx;
		letter-spacing: 3rpx;
	}

	.cs-hit-tag {
		font-size: 16rpx;
		font-weight: 600;
		border-radius: 999rpx;
		padding: 4rpx 12rpx;
	}

	.cs-hit-tag.on {
		background: rgba(249, 115, 22, .1);
		color: #d97706;
		border: 1rpx solid rgba(249, 115, 22, .25);
	}

	.cs-hit-tag.off {
		background: #f1f5f9;
		color: #94a3b8;
	}

	.cs-hit-btn {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: linear-gradient(135deg, #3b82f6, #6366f1);
		color: #fff;
		font-size: 22rpx;
		font-weight: 700;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
	}

	.cs-unbound {
		border: 2rpx dashed rgba(249, 115, 22, .3);
		background: rgba(249, 115, 22, .05);
		border-radius: 24rpx;
		padding: 40rpx 24rpx;
		text-align: center;
	}

	.cs-unbound-title {
		font-size: 24rpx;
		font-weight: 700;
		color: #334155;
	}

	.cs-unbound-btn {
		display: inline-block;
		margin-top: 24rpx;
		border-radius: 999rpx;
		background: linear-gradient(135deg, #3b82f6, #6366f1);
		color: #fff;
		font-size: 22rpx;
		font-weight: 700;
		padding: 16rpx 36rpx;
	}

	.cs-tip {
		border-radius: 20rpx;
		background: rgba(59, 130, 246, .08);
		border: 1rpx solid rgba(59, 130, 246, .15);
		padding: 20rpx 24rpx;
	}

	.cs-order-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 36rpx 28rpx 16rpx;
	}

	.cs-orders {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.cs-order {
		padding: 26rpx;
	}

	.cs-order-head-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.cs-order-foot {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 18rpx;
		padding-top: 14rpx;
		border-top: 1rpx solid #f8fafc;
	}

	.cs-order-st {
		font-size: 18rpx;
		color: #059669;
		background: rgba(16, 185, 129, .08);
		border-radius: 999rpx;
		padding: 4rpx 16rpx;
	}

	.cs-order-empty {
		padding: 40rpx 0;
		text-align: center;
	}
</style>
