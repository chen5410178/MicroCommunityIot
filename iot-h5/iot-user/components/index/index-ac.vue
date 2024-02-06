<template>
	<view class="index-undo" v-if="machines && machines.length > 0">
		<view class="undo-title">
			<text class="text-bold">门禁开门</text>
		</view>
		<view class="undo-menu flex justify-start flex-wrap">
			<view class="menu-item" v-for="(item,index) in machines" :key="index" @click="_openAcDoor(item)">
				<view style="position: relative;">
					<image src="/static/images/ac.png"></image>
					<view><text>{{item.machineName}}</text></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {getOwnerAccessControl,ownerOpenAccessControlDoor} from '../../api/accessControl/accessControlApi.js';
	import {getCommunityId} from '../../api/community/communityApi.js';
	export default {
		name: "indexAc",
		data() {
			return {
				machines: []
			};
		},
		methods:{
			_initData:function(){
				let _that = this;
				getOwnerAccessControl({
					page:1,
					row:30,
					communityId:getCommunityId()
				}).then(_json=>{
					_that.machines = _json.data;
				});
			},
			_openAcDoor: function(_machine) {
				ownerOpenAccessControlDoor({
					machineId:_machine.machineId,
					communityId:getCommunityId()
				}).then(_json=>{
					uni.showToast({
						icon:'none',
						title:'开门成功'
					})
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
