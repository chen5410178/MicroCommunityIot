<template>
	<view class="scan-page" :class="{ 'charge-theme-dark': themeDark }">
		<view class="scan-bg">
			<view class="scan-frame">
				<view class="corner c1"></view>
				<view class="corner c2"></view>
				<view class="corner c3"></view>
				<view class="corner c4"></view>
				<view class="scan-line"></view>
			</view>

			<!-- 扫码按钮 -->
			<view class="scan-again-btn" @click="_startScan">
				<image :src="imgUrl + '/h5/images/charge/ic-scan.png'" class="scan-again-ic" mode="aspectFit"></image>
				<text class="scan-again-text">请点击扫码</text>
			</view>
		</view>

		<view class="c-toast" v-if="toastMsg" :style="{ opacity: 1 }">{{ toastMsg }}</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		getChargeMachineByCode
	} from '@/api/machine/machineApi.js'

	export default {
		data() {
			return {
				chgType: chargeStore.chgType,
				scanTimer: null,
				toastMsg: ''
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			}
		},
		onLoad() {
			// 进入扫码页自动唤起微信扫一扫（仅进入时自动扫一次）
			this._startScan()
		},
		onShow() {
			// 【关键】onShow 禁止调起 uni.scanCode！
			// 原因：扫码成功 → switchTab 首页 → navigateTo 充电页后，从充电页返回时
			//       会先触发本页 onShow，若此处再自动扫码，将出现"返回回来又自动弹扫一扫、
			//       跳回扫码页"的死循环，用户无法正常关闭/返回。
			// onShow 仅做必要的状态同步（如 chgType），不做任何扫码动作
			this.chgType = chargeStore.chgType
		},
		onHide() {
			clearTimeout(this.scanTimer)
		},
		onUnload() {
			clearTimeout(this.scanTimer)
		},
		methods: {
			// #ifdef H5
			_startScan() {
				// H5 端 uni.scanCode 不可用，提示引导使用微信扫一扫充电
				clearTimeout(this.scanTimer)
				this._toast('请使用微信扫一扫充电')
			},
			// #endif
			// #ifdef MP-WEIXIN
			_startScan() {
				// 小程序真机扫码：调起扫码
				clearTimeout(this.scanTimer)
				uni.scanCode({
					onlyFromCamera: false,
					scanType: ['qrCode', 'barCode'],
					success: (res) => {
						this._handleScanResult(res.result)
					},
					fail: () => {
						// 用户取消扫码：停留在本页，可点击「扫一扫」按钮重新扫码
						this._toast('已取消扫码')
					}
				})
			},
			// #endif
			_parseScanResult(result) {
				// 解析二维码内容：仅解析小程序端地址（chargeBikeMini(Port)/chargeCarMini(Port)）+ 纯文本编号
				// H5 地址（chargeBikeH5(Port)/chargeCarH5(Port)）及 songbing/ykc 等格式返回 null
				const raw = String(result || '').replace(/\s+/g, '')
				if (!raw) return null
				// 提取 URL 路径部分（去掉协议/域名与 query/hash）
				let path = raw
				const urlMatch = raw.match(/^https?:\/\/[^/]+([^?#]*)/i)
				if (urlMatch) {
					path = urlMatch[1]
				}
				path = path.split('?')[0].split('#')[0]
				// 小程序端端口地址：/app/chargeBikeMiniPort/{mc}/{pc}、/app/chargeCarMiniPort/{mc}/{pc}
				let mPort = path.match(/^\/app\/charge(Bike|Car)MiniPort\/([^/]+)\/([^/]+)$/i)
				if (mPort) {
					return { machineCode: mPort[2], portCode: mPort[3] }
				}
				// 小程序端整桩地址：/app/chargeBikeMini/{mc}、/app/chargeCarMini/{mc}
				let mMini = path.match(/^\/app\/charge(Bike|Car)Mini\/([^/]+)$/i)
				if (mMini) {
					return { machineCode: mMini[2] }
				}
				// 带 URL 特征的地址（H5/公众号/songbing/ykc 等）不解析
				if (urlMatch || path.indexOf('/') >= 0) {
					return null
				}
				// 纯文本编码：整个结果作为 machineCode
				return { machineCode: raw }
			},
			_handleScanResult(result) {
				// 扫码结果解析 → 调免登录接口按编码查桩 → 直达充电页
				const parsed = this._parseScanResult(result)
				if (!parsed || !parsed.machineCode) {
					this._toast('未识别到有效二维码')
					return
				}
				const params = { machineCode: parsed.machineCode }
				if (parsed.portCode) {
					params.portCode = parsed.portCode
				}
				getChargeMachineByCode(params).then(_data => {
					const d = (_data && _data.data) || {}
					if (!_data || _data.code !== 0 || !d.machineId) {
						this._toast('未找到该充电桩')
						return
					}
					this._toPile(d)
				}).catch(() => {
					this._toast('未找到该充电桩')
				})
			},
			_toPile(d) {
				// 按接口返回组装充电页 URL（不再依赖本地桩列表）
				let url = ''
				if (d.chargeType === '1002') {
					// 汽车充电桩：有端口直达 carChargePort，无端口进入 machineToCarCharge
					url = d.portId
						? '/packageCharge/pages/charge/carChargePort?machineId=' + (d.machineId || '') + '&communityId=' +
							(d.communityId || '') + '&portId=' + d.portId
						: '/packageCharge/pages/charge/machineToCarCharge?machineId=' + (d.machineId || '') + '&communityId=' +
							(d.communityId || '')
				} else {
					// 两轮充电桩：进入 machineToCharge（带 portId 自动选中插座）
					url = '/packageCharge/pages/charge/machineToCharge?machineId=' + (d.machineId || '') + '&communityId=' +
						(d.communityId || '')
					if (d.portId) {
						url += '&portId=' + d.portId
					}
				}
				// 先切回首页 tab，再打开充电页：从充电页返回时回到首页
				uni.switchTab({
					url: '/pages/index/index',
					success: () => {
						// switchTab 完成后延时打开充电页，避免时序问题
						setTimeout(() => {
							uni.navigateTo({
								url: url
							})
						}, 300)
					}
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
	.scan-page {
		height: 100vh;
		background: #000;
		position: relative;
		overflow: hidden;
	}

	.scan-bg {
		position: relative;
		height: 100%;
		background: radial-gradient(circle at 50% 42%, #1a2b22 0%, #0a120e 55%, #050805 100%);
	}

	.scan-frame {
		position: absolute;
		left: 50%;
		top: 36%;
		transform: translate(-50%, -50%);
	}

	.scan-again-btn {
		position: absolute;
		left: 50%;
		top: 50%;
		transform: translate(-50%, -50%);
		width: 420rpx;
		padding: 44rpx 0;
		border-radius: 32rpx;
		background: rgba(255, 255, 255, .08);
		border: 1rpx solid rgba(255, 255, 255, .2);
		color: #fff;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 24rpx;
	}

	.scan-again-ic {
		width: 180rpx;
		height: 180rpx;
	}

	.scan-again-text {
		color: #fff;
		font-size: 28rpx;
		font-weight: 500;
	}
</style>
