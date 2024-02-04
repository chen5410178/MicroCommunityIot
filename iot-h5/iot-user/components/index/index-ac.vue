<template>
	<view class="index-undo" v-if="machines && machines.length > 0">
		<view class="undo-title">
			<text class="text-bold">门禁开门</text>
		</view>
		<view class="undo-menu flex justify-start flex-wrap">
			<view class="menu-item" v-for="(item,index) in machines" :key="index" @click="_toPage(item)">
				<view style="position: relative;">
					<image :src="item.icon"></image>
					<view><text>{{item.name}}</text></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "index-ac",
		data() {
			return {
				machines: []
			};
		},
		methods:{
			_initData:function(){
				this.machines = [{
					icon: '/static/image/index_apply_audit.png',
					name: '大门',
					url: '/pages/resource/purchaseApplyAuditOrders',
				}, {
					icon: '/static/image/index_itemout_audit.png',
					name: '大门2',
					url: '/pages/resource/itemOutAuditOrders',
				}, {
					icon: '/static/image/index_allocation.png',
					name: '大门3',
					url: '/pages/resource/allocationStorehouseAuditOrders',
				}, {
					icon: '/static/image/index_complaint.png',
					name: '大门4',
					url: '/pages/itemRelease/itemRelease',
				}]
			},
			_toPage: function(_item) {
				if(!this.java110Context.hasPrivilege(_item.pri)){
					uni.showToast({
						icon:'none',
						title:'无权限，联系管理员'
					});
					return ;
				}
				uni.navigateTo({
					url: _item.url
				})
			}
		}
	}
</script>

<style lang="scss">
	.index-undo {
		background-color: #ffffff;

		.undo-title {
			padding: 20upx;
			border-bottom: 1upx solid #F1F1F1;
		}
		
		.undo-menu{
			padding:20upx;
			.menu-item{
				
				width: 33.33%;
				padding: 30upx;
				text-align: center;
				image{
					width: 80upx;
					height: 80upx;
				}
				border-bottom: 1upx solid #F1F1F1;
				border-right: 1upx solid #F1F1F1;
			}
			.menu-item:nth-child(3n+3) {
			    border-right: none;
			}
			.menu-item:nth-child(n+4) {
				border-bottom: none;
			}
		}
	}
</style>
