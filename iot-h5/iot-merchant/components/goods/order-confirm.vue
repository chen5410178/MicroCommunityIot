<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isOrderConfirmCount?'show':''">
			<view class="cu-dialog">
				<scroll-view scroll-y style="height: 800upx;">
					<form>
						<view class="cu-form-group">
							<view class="title">会员</view>
							<text>{{vipUser.vipTel?vipUser.vipTel:'无'}}</text>
						</view>
						<view class="cu-form-group">
							<view class="title">应收金额</view>
							<text>{{totalMoney}}</text>
						</view>
						<view class="cu-form-group">
							<view class="title">折扣</view>
							<input placeholder="请输入折扣" class="text-right" name="input" @blur="_computeMoney"
								v-model="goodsDiscount"></input>
						</view>
						<view class="cu-form-group">
							<view class="title">减免</view>
							<input placeholder="请输入减免" class="text-right" name="input" @blur="_computeMoney"
								v-model="goodsReduction"></input>
						</view>
						<view class="cu-form-group">
							<view class="title">实收金额</view>
							<input placeholder="请输入数量" class="text-right" name="input" @blur="_computeMoney"
								v-model="money"></input>
						</view>
						<view class="padding-sm">
							<button class="cu-btn block bg-green margin-tb-sm lg" @tap="_payQrCodeMoney"
								type="">扫码支付</button>
							<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="_payMoney" type="">现金支付</button>
							<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_doChangeClose"
								type="">关闭</button>
						</view>
					</form>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		goodFaceToFaceCashierToPay,goodCashierToPay,checkGoodCashierToPay
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderConfirm",
		data() {
			return {
				money: 0,
				totalMoney: 0,
				isOrderConfirmCount: false,
				goodsDiscount: 1,
				goodsReduction: 0,
				storeOrderCarts: [],
				vipUser: {},
				checkOrderInfo:{}
			};
		},
		created() {
			uni.$on('qrCodeBarCode', (data) => {
				this._doPayQrCodeMoney(data.result);
			})
		},
		methods: {
			initStoreOrder: function(_storeOrderCarts, _vipUser) {
				this.isOrderConfirmCount = true;
				uni.$emit('qrcode',true);
				this.storeOrderCarts = _storeOrderCarts;
				this.vipUser = _vipUser;
				this._cumputeTotalMoney();
			},
			_computeMoney: function() {
				let _money = parseFloat(this.totalMoney);
				let _mulMoney = (_money - _money * parseFloat(this.goodsDiscount)) + parseFloat(this
					.goodsReduction);
				_money = (_money - _mulMoney).toFixed(2);
				this.money = _money;
			},
			_cumputeTotalMoney: function() {
				let _totalMoney = 0;
				this.storeOrderCarts.forEach(item => {
					_totalMoney += parseFloat(item.money);
				})
				this.totalMoney = _totalMoney.toFixed(2);
			},
			_getPayParam: function() {
				let _storeOrderCarts = [];
				this.storeOrderCarts.forEach(item => {
					_storeOrderCarts.push({
						"cateType": "1",
						"productId": item.productId,
						"prodName": item.prodName,
						"valueId": item.valueId,
						"goodsNum": item.count,
						"storeId": this.getCurrentShop().storeId,
						"shopId": this.getCurrentShop().shopId,
						"discountType": "1001",
						"discountRate": item.goodsDiscount,
						"discountPrice": item.goodsReduction,
						"cartType": "3306"
					})
				})
				return {
					"storeOrder": {
						"personId": "-1",
						"personName": "",
						"payPrice": this.money,
						"discountType": "1001",
						"discountRate": this.goodsDiscount,
						"discountPrice": this.goodsReduction
					},
					"storeOrderCarts": _storeOrderCarts,
					"vipUsers": this.vipUser,
					"shopId": this.getCurrentShop().shopId
				}
			},
			_payMoney: function() {
				this._computeMoney();
				// this.$emit('modifyGoodsCountFinish', this.goodsInfo);
				// this.isChangeGoodsCount = false;
				goodFaceToFaceCashierToPay(this._getPayParam(), this)
					.then(_data => {
						uni.showToast({
							icon: 'none',
							title: '成功'
						});
						 this.isOrderConfirmCount = false;
						 uni.$emit('qrcode',false);
						this.$emit('notifyPayResult', {
							result: true
						})
					}, err => {
						this.isOrderConfirmCount = false;
						uni.$emit('qrcode',false);
						uni.showToast({
							icon: 'none',
							title: err
						});
						
					})
			},
			_payQrCodeMoney: function() {
				this._computeMoney();
				uni.navigateTo({
					url: '/pages/plusScan/index?type=qrCodeBarCode',
				});
			},
			_doPayQrCodeMoney: function(_code) {
				let _that = this;
				this._computeMoney();
				let _param = this._getPayParam()
				_param.authCode=_code;
				goodCashierToPay(_param, this)
					.then(_data => {
						uni.showToast({
							icon: 'none',
							title: '成功'
						});
						 this.isOrderConfirmCount = false;
						 uni.$emit('qrcode',false);
						this.$emit('notifyPayResult', {
							result: true
						})
					}, orderInfo => {
						this.checkOrderInfo = orderInfo;
						setTimeout(function(){
							_that._checkGoodCashierToPay();
						},5000)
					})
				// this.$emit('modifyGoodsCountFinish', this.goodsInfo);
				// this.isChangeGoodsCount = false;
			},
			_checkGoodCashierToPay:function(){
				let _that = this
				checkGoodCashierToPay(this.checkOrderInfo, this)
					.then(_data => {
						uni.showToast({
							icon: 'none',
							title: '成功'
						});
						 this.isOrderConfirmCount = false;
						 uni.$emit('qrcode',false);
						this.$emit('notifyPayResult', {
							result: true
						})
					}, orderInfo => {
						setTimeout(function(){
							_that._checkGoodCashierToPay();
						},5000)
					})
			},
			_doChangeClose: function() {
				this.isOrderConfirmCount = false;
				 uni.$emit('qrcode',false);
				this.$emit('notifyPayResult', {result:false})
			}
		}
	}
</script>

<style>

</style>
