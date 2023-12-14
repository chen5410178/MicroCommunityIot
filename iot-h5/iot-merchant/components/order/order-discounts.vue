<template>
	<view>
		<view class="bg-white " v-if="discounts && discounts.length >0">
			<vc-table :isSave="isSave" :tableData="discounts" :title="columns" align="center" titleBg="#eee">
			</vc-table>
		</view>
	</view>
</template>

<script>
	import {
		queryStoreOrderDiscount
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderDiscounts",
		data() {
			return {
				isSave: true,
				columns: ['折扣', '减免','说明'],
				discounts: [],
			};
		},
		created() {
			this._loadDiscountss();
		},
		props: {
			orderId: {
				type: String,
				require: true
			}
		},
		methods: {
			_loadDiscountss: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				if (!_shopId) {
					return;
				}
				queryStoreOrderDiscount(this, {
					page: 1,
					row: 50,
					orderId: this.orderId,
					shopId: this.getCurrentShop().shopId
				}).then(_data => {
					let _datas = _data.data;
					_datas.forEach(_item => {
						_item['折扣'] = _item.discountRate;
						_item['减免'] = _item.discountPrice;
						_item['减免'] = _item.remark;
					});
					this.discounts =_datas;
				});

			}
		}
	}
</script>

<style>

</style>
