<template>
	<view>
		<view class="bg-white " >
			<vc-table :isSave="isSave" :tableData="fees" :title="columns" align="center" titleBg="#eee">
			</vc-table>
		</view>
	</view>
</template>

<script>
	import {
		queryStoreOrderCartFee
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderFee",
		data() {
			return {
				isSave: true,
				columns: ['费用名称', '支付方式','支付金额','状态','支付时间'],
				fees: [],
			};
		},
		created() {
			this._loadFees();
		},
		props: {
			orderId: {
				type: String,
				require: true
			}
		},
		methods: {
			_loadFees: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				if (!_shopId) {
					return;
				}
				queryStoreOrderCartFee(this, {
					page: 1,
					row: 1,
					orderId: this.orderId,
					shopId: this.getCurrentShop().shopId
				}).then(_data => {
					let _datas = _data.data;
					_datas.forEach(_item => {
						_item['费用名称'] = _item.feeName;
						_item['支付方式'] = _item.payWayName;
						_item['支付金额'] = _item.payPrice;
						_item['状态'] = _item.state == '1001'?'未支付':'已支付';
						_item['支付时间'] = _item.updateTime;
					});
					this.fees =_datas;
				});

			}
		}
	}
</script>

<style>

</style>
