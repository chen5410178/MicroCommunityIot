<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isAssetInventoryGoodsCount?'show':''">
			<view class="cu-dialog">
				<scroll-view scroll-y style="height: 800upx;">
					<form>
						<view class="cu-form-group">
							<view class="title">商品</view>
							<text>{{goodsInfo['商品']}}</text>
						</view>
						<view class="cu-form-group">
							<view class="title">账面数量</view>
							<text>{{goodsInfo['账面数量']}}</text>
						</view>
						<view class="cu-form-group">
							<view class="title">盘点数量</view>
							<input placeholder="请输入盘点数量" name="input" 
								v-model="goodsInfo.invQuantity"></input>
						</view>
						<view class="cu-form-group">
							<view class="title">耗损</view>
							<input placeholder="请输入耗损" name="input" 
								v-model="goodsInfo.attrition"></input>
						</view>
						<view class="padding">
							<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="_doChange" type="">确认</button>
							<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_doDeleteGoods"
								type="">删除</button>
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
		name: "assetInventoryGoods",
		data() {
			return {
				isAssetInventoryGoodsCount: false,
				goodsInfo: {}
			};
		},
		created() {

		},
		methods: {
			_doModify: function(_goodsInfo) {
				this.isAssetInventoryGoodsCount = true;
				this.goodsInfo = _goodsInfo;
			},
			_doChange: function() {
				this.$emit('assetInventoryGoodsFinish', this.goodsInfo);
				this.isAssetInventoryGoodsCount = false;
			},
			_doChangeClose: function() {
				this.isAssetInventoryGoodsCount = false;
			},
			_doDeleteGoods: function() {
				this.$emit('assetInventoryGoodsDelete', this.goodsInfo);
				this.isAssetInventoryGoodsCount = false;
			}
		}
	}
</script>

<style>

</style>
