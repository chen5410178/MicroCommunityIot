<template>
	<view>
		<view class="home_swiper ">
			<swiper class="ad-swiper bg-white radius-lg" indicator-dots="true" indicator-color="rgba(228,228,228,1)"
				indicator-active-color="#FECA49" autoplay="true" interval="5000" duration="1000" circular="true">
				<block v-for="(item, index) in banner" :key="index" @click="_advertJump(item)">
					<swiper-item>
						<image style="height:100%;width: 100%" class="radius-lg" :src="item.url"></image>
					</swiper-item>
				</block>
			</swiper>
			<!-- <vc-ads :height="300" locationTypeCd="5000"></vc-ads> -->
		</view>
	</view>
</template>

<script>
	import conf from '@/conf/config.js'
	export default {
		name: "indexAds",
		data() {
			return {
				banner: [{
						url: conf.commonBaseUrl+'/h5/images/serve/banner1.jpg'
					},
					{
						url: conf.commonBaseUrl+'/h5/images/serve/banner2.jpg'
					}
				],
			};
		},
		created() {
			//this._loadAdvertPhoto();
		},
		methods:{
			_loadAdvertPhoto: function() {
				let _that = this;
				let _objData = {
					page: 1,
					row: 5,
					locationTypeCd: '2000',
					viewType: '8888',
					clientType: 'H5'
				};
				//查询 广告
				loadAdverts(_objData)
					.then(function(_aPhotos) {
						_that.ad = _aPhotos;
						if(!_aPhotos || _aPhotos.length <1){
							_that.ad = _that.banner;
						}
					});
			},
			_advertJump: function(ad) {
				if (ad.advertType == 3 || !ad.pageUrl) {
					return;
				}
				if (ad.advertType == 2) {
					// 站外
					let url = encodeURIComponent(ad.pageUrl)
					uni.navigateTo({
						url: '/pages/hcWebView/hcWebView?url=' + url
					})
					return;
				}
				if (ad.advertType == 1) {
					// 站内
					uni.navigateTo({
						url: ad.pageUrl
					})
					return;
				}
			},
		}
	}
</script>

<style>
	.home_swiper {
		margin-bottom: 20upx;
	}
</style>
