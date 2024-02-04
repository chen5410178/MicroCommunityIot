<template>
	<view>
		<view>
			<index-ads></index-ads>
		</view>
		<view>
			<index-ac ref="indexAcRef"></index-ac>
		</view>
	</view>
</template>

<script>
	import indexAds from '@/components/index/index-ads.vue';
	import indexAc from '@/components/index/index-ac.vue';

	export default {
		data() {
			return {

			};
		},
		components: {
			indexAds,
			indexAc
		},

		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {

			this._initIndexData();
		},

		onShareAppMessage: function() {

		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {

		},

		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {

		},
		methods: {
			_initIndexData:function(){
				let _that = this;
				setTimeout(function(){
					_that.$refs.indexAcRef._initData();
				},1000)
			}
		}
	};
</script>
<style lang="less">




</style>