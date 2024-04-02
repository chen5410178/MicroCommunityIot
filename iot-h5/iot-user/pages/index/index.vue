<template>
	<view>
		<view class="padding-lr-sm">

			<view class="margin-top-sm ">
				<index-ads></index-ads>
			</view>
			<view class="margin-top">
				<index-header></index-header>
			</view>
			<view class="margin-top-sm">
				<index-ac ref="indexAcRef"></index-ac>
			</view>
			<view class="margin-top">
				<index-bind-room ref="indexBindRoomRef" :hasOwner="hasOwner"></index-bind-room>
			</view>
			
		</view>
	</view>
</template>

<script>
	import indexHeader from '@/components/index/index-header.vue';
	import indexAds from '@/components/index/index-ads.vue';
	import indexAc from '@/components/index/index-ac.vue';
	import indexBindRoom from '@/components/index/index-bind-room.vue';

	import {
		hasLogin,
		autoLogin
	} from '@/api/user/sessionApi.js';
	import {
		getAppUser
	} from '../../api/user/appUser.js';

	export default {
		data() {
			return {
				hasOwner: 'W', // Y 是业主 N 不是  W 查询中
			};
		},
		components: {
			indexAds,
			indexAc,
			indexBindRoom,
			indexHeader
		},

		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			autoLogin(options);
		},

		onShareAppMessage: function() {

		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			let _that = this;
			if (!hasLogin()) {
				_that.hasOwner = 'N';
				return;
			}


			getAppUser().then(_data => {
				_that.hasOwner = 'Y';
				_that._initIndexData();
			}, err => {
				console.log(err);
				if (err.code != -101) {
					_that.hasOwner = 'N';
				} else {
					_that.hasOwner = 'D';
				}

			})
		},


		methods: {
			_initIndexData: function() {
				let _that = this;
				if (this.hasOwner == 'Y') {
					setTimeout(function() {
						_that.$refs.indexAcRef._initData();
					}, 1000)
				}

			},

		}
	};
</script>
<style lang="less">




</style>