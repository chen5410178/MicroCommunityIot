<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isShowOrderUp?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white ">
					<view class="action">
						<text class="cuIcon-title text-orange "></text>挂单订单
					</view>
					<view class="action">
						<button class="cu-btn line-blue round sm margin-right-sm" @click="_doOrderUp()">挂单</button>
						<button class="cu-btn line-blue round sm" @click="_closeOrderUps()">关闭</button>
					</view>
				</view>
				<scroll-view scroll-y style="height: 700upx;">
					<block>
						<view>
							<view class="cu-list menu no-padding">
								<view class="cu-item " v-for="(items,sub) in orderUps" :key="sub"
									@click="_selectOrderUps(items)">
									<view class="">
										<view class="text-grey"><text
												class="text-abc">{{items.orderUpId}}({{items.totalPrice}})</text></view>
										<view class="text-gray text-sm">
											{{items.personName}}>{{items.createTime}}
										</view>
									</view>
									<view class="action">
										<button class="cu-btn line-blue round sm">选择</button>
									</view>
								</view>
							</view>
						</view>
					</block>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		queryStoreOrderUp,saveStoreOrderUp,queryStoreOrderCartUp
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderUp",
		data() {
			return {
				isShowOrderUp: false,
				orderUps: [],
				name: '',
				carts: []
			};
		},
		methods: {
			_loadOrderUps: function() {
				let _that = this;
				queryStoreOrderUp({
						shopId: this.getCurrentShop().shopId,
						page: 1,
						row: 100
					}, this)
					.then((data) => {
						_that.orderUps = data.data;
					}, err => {

					})
			},
			showOrderUp: function(_carts) {
				this.carts = _carts;
				this._loadOrderUps();
				this.isShowOrderUp = true;
				uni.$emit('qrcode',true);
			},
			// _selectOrderUps: function(_product) {
			// 	this.$emit('selectOrderUps', _product)
			// },
			_closeOrderUps: function() {
				this.isShowOrderUp = false;
				uni.$emit('qrcode',false);
			},
			_doOrderUp: function() {
				this.isShowOrderUp = false;
				if (!this.carts || this.carts.length < 1) {
					uni.showToast({
						icon: 'none',
						title: '没有需要挂的商品'
					})
					return;
				}
				let _totalPrice = 0;
				let _productStocks=[]
				this.carts.forEach(_item=>{
					_totalPrice += parseFloat(_item.money);
					_productStocks.push({
						"cateType": "1",
						"productId": _item.productId,
						"valueId": "912022030834420169",
						"prodName": _item.prodName,
						"prodPrice": _item.money,
						"count": _item.count,
						"specName": _item.specName,
						"discountType": "1001",
						"discountRate": _item.goodsDiscount,
						"discountPrice": _item.goodsReduction,
						"storeId": this.getCurrentShop().storeId,
						"shopId": this.getCurrentShop().shopId,
						"valueId":_item.valueId
					})
				})
				saveStoreOrderUp({
						"storeOrderUp": {
							"vipId": "-1",
							"totalPrice": _totalPrice.toFixed(2),
							"state": "0",
							"shopId": this.getCurrentShop().shopId,
							"orderUpId": ""
						},
						"productStocks": _productStocks
					}, this)
					.then(_data => {
						uni.showToast({
							icon: 'none',
							title: '挂单成功'
						});
						this.carts=[];
						this.isShowOrderUp = false;
						uni.$emit('qrcode',false);
						this.$emit('selectOrderUps',this.carts)
					}, err => {
						uni.showToast({
							icon: 'none',
							title: err
						})
					})
			},
			_selectOrderUps:function(_item){
				this.carts = [];
				queryStoreOrderCartUp({
					page:1,
					row:50,
					shopId:this.getCurrentShop().shopId,
					orderUpId:_item.orderUpId
				},this).then(_data=>{
					_data.data.forEach(data=>{
						let _money = (parseFloat(data.price) * parseFloat(data.count));
						let _mulMoney = (_money - _money * parseFloat(data.discountRate)) + parseFloat(data.discountPrice);
						_money = (_money-_mulMoney).toFixed(2)
						this.carts.push({
							'商品': data.prodName + "(" + data.specName + ")",
							'单价': data.price,
							'数量': data.count,
							'小计': _money,
							'productId': data.productId,
							'specId': data.specId,
							'price': data.price,
							'count': data.count,
							'goodsDiscount':data.discountRate,
							'goodsReduction':data.discountPrice,
							'money': _money,
							'prodName':data.prodName,
							'valueId':data.valueId,
							'specName':data.specName
						});
					})
					this.isShowOrderUp = false;
					uni.$emit('qrcode',false);
					this.$emit('selectOrderUps',this.carts)
					
				})
			}
		}
	}
</script>

<style>
.cu-modal{
	z-index: 1500;
}
</style>
