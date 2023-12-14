<template>
	<view>
		<view class="cu-bar bg-white solid-bottom ">
			<view class="action">
				<text class="cuIcon-title text-orange "></text>订单信息
			</view>
			<view class="action">
				<button class="cu-btn line-blue shadow margin-right-sm" v-if="orderInfo.state == '0'"
					@tap="_sendOrder()">发货</button>
				<button class="cu-btn line-blue shadow" v-if="orderInfo.state == '8' || orderInfo.state == '2'|| orderInfo.state == '4'"
					@tap="_returnOrder()">退货</button>
			</view>
		</view>
		<view class="cu-list menu">
			<view class="cu-item">
				<view class="title">订单编号</view>
				<text>{{orderInfo.orderId}}</text>
			</view>
			<view class="cu-item">
				<view class="title">平台</view>
				<text>{{orderInfo.appName}}</text>
			</view>
			<view class="cu-item">
				<view class="title">下单时间</view>
				<text>{{orderInfo.createTime}}</text>
			</view>
			<view class="cu-item">
				<view class="title">金额</view>
				<text>{{orderInfo.payPrice}}</text>
			</view>
			<view class="cu-item">
				<view class="title">用户</view>
				<text>{{orderInfo.personName}}</text>
			</view>
			<view class="cu-item">
				<view class="title">状态</view>
				<text>{{orderInfo.stateName}}</text>
			</view>
		</view>
		
		<view class="cu-modal bottom-modal" :class="isReturnFlag?'show':''">
			<view class="cu-dialog">
				<scroll-view scroll-y >
					<form>
						<view class="cu-form-group align-start">
							<view class="title">退货原因</view>
							<textarea placeholder="请输入退货原因" class="text-left" maxlength="200"
								v-model="remark"></textarea>
						</view>
						<view class="padding">
							<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="_doReturnOrder" type="">确认</button>
							<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_doClose"
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
		queryStoreOrder,
		sendStoreOrderCart,
		applyReturnStoreCarts,
		updateStoreOrderCartReturn
	} from '@/api/order/orderApi.js'
	export default {
		name: "orderInfo",
		data() {
			return {
				orderInfo: {},
				isReturnFlag:false,
				remark:''
			};
		},
		created() {
			this._loadOrder();
		},
		props: {
			orderId: {
				type: String,
				require: true
			}
		},
		methods: {
			_loadOrder: function() {
				let _that = this
				let _shopId = this.getCurrentShop().shopId;
				if (!_shopId) {
					return;
				}
				queryStoreOrder(this, {
					page: 1,
					row: 1,
					orderId: this.orderId,
					shopId: this.getCurrentShop().shopId
				}).then(_data => {
					this.orderInfo = _data.data[0]
				})
			},
			_sendOrder:function(){
				sendStoreOrderCart({
					orderId:this.orderId,
					shopId: this.getCurrentShop().shopId
				},this)
				.then(_data=>{
					uni.showToast({
						icon:'none',
						title:'发货成功'
					});
					uni.navigateBack({
						delta:1
					});
				},err=>{
					uni.showToast({
						icon:'none',
						title:err
					});
				})
			},
			_returnOrder:function(){
				this.isReturnFlag=true;
			},
			_doClose:function(){
				this.isReturnFlag = false;
			},
			_doReturnOrder:function(){
				let _that = this;
				applyReturnStoreCarts({
					orderId:this.orderInfo.orderId,
					shopId:this.getCurrentShop().shopId,
					remark:this.remark
				},this).then(_data=>{
					return updateStoreOrderCartReturn({
						orderId:_that.orderInfo.orderId,
						shopId:_that.getCurrentShop().shopId,
						state:1100,
						message:'同意'
					},this);
				},err=>{
					uni.showToast({
						icon:'none',
						title:err
					});
					throw new Error(err)
				}).then(_data=>{
					uni.showToast({
						icon:'none',
						title:'申请成功'
					});
					uni.navigateBack({
						delta:1
					});
				},err=>{
					uni.showToast({
						icon:'none',
						title:err
					});
				})
			}
		}
	}
</script>

<style>

</style>
