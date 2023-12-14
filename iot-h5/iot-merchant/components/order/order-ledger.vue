<template>
	<view>
		<view class="bg-white " >
			<vc-table :isSave="isSave" :tableData="ledgers" :title="columns" align="center" titleBg="#eee">
			</vc-table>
		</view>
	</view>
</template>

<script>
	import {
		listStoreOrderLedgerAcct
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderLedger",
		data() {
			return {
				isSave: true,
				columns: ['商户', '规格','分账金额','充值对象','对象编号'],
				ledgers: [],
			};
		},
		created() {
			this._loadLedgers();
		},
		props: {
			orderId: {
				type: String,
				require: true
			}
		},
		methods: {
			_loadLedgers: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				if (!_shopId) {
					return;
				}
				listStoreOrderLedgerAcct(this, {
					page: 1,
					row: 1,
					orderId: this.orderId,
					shopId: this.getCurrentShop().shopId
				}).then(_data => {
					let _datas = _data.data;
					_datas.forEach(_item => {
						let _feeSpecName = _that.getFeeSpecName(_item);
						let _feeSaveTypeName = _that.getFeeSaveTypeName(_item);
						
						_item['商户'] = _item.storeName;
						_item['规格'] = _feeSpecName;
						_item['分账金额'] = _item.amount;
						_item['充值对象'] = _feeSaveTypeName;
						_item['对象编号'] = _item.saveObjId;
					});
					this.ledgers =_datas;
				});

			},
			getFeeSpecName:function(_item){
				if(_item.feeSpecCd == '1001'){
					return '服务费';
				}
				
				if(_item.feeSpecCd == '2002'){
					return '折扣金';
				}
				
				if(_item.feeSpecCd == '3003'){
					return '商品金';
				}
				return '未知'
			},
			getFeeSaveTypeName:function(_item){
				if(_item.saveType == '45'){
					return '账户充值';
				}
				
				if(_item.saveType == '55'){
					return '优惠券';
				}
				
				return '未知'
			}
		}
	}
</script>

<style>

</style>
