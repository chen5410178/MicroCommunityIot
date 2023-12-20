<template>
	<view>
		<scroll-view scroll-x class="bg-white nav ">
			<view class="flex text-center">
				<view class="cu-item flex-sub" :class="active==0?'text-blue cur':''" @tap="tabSelect(0)">
					通行码
				</view>
				<view class="cu-item flex-sub" :class="active==1?'text-blue cur':''" @tap="tabSelect(1)">
					远程开门
				</view>
			</view>
		</scroll-view>
		<view v-show="active == 0" class="margin-top">
			<apply-pass-qrcode :communityId='communityId' :openId="openId" :pqsId="pqsId"></apply-pass-qrcode>
		</view>
		<view v-show="active == 1" class="margin-top">
			
		</view>
	</view>
</template>

<script>
	import applyPassQrcode from '../../components/pass/applyPassQrcode.vue';
	export default {
		data() {
			return {
				active:0,
				communityId:'',
				pqsId:'',
				openId:'123123123'
			}
		},
		components:{
			applyPassQrcode
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.pqsId = options.pqsId;
		},
		methods: {
			tabSelect:function(_active){
				this.active = _active;
				if(this.active == 1){
					//this.$refs.applyFaceDetailRef.loadApplyFaceDetail();
				}
			}
		}
	}
</script>

<style>

</style>
