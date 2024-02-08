<template>
	<view>
		<view class="block__title">
			<text>访客预约历史</text>
		</view>
		<view v-if="visits && visits.length>0">
			<view v-for="(item,index) in visits" :key="index"
				class="bg-white margin-bottom margin-right-xs radius margin-left-xs padding" @click="_showDetail(item)">
				<view class="flex padding-bottom-xs solid-bottom justify-between">
					<view>审核状态</view>
					<view class="text-gray">{{item.stateName}}</view>
				</view>
				<view class="flex margin-top justify-between">
					<view class="text-gray">访客姓名</view>
					<view class="text-gray">{{item.name}}</view>
				</view>
				<view class="flex margin-top-xs justify-between">
					<view class="text-gray">预计来访时间</view>
					<view class="text-gray">{{item.visitTime}}</view>
				</view>
			</view>
		</view>
		<view v-else>
			<no-data-page></no-data-page>
		</view>

	</view>
</template>

<script>
	import context from '../../lib/java110/Java110Context.js';
	import {
		getOwnerVisit
	} from '../../api/visit/visitApi.js'
	import noDataPage from '@/components/no-data-page/no-data-page.vue'
	import {
		getCommunityId
	} from '../../api/community/communityApi.js';

	export default {
		data() {
			return {
				visits: [],
				ownerInfo: {},
				noData: false,
				page: 1,
			}
		},
		components: {
			noDataPage
		},

		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			let _that = this;
			_that._loadVisitList();
		},
		methods: {
			_loadVisitList: function() {
				this.loadingStatus = 'more';
				let _that = this;
				let _objData = {
					page: 1,
					row: 30,
					communityId: getCommunityId()
				}
				getOwnerVisit(_objData).then(function(_visits) {
					_that.visits = _visits;
				})
			},

			_showDetail(_item) {
				uni.navigateTo({
					url: '/pages/visit/visitDetail?vId=' + _item.visitId + '&qrcode=' + _item.qrcode
				})
			}
		}
	}
</script>

<style>
	.solid-bottom::after {
		border-bottom: 2upx solid rgba(0, 0, 0, 0.1);
	}

	.ppf_item {
		padding: 0rpx 0rpx 0rpx 0rpx;
	}

	.block__title {
		display: flex;
		justify-content: space-between;
		margin: 0;
		font-weight: 400;
		font-size: 14px;
		color: rgba(69, 90, 100, .6);
		padding: 40rpx 30rpx 20rpx;
	}

	.button_up_blank {
		height: 40rpx;
	}

	.block__bottom {
		height: 180rpx;
	}
</style>