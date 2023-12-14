<template>
	<view>
		<view class="cu-modal" :class="isChange?'show':''">
			<view class="cu-dialog">
				<form>
					<view class="cu-form-group">
						<view class="title">规格</view>
						<text>{{curProductSpec.specName}}</text>
					</view>
					<view class="cu-form-group">
						<view class="title">库存</view>
						<input placeholder="请输入库存" name="input" type="number" v-model="curProductSpec.stock"></input>
					</view>
					<view class="cu-form-group">
						<view class="title">价格/积分</view>
						<input placeholder="请输入价格/积分" name="input" type="number" v-model="curProductSpec.price"></input>
					</view>
					<view class="cu-form-group">
						<view class="title">业主价</view>
						<input placeholder="请输入业主价" name="input" type="number" v-model="curProductSpec.ownerPrice"></input>
					</view>
					<view class="cu-form-group">
						<view class="title">会员价</view>
						<input placeholder="请输入会员价" name="input" type="number" v-model="curProductSpec.vipPrice"></input>
					</view>
					<view class="cu-form-group">
						<view class="title">折扣金</view>
						<input placeholder="请输入折扣金" name="input" type="number" v-model="curProductSpec.deduction"></input>
					</view>
					<view class="cu-form-group">
						<view class="title">条形码</view>
						<input placeholder="请输入条形码" name="input" type="number" v-model="curProductSpec.barCode"></input>
						<view class="cu-capsule radius">
							<view class="cu-tag bg-blue" @click="_qrCodeBarCode">
								扫码
							</view>
						</view>
					</view>
					<view class="padding">
						<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="_doChange" type="">确认</button>
						<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_doChangeClose" type="">关闭</button>
						<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_delete" type="">删除</button>
					</view>
				</form>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "changeProductSpec",
		data() {
			return {

			};
		},
		props: {
			isChange: {
				type: Boolean,
				default: false
			},
			curProductSpec: {
				type: Object
			}
		},
		created() {
			uni.$on('qrCodeBarCode', (data) => {
				this.curProductSpec.barCode = data.result
			})
		},
		methods: {
			_doChange: function() {
				this.$emit('doChange', this.curProductSpec);
			},
			_delete: function() {
				this.$emit('deleteProductSpec', this.curProductSpec);
			},
			_qrCodeBarCode: function() {
				uni.navigateTo({
					url: '/pages/plusScan/index?type=qrCodeBarCode',
				});
			},
			_doChangeClose:function(){
				this.$emit('changeClose', this.curProductSpec);
			}
		}
	}
</script>

<style>

</style>
