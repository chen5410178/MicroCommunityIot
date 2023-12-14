<template>
	<view>
		<view class="bg-white " >
			<vc-table :isSave="isSave" :tableData="events" :title="columns" align="center" titleBg="#eee">
			</vc-table>
		</view>
	</view>
</template>

<script>
	import {
		queryStoreOrderCartEvent
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderEvent",
		data() {
			return {
				isSave: true,
				columns: ['操作时间', '说明'],
				events: [],
			};
		},
		created() {
			this._loadEvents();
		},
		props: {
			orderId: {
				type: String,
				require: true
			}
		},
		methods: {
			_loadEvents: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				if (!_shopId) {
					return;
				}
				queryStoreOrderCartEvent(this, {
					page: 1,
					row: 50,
					orderId: this.orderId,
					shopId: this.getCurrentShop().shopId
				}).then(_data => {
					let _datas = _data.data;
					_datas.forEach(_item => {
						_item['操作时间'] = _item.createTime;
						_item['说明'] = _item.eventMsg;
					});
					this.events =_datas;
				});

			}
		}
	}
</script>

<style>

</style>
