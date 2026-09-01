<template>
	<view>
		<!-- 车牌键盘 -->
		<view class="pk-mask" v-if="show" @click="close"></view>
		<view class="pk-board" v-if="show">
			<text class="pk-label">{{ splitMode ? '请选择省份汉字' : '请选择省份汉字' }}</text>
			<scroll-view scroll-x class="pk-provinces" v-if="!splitMode">
				<view v-for="(c, i) in KB_PROVINCES" :key="i" class="pk-key prov" @click="kbChar(c)">{{ c }}</view>
			</scroll-view>
			<view class="pk-num">
				<view v-for="(c, i) in KB_NUMS" :key="i" class="pk-key" @click="kbChar(c)">{{ c }}</view>
			</view>
			<view class="pk-let">
				<view v-for="(c, i) in KB_LET1" :key="i" class="pk-key" @click="kbChar(c)">{{ c }}</view>
			</view>
			<view class="pk-let">
				<view v-for="(c, i) in KB_LET2" :key="i" class="pk-key" @click="kbChar(c)">{{ c }}</view>
			</view>
			<view class="pk-tails">
				<text class="pk-tail-label">特殊尾字</text>
				<view v-for="(c, i) in KB_TAILS" :key="i" class="pk-key" @click="kbTail(c)">{{ c }}</view>
			</view>
			<view class="pk-action">
				<view class="pk-key" style="flex:1;" @click="kbChar('Y')">Y</view>
				<view class="pk-key" style="flex:1;" @click="kbChar('Z')">Z</view>
				<view class="pk-key pk-del" @click="kbDel">⌫</view>
				<view class="pk-key pk-done" @click="close">完成</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		KB_PROVINCES,
		KB_NUMS,
		KB_LET1,
		KB_LET2,
		KB_TAILS
	} from '@/packageCharge/api/mock/carPlateData.js'

	export default {
		name: 'plate-keyboard',
		props: {
			// 是否显示
			show: {
				type: Boolean,
				default: false
			},
			// 分框模式：省份写第一个框、字母数字写第二个框
			splitMode: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				KB_PROVINCES,
				KB_NUMS,
				KB_LET1,
				KB_LET2,
				KB_TAILS,
				prov: '',
				rest: ''
			}
		},
		methods: {
			/**
			 * 打开键盘并设置当前值
			 * @param {String} prov 省份
			 * @param {String} rest 其余字母数字
			 */
			open(prov, rest) {
				this.prov = prov || ''
				this.rest = rest || ''
				this.$emit('update:show', true)
			},
			close() {
				this.$emit('update:show', false)
			},
			kbChar(ch) {
				const isHan = /[\u4e00-\u9fa5]/.test(ch)
				if (this.splitMode) {
					// 分框模式：省份写第一个框，字母数字写第二个框
					if (isHan) {
						this.prov = ch
					} else {
						if (!this.prov) {
							this._toast('请先选择省份')
							return
						}
						if (this.rest.length >= 7) return
						this.rest += ch
					}
				} else {
					// 单框模式：首位省份，其余字母数字
					if (isHan) {
						if (/[\u4e00-\u9fa5]/.test(this.prov + this.rest)) this.prov = ch
						else this.prov = ch
					} else {
						if (!this.prov) {
							this._toast('请先选择省份')
							return
						}
						if ((this.prov + this.rest).length >= 8) return
						this.rest += ch
					}
				}
				this._emit()
			},
			kbDel() {
				if (this.rest) this.rest = this.rest.slice(0, -1)
				else this.prov = ''
				this._emit()
			},
			kbTail(ch) {
				if (this.rest.length < 5) {
					this._toast('请先输入完整车牌（省份+字母+5位）后再选择尾字')
					return
				}
				if (this.rest.length >= 6) return
				this.rest += ch
				this._emit()
			},
			_emit() {
				this.$emit('change', {
					prov: this.prov,
					rest: this.rest,
					plate: this.prov + this.rest
				})
			},
			_toast(msg) {
				uni.showToast({
					title: msg,
					icon: 'none'
				})
			}
		}
	}
</script>

<style>
	.pk-mask {
		position: fixed;
		inset: 0;
		z-index: 960;
		background: rgba(2, 6, 23, .45);
	}

	.pk-board {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 961;
		background: #f1f5f9;
		border-radius: 28rpx 28rpx 0 0;
		padding: 20rpx 20rpx 30rpx;
	}

	.pk-label {
		display: block;
		font-size: 16rpx;
		color: #94a3b8;
		padding: 0 8rpx 12rpx;
	}

	.pk-provinces {
		white-space: nowrap;
		padding-bottom: 8rpx;
	}

	.pk-key {
		height: 76rpx;
		border-radius: 16rpx;
		background: #fff;
		color: #1e293b;
		font-size: 26rpx;
		font-weight: 700;
		display: inline-flex;
		align-items: center;
		justify-content: center;
		border: 1rpx solid #e2e8f0;
		box-shadow: 0 2rpx 6rpx rgba(15, 23, 42, .04);
	}

	.pk-key.prov {
		width: 88rpx;
		margin-right: 10rpx;
	}

	.pk-num {
		display: grid;
		grid-template-columns: repeat(10, 1fr);
		gap: 10rpx;
		margin-top: 8rpx;
	}

	.pk-let {
		display: grid;
		grid-template-columns: repeat(6, 1fr);
		gap: 10rpx;
		margin-top: 8rpx;
	}

	.pk-tails {
		display: flex;
		align-items: center;
		gap: 10rpx;
		margin-top: 8rpx;
	}

	.pk-tail-label {
		width: 120rpx;
		font-size: 16rpx;
		color: #94a3b8;
		text-align: center;
		flex-shrink: 0;
	}

	.pk-tails .pk-key {
		flex: 1;
	}

	.pk-action {
		display: flex;
		gap: 10rpx;
		margin-top: 8rpx;
	}

	.pk-action .pk-key {
		height: 80rpx;
	}

	.pk-del {
		width: 160rpx;
		background: #e2e8f0;
		color: #64748b;
	}

	.pk-done {
		width: 200rpx;
		background: #10b981;
		color: #fff;
		border-color: #10b981;
	}

	/* 黑夜主题 */
	.charge-theme-dark .pk-board {
		background: #1e293b !important;
		border-top-color: #334155 !important;
	}

	.charge-theme-dark .pk-key {
		background: #334155 !important;
		color: #e2e8f0 !important;
		border-color: #475569 !important;
	}

	.charge-theme-dark .pk-del {
		background: #334155 !important;
		color: #cbd5e1 !important;
	}

	.charge-theme-dark .pk-done {
		background: #10b981 !important;
		color: #fff !important;
		border-color: #10b981 !important;
	}
</style>
