<template>
	<view>
		<scroll-view scroll-x class="bg-white nav ">
			<view class="flex text-center">
				<view class="cu-item flex-sub" :class="active==0?'text-blue cur':''" @tap="tabSelect(0)">
					申请人脸
				</view>
				<view class="cu-item flex-sub" :class="active==1?'text-blue cur':''" @tap="tabSelect(1)">
					申请记录
				</view>
			</view>
		</scroll-view>

		<view v-show="active == 0" class="margin-top">
			<apply-face ref="applyFaceRef" :communityId="communityId" :pfqId="pfqId" :openId="openId"></apply-face>
		</view>
		<view v-show="active == 1" class="margin-top">
			<apply-face-detail ref="applyFaceDetailRef" :communityId="communityId" :openId="openId"></apply-face-detail>
		</view>
	</view>
</template>

<script>
	import applyFace from '@/components/face/applyFace.vue';
	import applyFaceDetail from '@/components/face/applyFaceDetail.vue';
	import {
		isNotNull
	} from '../../lib/java110/utils/StringUtil.js';
	import {
		refreshUserOpenId,
		getOpenIdFromAliPay
	} from '../../api/user/userApi.js'
	export default {
		data() {
			return {
				active: 0,
				communityId: '',
				pfqId: '',
				openId: ''
			}
		},
		components: {
			applyFace,
			applyFaceDetail
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.pfqId = options.pfqId;
			if (!isNotNull(this.openId)) {
				//刷新 openId
				this._refreshWechatOpenId();
				return;
			}
		},
		onShow() {

			if (this.$refs.applyFaceRef && this.active == 0) {
				let _selectRoom = uni.getStorageSync('selectRoom');
				if (_selectRoom) {
					uni.removeStorageSync('selectRoom');
					this.$refs.applyFaceRef.setRoom(_selectRoom);
				}
			}

		},
		methods: {

			tabSelect: function(_active) {
				this.active = _active;
				if (this.active == 1) {
					this.$refs.applyFaceDetailRef.loadApplyFaceDetail();
				}
			},
			_refreshWechatOpenId: function() {
				// console.log("判断微信还是支付宝");
				// console.log(isWxOrAli());
				let _redirectUrl = window.location.href;
				refreshUserOpenId({
					redirectUrl: _redirectUrl,
					wAppId: this.appId,
				}).then(_data => {
					console.log(_data, 123)
					if (_data.code == 0) {
						window.location.href = _data.data.openUrl;
						return;
					}
				});
			},


		}
	}
</script>

<style>

</style>