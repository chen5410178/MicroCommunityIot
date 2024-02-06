<template>
	<view>

		<view>
			<index-header></index-header>
		</view>
		<view class="margin-top-sm">
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
	import indexHeader from '@/components/index/index-header.vue';
	import indexAds from '@/components/index/index-ads.vue';
	import indexAc from '@/components/index/index-ac.vue';
	import indexBindRoom from '@/components/index-bind-room.vue';

	import {
		hasLogin
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


			//this._initIndexData();
		},

		onShareAppMessage: function() {

		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			if (!hasLogin()) {
				return;
			}

			let _that = this;
			getAppUser().then(_data => {
				_that.hasOwner = 'Y';
				_that._initIndexData();
			}, err => {
				_that.hasOwner = 'N';
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