<template>
	<view>
		<view>
			<index-ads></index-ads>
		</view>
		<view v-if="hasOwner == 'Y'">
			<index-ac ref="indexAcRef"></index-ac>
		</view>
		<view v-if="hasOwner == 'N'">
			<index-bind-room ref="indexBindRoomRef"></index-bind-room>
		</view>
	</view>
</template>

<script>
	import indexAds from '@/components/index/index-ads.vue';
	import indexAc from '@/components/index/index-ac.vue';
	import indexBindRoom from '@/components/index-bind-room.vue';

	export default {
		data() {
			return {
				hasOwner:'N',// Y 是业主 N 不是  W 查询中
			};
		},
		components: {
			indexAds,
			indexAc,
			indexBindRoom
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
				if(this.hasOwner == 'Y'){
					setTimeout(function(){
						_that.$refs.indexAcRef._initData();
					},1000)
				}
				
			}
		}
	};
</script>
<style lang="less">




</style>