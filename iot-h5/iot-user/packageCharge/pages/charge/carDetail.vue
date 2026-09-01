<template>
	<view class="cd-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 车辆信息 -->
		<view class="c-card cd-mx cd-mt">
			<view class="cd-car-head">
				<view class="cd-car-ic">
					<image src="/static/h5/images/charge/ic-car-full.png" class="cd-car-ic-img" mode="aspectFit"></image>
				</view>
				<view style="flex:1;">
					<text class="c-text-sub">车辆服务</text>
					<text class="cd-plate">{{ car.plate }}</text>
				</view>
				<text class="cd-plate-type" :class="isNewEnergy ? 'new' : 'oil'">{{ isNewEnergy ? '新能源' : '燃油' }}</text>
			</view>
			<view class="cd-car-grid">
				<view class="cd-car-cell">
					<text class="c-text-sub">车辆类型</text>
					<text class="cd-car-val">{{ car.model }}</text>
				</view>
				<view class="cd-car-cell">
					<text class="c-text-sub">绑定小区</text>
					<text class="cd-car-val">{{ car.comm }}</text>
				</view>
				<view class="cd-car-cell">
					<text class="c-text-sub">绑定时间</text>
					<text class="cd-car-val">{{ car.bindDate || '未绑定' }}</text>
				</view>
			</view>
		</view>

		<!-- 车辆月租 -->
		<view class="cd-rent-card">
			<view class="cd-rent-head">
				<text class="cd-rent-title">车辆月租</text>
				<text class="cd-rent-state">{{ car.expire ? '生效中' : '未开通' }}</text>
			</view>
			<text class="cd-rent-label">当前有效期至</text>
			<text class="cd-rent-expire">{{ car.expire || '未开通' }}</text>
			<text class="cd-rent-tip">{{ car.expire ? '到期前可随时续费，续费后有效期自动顺延' : '开通月租后，车辆进出小区按月租计费' }}</text>
		</view>

		<!-- 月租套餐 -->
		<view class="cd-plan-head">
			<text class="c-title">月租套餐</text>
			<text class="c-text-sub">支持余额 / 微信支付</text>
		</view>
		<view class="cd-plans cd-mx">
			<view v-for="(p, i) in carPlans" :key="i" class="cd-plan" :class="{ active: planIdx === i }" @click="pickPlan(i)">
				<view style="flex:1;min-width:0;">
					<view class="cd-plan-name-row">
						<text class="c-text-bold" style="font-size:26rpx;font-weight:700;color:#0f172a;">{{ p.name }}</text>
						<text class="cd-plan-tag" v-if="p.tag">{{ p.tag }}</text>
					</view>
					<text class="c-text-sub" style="margin-top:4rpx;display:block;">{{ p.months }} 个月 · 平均 ¥{{ (p.price / p.months).toFixed(0) }}/月</text>
				</view>
				<text class="cd-plan-price">¥{{ p.price }}</text>
				<view class="cd-plan-radio" :class="{ on: planIdx === i }"></view>
			</view>
		</view>

		<view class="cd-mx cd-mt" style="padding-bottom:60rpx;">
			<view class="c-btn-primary" @click="renewCarRent">立即续费</view>
		</view>

		<view class="cd-tip cd-mx">
			<text style="font-size:20rpx;line-height:1.6;color:#3b82f6;">车辆月租为小区固定车位租赁费用，与充电费用相互独立；月租到期后车辆可正常进出小区，但将按临停标准计费。</text>
		</view>

		<!-- 续费确认弹窗 -->
		<view class="c-mask" v-if="rentShow" @click="rentShow = false"></view>
		<view class="c-dialog" v-if="rentShow">
			<view class="cd-dialog-head">
				<view class="cd-dialog-ic">
					<image src="/static/h5/images/charge/ic-car-full.png" class="cd-dialog-ic-img" mode="aspectFit"></image>
				</view>
				<view>
					<text class="c-title" style="display:block;">月租续费</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">{{ car.plate }} · {{ car.comm }}</text>
				</view>
			</view>
			<view class="cd-rent-box">
				<view class="cd-rent-row"><text class="c-text-sub">续费套餐</text><text class="cd-rent-val">{{ curPlan.name }}（{{ curPlan.months }} 个月）</text></view>
				<view class="cd-rent-row"><text class="c-text-sub">有效期至</text><text class="cd-rent-val">{{ newExpire }}</text></view>
				<view class="cd-rent-row"><text class="c-text-sub">支付方式</text><text class="cd-rent-val">余额支付</text></view>
				<view class="cd-rent-row" style="border-top:1rpx solid #e2e8f0;padding-top:16rpx;">
					<text class="c-text-sub">应付金额</text>
					<text class="cd-rent-fee">¥{{ curPlan.price }}</text>
				</view>
			</view>
			<view class="cd-dialog-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="rentShow = false">取消</view>
				<view class="c-btn-primary" style="flex:1;" @click="confirmRenew">确认续费</view>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		carPlans,
		mockVehicles
	} from '@/api/mock/chargeMockData.js'
	import {
		isNewEnergy
	} from '@/packageCharge/api/mock/carPlateData.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'

	export default {
		data() {
			return {
				carPlans,
				planIdx: 0,
				rentShow: false,
				newExpire: '',
				curCar: null,
				curTemp: false,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			car() {
				return this.curCar || this.curTempCar || {}
			},
			curTempCar() {
				// 未绑定车辆直接续费时的临时车辆
				if (this.curTemp) {
					return {
						plate: this.plate,
						model: this.isNewEnergy ? '新能源小型汽车' : '燃油小型汽车',
						comm: chargeStore.curCommunity().name,
						bindDate: '',
						expire: ''
					}
				}
				return null
			},
			isNewEnergy() {
				return isNewEnergy((this.car.plate || '').replace(/·/g, ''))
			},
			curPlan() {
				return this.carPlans[this.planIdx]
			}
		},
		onLoad(options) {
			this.plate = decodeURIComponent(options.plate || '')
			const bare = this.plate.replace(/·/g, '')
			const idx = chargeStore.vehicles.findIndex(c => c.plate.replace(/·/g, '') === bare)
			if (idx >= 0) {
				this.curCar = chargeStore.vehicles[idx]
				this.curTemp = false
			} else {
				this.curTemp = true
			}
		},
		methods: {
			pickPlan(i) {
				this.planIdx = i
			},
			renewCarRent() {
				if (!hasLogin()) {
					this._toast('请先登录')
					uni.navigateTo({ url: '/pages/user/login' })
					return
				}
				const p = this.curPlan
				this.newExpire = this._addMonths(this.car.expire || this._today(), p.months)
				this.rentShow = true
			},
			confirmRenew() {
				const p = this.curPlan
				const oldExpire = this.car.expire || this._today()
				const newExpire = this._addMonths(oldExpire, p.months)
				if (!this.curTemp) {
					// 已绑定车辆：更新有效期
					this.curCar.expire = newExpire
					chargeStore.saveVehicles()
				} else {
					// 未绑定：直接开通（生成车辆）
					chargeStore.vehicles.push({
						id: Date.now(),
						plate: this.plate,
						model: this.isNewEnergy ? '新能源小型汽车' : '燃油小型汽车',
						comm: chargeStore.curCommunity().name,
						bindDate: this._today(),
						expire: newExpire
					})
					chargeStore.saveVehicles()
					this.curTemp = false
					this.curCar = chargeStore.vehicles[chargeStore.vehicles.length - 1]
				}
				// 追加月租续费订单
				const d = new Date()
				const hm = ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2)
				chargeStore.rentOrders.unshift({
					id: Date.now(),
					plate: this.plate,
					plan: p.name,
					months: p.months,
					fee: p.price,
					from: oldExpire,
					to: newExpire,
					time: this._today() + ' ' + hm,
					st: '已完成'
				})
				chargeStore.saveRentOrders()
				this.rentShow = false
				this._toast('续费成功 · 有效期至 ' + newExpire)
			},
			_addMonths(dateStr, n) {
				const d = new Date(dateStr.replace(/-/g, '/'))
				d.setMonth(d.getMonth() + n)
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2)
			},
			_today() {
				const d = new Date()
				return d.getFullYear() + '-' + ('0' + (d.getMonth() + 1)).slice(-2) + '-' + ('0' + d.getDate()).slice(-2)
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
	.cd-page {
		padding-bottom: 60rpx;
	}

	.cd-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.cd-mt {
		margin-top: 24rpx;
	}

	.cd-car-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 28rpx;
	}

	.cd-car-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		background: rgba(59, 130, 246, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cd-car-ic-img {
		width: 44rpx;
		height: 44rpx;
	}

	.cd-plate {
		display: block;
		font-size: 36rpx;
		font-weight: 700;
		color: #0f172a;
		margin-top: 6rpx;
		letter-spacing: 4rpx;
	}

	.cd-plate-type {
		font-size: 18rpx;
		font-weight: 700;
		color: #fff;
		border-radius: 10rpx;
		padding: 8rpx 16rpx;
		letter-spacing: 2rpx;
	}

	.cd-plate-type.new {
		background: #1d4ed8;
	}

	.cd-plate-type.oil {
		background: #475569;
	}

	.cd-car-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 16rpx;
		padding: 0 28rpx 28rpx;
		border-top: 1rpx solid #f1f5f9;
		padding-top: 24rpx;
	}

	.cd-car-val {
		display: block;
		font-size: 22rpx;
		font-weight: 600;
		color: #1e293b;
		margin-top: 6rpx;
	}

	.cd-rent-card {
		margin: 24rpx 28rpx 0;
		border-radius: 28rpx;
		background: linear-gradient(135deg, #3b82f6, #6366f1);
		padding: 28rpx;
		box-shadow: 0 12rpx 30rpx rgba(59, 130, 246, .25);
	}

	.cd-rent-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.cd-rent-title {
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
	}

	.cd-rent-state {
		color: #fff;
		font-size: 18rpx;
		background: rgba(255, 255, 255, .2);
		border-radius: 999rpx;
		padding: 6rpx 18rpx;
	}

	.cd-rent-label {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 20rpx;
		margin-top: 20rpx;
	}

	.cd-rent-expire {
		display: block;
		color: #fff;
		font-size: 44rpx;
		font-weight: 700;
		margin-top: 8rpx;
	}

	.cd-rent-tip {
		display: block;
		color: rgba(255, 255, 255, .7);
		font-size: 18rpx;
		margin-top: 14rpx;
	}

	.cd-plan-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 32rpx 28rpx 16rpx;
	}

	.cd-plans {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.cd-plan {
		display: flex;
		align-items: center;
		gap: 16rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 24rpx;
		padding: 24rpx;
		transition: all .2s;
	}

	.cd-plan.active {
		border-color: #3b82f6;
		background: rgba(59, 130, 246, .05);
	}

	.cd-plan-name-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.cd-plan-tag {
		font-size: 16rpx;
		font-weight: 700;
		color: #2563eb;
		background: rgba(59, 130, 246, .1);
		border-radius: 999rpx;
		padding: 2rpx 12rpx;
	}

	.cd-plan-price {
		font-size: 30rpx;
		font-weight: 700;
		color: #0f172a;
	}

	.cd-plan-radio {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		border: 2rpx solid #cbd5e1;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.cd-plan-radio.on {
		border-color: #3b82f6;
	}

	.cd-plan-radio.on::after {
		content: '';
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		background: #3b82f6;
	}

	.cd-tip {
		border-radius: 20rpx;
		background: rgba(59, 130, 246, .08);
		border: 1rpx solid rgba(59, 130, 246, .15);
		padding: 20rpx 24rpx;
		margin-top: 24rpx;
	}

	.cd-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.cd-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(59, 130, 246, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cd-dialog-ic-img {
		width: 34rpx;
		height: 34rpx;
	}

	.cd-rent-box {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 24rpx;
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.cd-rent-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.cd-rent-val {
		font-size: 24rpx;
		font-weight: 600;
		color: #1e293b;
	}

	.cd-rent-fee {
		font-size: 32rpx;
		font-weight: 700;
		color: #3b82f6;
	}

	.cd-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}
</style>
