<template>
	<view class="cs-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="cs-head">
			<text class="c-title">车辆服务 <text class="c-text-sub" style="font-weight:400;" v-if="!loading">共 {{ rentOrders.length }} 笔</text><text class="c-text-sub" style="font-weight:400;" v-else>加载中...</text></text>
		</view>

		<!-- 车牌搜索（两框输入） -->
		<view class="cs-mx">
			<view class="cs-search-row">
				<view class="cs-prov-box" @click="openProvPanel">
					<input :value="searchProv" readonly placeholder="省" class="cs-prov-input" />
				</view>
				<view class="cs-rest-box">
					<image :src="imgUrl + '/h5/images/common/ic-search.png'" class="cs-search-ic" mode="aspectFit"></image>
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
						<image :src="imgUrl + '/h5/images/charge/ic-car-full.png'" class="cs-suggest-ic" mode="aspectFit"></image>
						<text class="cs-suggest-plate">{{ p }}</text>
						<text class="cs-suggest-tag">历史</text>
					</view>
					<text class="cs-suggest-del" @click="delHistory(p)">✕</text>
				</view>
			</view>
		</view>

		<!-- 搜索结果：仅展示车辆信息 -->
		<view class="cs-mx cs-mt" v-if="hitCar">
			<view class="c-card cs-hit">
				<view class="cs-hit-head">
					<view class="cs-hit-ic">
						<image :src="imgUrl + '/h5/images/charge/ic-car-full.png'" class="cs-hit-ic-img" mode="aspectFit"></image>
					</view>
					<view style="flex:1;min-width:0;">
						<view class="cs-hit-plate-row">
							<text class="cs-hit-plate">{{ hitCar.plate }}</text>
							<text class="cs-hit-tag" :class="hitCar.expire ? 'on' : 'off'">{{ hitCar.expire ? '月卡生效中 · 剩余 ' + hitCar.expireTip + ' 天' : '未开通月卡' }}</text>
						</view>
						<text class="c-text-sub" style="margin-top:8rpx;display:block;">{{ hitCar.model }} · {{ hitCar.comm }}{{ hitCar.expire ? ' · 有效期至 ' + hitCar.expire : '' }}</text>
					</view>
				</view>
				<view class="cs-hit-btn" @click="buyMonthCard">购买月卡</view>
			</view>
		</view>

		<view class="cs-tip cs-mx cs-mt">
			<text style="font-size:26rpx;line-height:1.6;color:#3b82f6;">输入车牌号即可查询月卡并直接购买</text>
		</view>

		<!-- 月卡订单 -->
		<view class="cs-order-head">
			<text class="c-title">月卡订单</text>
			<text class="c-text-sub" v-if="!loading">共 {{ rentOrders.length }} 笔</text>
			<text class="c-text-sub" v-else>加载中...</text>
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
				<text class="c-text-sub" style="margin-top:10rpx;display:block;">有效期 {{ o.from }} → {{ o.to }}{{ o.months ? '（' + o.months + ' 个月）' : '' }}</text>
				<view class="cs-order-foot">
					<text class="cs-order-st">{{ o.st }}</text>
					<text class="c-text-sub" style="font-size:18rpx;">购买时间 {{ o.time }}</text>
				</view>
			</view>
			<view v-if="!rentOrders.length" class="c-card cs-order-empty">
				<text class="c-text-sub cs-empty-text">暂无月卡订单，购买月卡后这里将展示记录</text>
			</view>
		</view>

		<!-- 车牌键盘 -->
		<plate-keyboard ref="plateKey" :show.sync="keyShow" :split-mode="true" @change="onKeyChange" />

		<!-- 添加车辆（车辆由物业前台录入） -->
		<view class="cs-add-bar" @click="addCar">
			<text class="cs-add-txt">＋ 添加车辆</text>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import plateKeyboard from '@/packageCharge/components/charge/plate-keyboard.vue'
	import {
		queryMonthCardByCarNum,
		listUserCarMonthOrder
	} from '@/packageCharge/api/chargeApi.js'
	import {
		ensureCommunityId
	} from '@/api/community/communityApi.js'
	import {
		getUserId
	} from '@/api/user/userApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import {
		KB_PROVINCES,
		getPlateHistory,
		savePlateHistory,
		delPlateHistory,
		isPlateValid,
		isNewEnergy
	} from '@/packageCharge/api/mock/carPlateData.js'
	import {
		saveTargetUrl,
		buildCurrentPageUrl
	} from '@/lib/java110/utils/TargetUrlUtil.js'

	export default {
		components: {
			plateKeyboard
		},
		data() {
			return {
				KB_PROVINCES,
				themeDark: false,
				searchProv: '',
				searchRest: '',
				provPanel: false,
				suggestShow: false,
				keyShow: false,
				hitCar: null,
				suggestList: [],
				rentOrders: [],
				communityId: '',
				loading: false,
				toastMsg: ''
			}
		},
		onShow() {
			// 每次显示刷新（支付返回后联动车辆/订单）
			if (!hasLogin()) {
				saveTargetUrl(buildCurrentPageUrl())
				uni.navigateTo({ url: '/pages/user/login' })
				return
			}
			this.themeDark = chargeStore.themeDark
			this._loadData()
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
				this.$refs.plateKey.open(c, this.searchRest)
				this._keyChange()
			},
			openSearchKey() {
				this.provPanel = false
				this.suggestShow = false
				this.$refs.plateKey.open(this.searchProv, this.searchRest)
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
				this.suggestList = hist.slice(0, 6)
				this.suggestShow = hist.length > 0 && !this.keyShow
			},
			toggleSearchSuggest() {
				if (this.suggestShow) {
					this.suggestShow = false
				} else {
					this.provPanel = false
					this.suggestList = getPlateHistory().slice(0, 6)
					this.suggestShow = true
				}
			},
			pickSearchPlate(p) {
				const bare = p.replace(/·/g, '')
				this.searchProv = bare.charAt(0)
				this.searchRest = bare.slice(1)
				this.suggestShow = false
				this.searchCar()
			},
			delHistory(p) {
				delPlateHistory(p)
				this._toast('已删除历史车牌 ' + p)
				this.suggestList = getPlateHistory().slice(0, 6)
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
				if (!isPlateValid(kw)) {
					this._toast('车牌号码格式不正确')
					return
				}
				this.hitCar = null
				this._ensureCommunity().then(communityId => {
					if (!communityId) return
					return queryMonthCardByCarNum({
						carNum: kw,
						communityId: communityId
					}).then(data => {
						this.hitCar = this._adapterHitCar(kw, data)
						savePlateHistory(kw)
					}).catch(e => {
						const msg = (typeof e === 'string' ? e : (e && e.message)) || '查询月卡失败'
						this._toast(msg + '，请到物业前台申请车位绑定车辆')
					})
				})
			},
			buyMonthCard() {
				if (!this.hitCar) return
				uni.navigateTo({
					url: '/packageFee/pages/fee/buyCarMonthCard?carNum=' + this.hitCar.plate +
						'&communityId=' + this.communityId +
						'&createUserId=' + getUserId()
				})
			},
			openRentOrder(i) {
				const o = this.rentOrders[i]
				if (!o) return
				uni.navigateTo({
					url: '/packageCharge/pages/charge/rentOrderDetail?orderId=' + (o.orderId || '')
				})
			},
			addCar() {
				this._toast('请到物业前台申请车位绑定车辆')
			},
			_loadData() {
				this.loading = true
				this._ensureCommunity().then(communityId => {
					if (!communityId) {
						this.loading = false
						return
					}
					return listUserCarMonthOrder({
						communityId: communityId
					}).then(list => {
						this.rentOrders = (list || []).map(o => this._adapterOrder(o))
					}).catch(e => {
						this._toast((typeof e === 'string' ? e : (e && e.message)) || '订单加载失败')
					}).then(() => {
						this.loading = false
					})
				})
			},
			_ensureCommunity() {
				return ensureCommunityId().then(communityId => {
					if (!communityId) {
						this._toast('暂无可用小区，请稍后再试')
						return ''
					}
					this.communityId = communityId
					return communityId
				})
			},
			_adapterHitCar(carNum, data) {
				const expire = this._expireDate(data && data.endTime)
				const isNew = isNewEnergy(carNum)
				return {
					carId: (data && data.carId) || '',
					plate: carNum,
					model: (data && data.carTypeName) || (isNew ? '新能源小型汽车' : '燃油小型汽车'),
					comm: this._commName(),
					expire: expire,
					expireTip: expire ? this._leftDays(expire) : 0
				}
			},
			_adapterOrder(o) {
				return {
					orderId: o.orderId || '',
					plate: o.carNum || '',
					plan: o.cardName || '',
					months: this._monthSpan(o.startTime, o.endTime),
					fee: o.receivedAmount || o.receivableAmount || 0,
					from: this._toDate(o.startTime),
					to: this._toDate(o.endTime),
					time: this._toDateTime(o.createTime),
					st: o.stateName || (o.state === '1001' ? '正常' : (o.state === '2002' ? '已退费' : '—'))
				}
			},
			_expireDate(v) {
				const d = this._toDate(v)
				if (!d) return ''
				const end = new Date(d.replace(/-/g, '/')).getTime()
				if (isNaN(end) || end <= Date.now()) return ''
				return d
			},
			_leftDays(dateStr) {
				const end = new Date(dateStr.replace(/-/g, '/')).getTime()
				if (isNaN(end)) return 0
				return Math.max(0, Math.ceil((end - Date.now()) / 86400000))
			},
			_monthSpan(from, to) {
				const a = this._toDate(from)
				const b = this._toDate(to)
				if (!a || !b) return ''
				const d1 = new Date(a.replace(/-/g, '/'))
				const d2 = new Date(b.replace(/-/g, '/'))
				if (isNaN(d1.getTime()) || isNaN(d2.getTime())) return ''
				return Math.max(1, (d2.getFullYear() - d1.getFullYear()) * 12 + (d2.getMonth() - d1.getMonth()))
			},
			_toDate(v) {
				if (!v) return ''
				const s = String(v)
				if (s.indexOf('-') >= 0) return s.slice(0, 10)
				const d = new Date(Number(s))
				if (isNaN(d.getTime())) return ''
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2)
			},
			_toDateTime(v) {
				if (!v) return ''
				const s = String(v)
				if (s.indexOf('-') >= 0) return s.slice(0, 16)
				const d = new Date(Number(s))
				if (isNaN(d.getTime())) return ''
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2) +
					' ' + ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2)
			},
			_commName() {
				const c = uni.getStorageSync('currentCommunityInfo')
				return (c && (c.communityName || c.name)) || '无'
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
		padding-bottom: 160rpx;
	}

	.cs-add-bar {
		position: fixed;
		left: 28rpx;
		right: 28rpx;
		bottom: 30rpx;
		height: 88rpx;
		border-radius: 20rpx;
		background: #fff;
		border: 1rpx solid #e2e8f0;
		box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, .08);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cs-add-txt {
		font-size: 26rpx;
		font-weight: 700;
		color: #3b82f6;
	}

	.cs-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.cs-mt {
		margin-top: 28rpx;
	}

	.cs-head {
		margin: 28rpx 28rpx 24rpx;
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
		width: 28rpx;
		height: 28rpx;
		flex-shrink: 0;
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

	.cs-suggest-ic {
		width: 26rpx;
		height: 26rpx;
		flex-shrink: 0;
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
	}

	.cs-hit-ic-img {
		width: 40rpx;
		height: 40rpx;
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

	.cs-tip {
		border-radius: 20rpx;
		background: rgba(59, 130, 246, .08);
		border: 1rpx solid rgba(59, 130, 246, .15);
		padding: 22rpx 26rpx;
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

	.cs-order-empty .cs-empty-text {
		font-size: 26rpx;
		line-height: 1.6;
	}
</style>
