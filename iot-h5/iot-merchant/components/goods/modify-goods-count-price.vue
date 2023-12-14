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
							<view class="title">单价</view>
							<input placeholder="请输入单价" name="input" @blur="_computeMoney"
								v-model="goodsInfo.price"></input>
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
		name: "modifyGoodsCountPrice",
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
				this.goodsInfo = _goodsInfo;
				this._computeMoney();
			},
			_computeMoney: function() {
				let _money = (parseFloat(this.goodsInfo.price) * parseFloat(this.goodsInfo.count));
				this.money = _money;
			},
			_doChange: function() {
				this.$emit('modifyGoodsCountFinish', this.goodsInfo);
				this.isChangeGoodsCount = false;

			},
			_doChangeClose: function() {
				this.isChangeGoodsCount = false;
			},
			_doDeleteGoods:function(){
				this.$emit('deleteGoodsCountFinish', this.goodsInfo);
				this.isChangeGoodsCount = false;
			}
		}
	}
</script>

<style>

</style>
