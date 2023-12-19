<template>
	<view>
		<scroll-view scroll-x class="bg-white nav ">
			<view class="flex text-center">
				<view class="cu-item flex-sub" :class="active==0?'text-green cur':''" @tap="tabSelect(0)">
					申请人脸
				</view>
				<view class="cu-item flex-sub" :class="active==1?'text-green cur':''" @tap="tabSelect(1)">
					申请记录
				</view>
			</view>
		</scroll-view>
		
		<view v-show="active == 0" class="margin-top">
			<apply-face ref="applyFaceRef" ></apply-face>
		</view>
		<view v-show="active == 1" class="margin-top">
			<apply-face-detail ref="applyFaceDetailRef"></apply-face-detail>
		</view>
	</view>
</template>

<script>
	import applyFace from '@/components/face/applyFace.vue';
	import applyFaceDetail from '@/components/face/applyFaceDetail.vue'
	export default {
		data() {
			return {
				active:0,
			}
		},
		components:{
			applyFace,
			applyFaceDetail
		},
		methods: {
			
			tabSelect:function(_active){
				this.active = _active;
				if(this.active == 1){
					this.$refs.applyFaceDetailRef.loadApplyFaceDetail();
				}
			}
			
			
		}
	}
</script>

<style>

</style>
