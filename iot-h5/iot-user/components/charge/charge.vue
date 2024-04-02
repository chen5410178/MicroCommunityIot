<template>
	<view>
		<view class="cu-list menu">
			<view class="cu-item arrow" @click="_toChargeCard()">
				<view class="content padding-tb-sm">
					<view>
						<text class="cuIcon-rechargefill text-green margin-right-xs"></text> 充电月卡
					</view>
					<view class="text-gray text-sm">
						<text class="cuIcon-right margin-right-xs"></text> {{ chargeOrders.length}} 张
					</view>
				</view>
				<view class="action">
					去购买
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getChargeMonthOrder
	} from '@/api/machine/machineApi.js';
	import {
		formatDate,formatTimeNow
	} from '../../lib/java110/utils/DateUtil.js';
	import {
		getUserTel
	} from '../../api/user/userApi.js'
	export default {
		name: "charge",
		data() {
			return {
				chargeOrders: [],
				communityId: ''
			};
		},
		created() {
			//this.loadOwnerAccount();
		},
		methods: {
			loadChargeMonthOrder: function(_communityId) {
				this.communityId = _communityId;
				let _that = this;

				getChargeMonthOrder({
					page: 1,
					row: 20,
					personTel: getUserTel(),
					communityId: _communityId,
					queryTime: formatTimeNow()
				}).then((data) => {
					if (!data) {
						_that.chargeOrders = [];
						return;
					}
					_that.chargeOrders = data;
				})
			},
			_toChargeCard: function() {
				uni.navigateTo({
					url: '/pages/charge/chargeCard?communityId=' + this.communityId
				})
			}
		},
	}
</script>

<style>

</style>