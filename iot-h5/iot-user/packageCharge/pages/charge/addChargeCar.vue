<template>
	<view class="ac-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 当前充电车辆卡 -->
		<view class="ac-mx ac-mt">
			<view class="c-card ac-current">
				<view class="ac-current-head">
					<text class="c-title">当前充电车辆</text>
					<text class="ac-new-tag" v-if="curNewEnergy">新能源</text>
				</view>
				<view class="ac-current-row">
					<image :src="imgUrl + '/h5/images/charge/ic-car-full.png'" class="ac-current-ic" mode="aspectFit"></image>
					<text class="ac-current-plate" v-if="curCarNum">{{ curCarNum }}</text>
					<text class="c-text-sub" v-else>未设置</text>
				</view>
			</view>
		</view>

		<!-- 车牌录入（两框输入） -->
		<view class="ac-mx ac-mt">
			<view class="ac-search-row">
				<view class="ac-prov-box" @click="openProvPanel">
					<input :value="searchProv" readonly placeholder="省" class="ac-prov-input" />
				</view>
				<view class="ac-rest-box">
					<input :value="searchRest" readonly placeholder="输入字母数字" class="ac-rest-input" @click="openSearchKey" />
					<text class="ac-rest-clear" v-if="searchProv || searchRest" @click="clearPlate">✕</text>
				</view>
			</view>

			<!-- 省份选择面板 -->
			<view class="ac-prov-panel" v-if="provPanel">
				<text class="ac-prov-panel-label">请选择省份汉字（车牌第一位）</text>
				<view class="ac-prov-grid">
					<view v-for="(c, i) in KB_PROVINCES" :key="i" class="ac-prov-cell" @click="pickProv(c)">{{ c }}</view>
				</view>
			</view>

			<!-- 历史车牌 -->
			<view class="ac-history" v-if="historyList.length">
				<view class="ac-history-head">
					<text class="c-text-sub">历史车牌</text>
				</view>
				<view class="ac-history-list">
					<view v-for="(p, i) in historyList" :key="i" class="ac-history-item" @click="pickHistory(p)">
						<image :src="imgUrl + '/h5/images/charge/ic-car-full.png'" class="ac-history-ic" mode="aspectFit"></image>
						<text class="ac-history-plate">{{ p }}</text>
						<text class="ac-history-tag" v-if="_isNewEnergy(p)">新能源</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 保存 -->
		<view class="ac-save-bar">
			<view class="ac-save-btn" @click="_submit">保存</view>
		</view>

		<!-- 车牌键盘 -->
		<plate-keyboard ref="plateKey" :show.sync="keyShow" :split-mode="true" @change="onKeyChange" />

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import plateKeyboard from '@/packageCharge/components/charge/plate-keyboard.vue'
	import {
		getUserChargeCar,
		saveUserChargeCar
	} from '@/api/user/userApi.js'
	import {
		hasLogin
	} from '@/api/user/sessionApi.js'
	import {
		KB_PROVINCES,
		getPlateHistory,
		savePlateHistory,
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
				themeDark: false,
				curCarNum: '',
				searchProv: '',
				searchRest: '',
				provPanel: false,
				keyShow: false,
				historyList: [],
				toastMsg: ''
			}
		},
		computed: {
			curNewEnergy() {
				return isNewEnergy(this.curCarNum)
			}
		},
		onLoad() {
			if (!hasLogin()) {
				uni.navigateTo({ url: '/pages/user/login' })
				return
			}
			this.themeDark = chargeStore.themeDark
			this.historyList = getPlateHistory().slice(0, 10)
			this._loadCurrent()
		},
		methods: {
			_isNewEnergy(kw) {
				return isNewEnergy(kw)
			},
			// 查询当前已保存的充电车牌
			_loadCurrent() {
				getUserChargeCar({
					page: 1,
					row: 1
				}).then(res => {
					if (res && res.code == '0' && res.data) {
						const bare = String(res.data).replace(/·/g, '')
						this.curCarNum = bare
						this.searchProv = bare.charAt(0)
						this.searchRest = bare.slice(1)
					}
				}).catch(() => {
					// 未设置时后端抛"车牌不存在"，静默展示"未设置"
					this.curCarNum = ''
				})
			},
			openProvPanel() {
				this.keyShow = false
				this.provPanel = !this.provPanel
			},
			pickProv(c) {
				this.searchProv = c
				this.provPanel = false
				this.$refs.plateKey.open(c, this.searchRest)
			},
			openSearchKey() {
				this.provPanel = false
				this.$refs.plateKey.open(this.searchProv, this.searchRest)
			},
			onKeyChange(e) {
				this.searchProv = e.prov
				this.searchRest = e.rest
			},
			clearPlate() {
				this.searchProv = ''
				this.searchRest = ''
			},
			pickHistory(p) {
				const bare = p.replace(/·/g, '')
				this.searchProv = bare.charAt(0)
				this.searchRest = bare.slice(1)
			},
			_submit() {
				const bare = (this.searchProv + this.searchRest).replace(/·/g, '')
				if (!this.searchProv) {
					this._toast('请先选择省份')
					return
				}
				if (!isPlateValid(bare)) {
					this._toast('车牌格式不正确')
					return
				}
				saveUserChargeCar({
					carNum: bare
				}).then(() => {
					savePlateHistory(bare)
					this._toast('保存成功')
					setTimeout(() => {
						uni.navigateBack()
					}, 600)
				}).catch(e => {
					this._toast((typeof e === 'string' ? e : (e && e.message)) || '保存失败')
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
	.ac-page {
		padding-bottom: 180rpx;
	}

	.ac-mx {
		margin-left: 28rpx;
		margin-right: 28rpx;
	}

	.ac-mt {
		margin-top: 28rpx;
	}

	/* 当前充电车辆卡 */
	.ac-current {
		padding: 28rpx;
	}

	.ac-current-head {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.ac-new-tag {
		font-size: 16rpx;
		font-weight: 600;
		color: #059669;
		background: rgba(16, 185, 129, .1);
		border: 1rpx solid rgba(16, 185, 129, .25);
		border-radius: 999rpx;
		padding: 4rpx 14rpx;
	}

	.ac-current-row {
		display: flex;
		align-items: center;
		gap: 16rpx;
		margin-top: 24rpx;
	}

	.ac-current-ic {
		width: 56rpx;
		height: 56rpx;
	}

	.ac-current-plate {
		font-size: 30rpx;
		font-weight: 700;
		background: #1d4ed8;
		color: #fff;
		border-radius: 12rpx;
		padding: 8rpx 20rpx;
		letter-spacing: 4rpx;
	}

	/* 车牌录入 */
	.ac-search-row {
		display: flex;
		gap: 16rpx;
	}

	.ac-prov-box {
		width: 130rpx;
		flex-shrink: 0;
		display: flex;
		align-items: center;
		justify-content: center;
		border: 1rpx solid #e2e8f0;
		background: #f8fafc;
		border-radius: 20rpx;
	}

	.ac-prov-input {
		width: 100%;
		text-align: center;
		font-size: 26rpx;
		font-weight: 700;
		color: #0f172a;
		letter-spacing: 4rpx;
		height: 88rpx;
	}

	.ac-rest-box {
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

	.ac-rest-input {
		flex: 1;
		font-size: 24rpx;
		font-weight: 700;
		color: #0f172a;
		letter-spacing: 4rpx;
	}

	.ac-rest-clear {
		color: #94a3b8;
		font-size: 24rpx;
		padding: 8rpx;
	}

	/* 省份面板 */
	.ac-prov-panel {
		margin-top: 20rpx;
		border: 1rpx solid #e2e8f0;
		background: #fff;
		border-radius: 20rpx;
		padding: 20rpx;
		box-shadow: 0 10rpx 30rpx rgba(15, 23, 42, .08);
	}

	.ac-prov-panel-label {
		display: block;
		font-size: 18rpx;
		color: #94a3b8;
		margin-bottom: 12rpx;
	}

	.ac-prov-grid {
		display: grid;
		grid-template-columns: repeat(9, 1fr);
		gap: 10rpx;
	}

	.ac-prov-cell {
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

	.ac-prov-cell:active {
		background: #eff6ff;
	}

	/* 历史车牌 */
	.ac-history {
		margin-top: 28rpx;
	}

	.ac-history-head {
		margin-bottom: 12rpx;
	}

	.ac-history-list {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.ac-history-item {
		display: flex;
		align-items: center;
		gap: 14rpx;
		background: #fff;
		border: 1rpx solid #f1f5f9;
		border-radius: 18rpx;
		padding: 20rpx 24rpx;
	}

	.ac-history-ic {
		width: 28rpx;
		height: 28rpx;
		flex-shrink: 0;
	}

	.ac-history-plate {
		flex: 1;
		font-size: 24rpx;
		font-weight: 700;
		color: #334155;
		letter-spacing: 4rpx;
	}

	.ac-history-tag {
		font-size: 16rpx;
		font-weight: 600;
		color: #059669;
		background: rgba(16, 185, 129, .1);
		border-radius: 999rpx;
		padding: 4rpx 12rpx;
	}

	/* 保存 */
	.ac-save-bar {
		position: fixed;
		left: 28rpx;
		right: 28rpx;
		bottom: 30rpx;
	}

	.ac-save-btn {
		border-radius: 24rpx;
		background: linear-gradient(135deg, #10b981, #059669);
		color: #fff;
		font-size: 28rpx;
		font-weight: 700;
		height: 92rpx;
		line-height: 92rpx;
		text-align: center;
		box-shadow: 0 10rpx 24rpx rgba(16, 185, 129, .3);
	}

	/* 黑夜主题 */
	.charge-theme-dark .ac-current-plate {
		background: #2563eb !important;
	}

	.charge-theme-dark .ac-prov-box,
	.charge-theme-dark .ac-rest-box {
		background: #182338 !important;
		border-color: #334155 !important;
	}

	.charge-theme-dark .ac-prov-input,
	.charge-theme-dark .ac-rest-input {
		color: #e2e8f0 !important;
	}

	.charge-theme-dark .ac-prov-panel {
		background: #1e293b !important;
		border-color: #334155 !important;
	}

	.charge-theme-dark .ac-prov-cell {
		background: #182338 !important;
		color: #e2e8f0 !important;
		border-color: #1f2b40 !important;
	}

	.charge-theme-dark .ac-prov-cell:active {
		background: #1e293b !important;
	}

	.charge-theme-dark .ac-history-item {
		background: #1e293b !important;
		border-color: #334155 !important;
	}

	.charge-theme-dark .ac-history-plate {
		color: #e2e8f0 !important;
	}
</style>
