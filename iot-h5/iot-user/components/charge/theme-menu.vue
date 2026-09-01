<template>
	<view>
		<view class="tm-mask" v-if="show" @click="close"></view>
		<view class="tm-menu" v-if="show">
			<view class="tm-arrow"></view>
			<text class="tm-label">外观模式</text>
			<view class="tm-item" :class="{ on: !dark }" @click="pick(false)">
				<image src="/static/h5/images/common/ic-sun.png" class="tm-ic" mode="aspectFit"></image>
				<text class="tm-item-text">白天模式</text>
				<image v-if="!dark" src="/static/h5/images/common/ic-check.png" class="tm-check-ic" mode="aspectFit"></image>
			</view>
			<view class="tm-item" :class="{ on: dark }" @click="pick(true)">
				<image src="/static/h5/images/common/ic-moon.png" class="tm-ic" mode="aspectFit"></image>
				<text class="tm-item-text">黑夜模式</text>
				<image v-if="dark" src="/static/h5/images/common/ic-check.png" class="tm-check-ic" mode="aspectFit"></image>
			</view>
			<view class="tm-divider"></view>
			<text class="tm-label">定时</text>
			<view class="tm-item" @click="openSched">
				<image src="/static/h5/images/common/ic-clock-green.png" class="tm-ic" mode="aspectFit"></image>
				<text class="tm-item-text">定时切换黑夜</text>
				<text class="tm-sched-desc">{{ sched.on ? sched.start + '-' + sched.end : '关闭' }}</text>
			</view>
		</view>

		<!-- 定时切换黑夜弹层 -->
		<view class="c-mask" v-if="schedShow" @click="schedShow = false"></view>
		<view class="c-dialog" v-if="schedShow">
			<view class="tm-sched-head">
				<view class="tm-sched-ic">
					<image src="/static/h5/images/common/ic-clock-green.png" class="tm-sched-ic-img" mode="aspectFit"></image>
				</view>
				<view style="flex:1;">
					<text class="c-title" style="display:block;">定时切换黑夜</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">到点自动切换主题，支持跨天时段</text>
				</view>
				<view class="tm-toggle" :class="{ on: sched.on }" @click="toggleSched">
					<view class="tm-knob" :class="{ on: sched.on }"></view>
				</view>
			</view>
			<view class="tm-sched-times">
				<view style="flex:1;">
					<text class="c-text-sub">黑夜开始</text>
					<picker mode="time" :value="sched.start" @change="e => sched.start = e.detail.value">
						<view class="tm-time-input">{{ sched.start }}</view>
					</picker>
				</view>
				<text class="c-text-sub" style="align-self:flex-end;padding-bottom:20rpx;">至</text>
				<view style="flex:1;">
					<text class="c-text-sub">黑夜结束</text>
					<picker mode="time" :value="sched.end" @change="e => sched.end = e.detail.value">
						<view class="tm-time-input">{{ sched.end }}</view>
					</picker>
				</view>
			</view>
			<view class="tm-sched-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="schedShow = false">取消</view>
				<view class="c-btn-primary" style="flex:1;" @click="saveSched">保存</view>
			</view>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'

	export default {
		name: 'theme-menu',
		props: {
			show: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				schedShow: false,
				sched: {
					on: false,
					start: '22:00',
					end: '07:00'
				}
			}
		},
		computed: {
			dark() {
				return chargeStore.themeDark
			}
		},
		created() {
			this.sched = Object.assign({}, chargeStore.themeSched)
		},
		methods: {
			close() {
				this.$emit('update:show', false)
			},
			pick(dark) {
				chargeStore.themeDark = dark
				chargeStore.saveTheme()
				this._applyPageTheme()
				this.close()
			},
			openSched() {
				this.sched = Object.assign({}, chargeStore.themeSched)
				this.schedShow = true
				this.close()
			},
			toggleSched() {
				this.sched.on = !this.sched.on
			},
			saveSched() {
				chargeStore.themeSched = Object.assign({}, this.sched)
				chargeStore.saveTheme()
				this.schedShow = false
				this._checkSched()
				uni.showToast({
					title: this.sched.on ? '定时切换已保存' : '定时切换已关闭',
					icon: 'none'
				})
			},
			_checkSched() {
				if (!chargeStore.themeSched.on) return
				const d = new Date()
				const hhmm = ('0' + d.getHours()).slice(-2) + ':' + ('0' + d.getMinutes()).slice(-2)
				const s = chargeStore.themeSched.start
				const e = chargeStore.themeSched.end
				let dark = false
				if (s === e) dark = false
				else if (s < e) dark = hhmm >= s && hhmm < e
				else dark = hhmm >= s || hhmm < e
				chargeStore.themeDark = dark
				chargeStore.saveTheme()
				this._applyPageTheme()
			},
			_applyPageTheme() {
				// 将主题应用到当前页面根节点
				const pages = getCurrentPages()
				const page = pages[pages.length - 1]
				if (page && page.$el) {
					page.$el.classList.toggle('charge-theme-dark', chargeStore.themeDark)
				}
			}
		}
	}
</script>

<style>
	.tm-mask {
		position: fixed;
		inset: 0;
		z-index: 965;
	}

	.tm-menu {
		position: fixed;
		right: 24rpx;
		top: 120rpx;
		z-index: 966;
		width: 420rpx;
		border-radius: 28rpx;
		background: #1c1c1e;
		box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, .4);
		border: 1rpx solid rgba(255, 255, 255, .1);
		overflow: hidden;
		padding: 16rpx 0;
	}

	.tm-arrow {
		position: absolute;
		top: -8rpx;
		right: 56rpx;
		width: 24rpx;
		height: 24rpx;
		transform: rotate(45deg);
		background: #1c1c1e;
		border-left: 1rpx solid rgba(255, 255, 255, .1);
		border-top: 1rpx solid rgba(255, 255, 255, .1);
	}

	.tm-label {
		display: block;
		padding: 12rpx 32rpx 8rpx;
		font-size: 18rpx;
		color: rgba(255, 255, 255, .4);
		letter-spacing: 2rpx;
	}

	.tm-item {
		display: flex;
		align-items: center;
		gap: 16rpx;
		padding: 22rpx 32rpx;
		color: rgba(255, 255, 255, .9);
	}

	.tm-item.on {
		background: rgba(255, 255, 255, .08);
	}

	.tm-item-text {
		flex: 1;
		font-size: 24rpx;
	}

	.tm-ic {
		width: 28rpx;
		height: 28rpx;
		flex-shrink: 0;
	}

	.tm-check-ic {
		width: 26rpx;
		height: 26rpx;
		flex-shrink: 0;
	}

	.tm-check {
		color: #10b981;
		font-size: 22rpx;
		font-weight: 700;
	}

	.tm-sched-desc {
		font-size: 20rpx;
		color: rgba(255, 255, 255, .5);
	}

	.tm-divider {
		height: 1rpx;
		background: rgba(255, 255, 255, .1);
		margin: 8rpx 32rpx;
	}

	.tm-sched-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.tm-sched-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(99, 102, 241, .1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.tm-sched-ic-img {
		width: 32rpx;
		height: 32rpx;
	}

	.tm-toggle {
		width: 88rpx;
		height: 48rpx;
		border-radius: 999rpx;
		background: #cbd5e1;
		position: relative;
		transition: all .2s;
		flex-shrink: 0;
	}

	.tm-toggle.on {
		background: #10b981;
	}

	.tm-knob {
		position: absolute;
		top: 4rpx;
		left: 4rpx;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background: #fff;
		box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, .2);
		transition: all .2s;
	}

	.tm-knob.on {
		left: 44rpx;
	}

	.tm-sched-times {
		display: flex;
		align-items: center;
		gap: 16rpx;
		margin-top: 28rpx;
	}

	.tm-time-input {
		margin-top: 12rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 20rpx;
		padding: 20rpx 24rpx;
		font-size: 26rpx;
		font-weight: 600;
		color: #0f172a;
		text-align: center;
	}

	.tm-sched-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 32rpx;
	}
</style>
