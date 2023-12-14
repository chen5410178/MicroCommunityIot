<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isChangeGoodsCount?'show':''">
			<view class="cu-dialog">
				<scroll-view scroll-y style="height: 800upx;">
					<form>
						<view class="cu-form-group">
							<view class="title">商品</view>
							<text>{{goodsInfo['商品']}}</text>
						</view>
						<view class="cu-form-group">
							<view class="title">数量</view>
							<input placeholder="请输入数量" name="input" @blur="_computeMoney"
								v-model="goodsInfo.count"></input>
						</view>
						<view class="cu-form-group">
							<view class="title">折扣</view>
							<input placeholder="请输入折扣" name="input" @blur="_computeMoney"
								v-model="goodsInfo.goodsDiscount"></input>
						</view>
						<view class="cu-form-group">
							<view class="title">减免</view>
							<input placeholder="请输入减免" name="input" @blur="_computeMoney"
								v-model="goodsInfo.goodsReduction"></input>
						</view>
						<view class="cu-form-group">
							<view class="title">小计</view>
							<text>{{money}}</text>
						</view>
						<view class="padding">
							<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="_doChange" type="">确认</button>
							<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_doDeleteGoods" type="">删除</button>
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
	export default {
		name: "modifyGoodsCount",
		data() {
			return {
				money: 0,
				isChangeGoodsCount: false,
				goodsInfo: {}
			};
		},
		created() {

		},
		methods: {
			_doModify: function(_goodsInfo) {
				this.isChangeGoodsCount = true;
				uni.$emit('qrcode',true);
				this.goodsInfo = _goodsInfo;
				this._computeMoney();
			},
			_computeMoney: function() {
				let _money = (parseFloat(this.goodsInfo.price) * parseFloat(this.goodsInfo.count));
				let _mulMoney = (_money - _money * parseFloat(this.goodsInfo.goodsDiscount)) + parseFloat(this
					.goodsInfo.goodsReduction);
				_money = (_money - _mulMoney).toFixed(2);
				this.money = _money;
			},
			_doChange: function() {
				this.$emit('modifyGoodsCountFinish', this.goodsInfo);
				this.isChangeGoodsCount = false;
				uni.$emit('qrcode',false);

			},
			_doChangeClose: function() {
				this.isChangeGoodsCount = false;
				uni.$emit('qrcode',false);
			},
			_doDeleteGoods:function(){
				this.$emit('deleteGoodsCountFinish', this.goodsInfo);
				this.isChangeGoodsCount = false;
				uni.$emit('qrcode',false);
			}
		}
	}
</script>

<style>

</style>
