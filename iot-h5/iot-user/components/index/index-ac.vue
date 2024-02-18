<template>
	<view class="index-undo">
		<view class="undo-title">
			<text class="text-bold">开门</text>
		</view>
		<view class="undo-menu flex justify-between flex-wrap" v-if="machines && machines.length > 1">
			<view class="menu-item" v-for="(item,index) in machines" :key="index" @click="_openAcDoor(item)">
				<view style="position: relative;">
					<image src="/static/images/ac.png"></image>
					<view><text>{{item.machineName}}</text></view>
				</view>
			</view>
		</view>
		<view class="undo-menu flex justify-between flex-wrap" v-else-if="machines && machines.length == 1">
			<view class="menu-item-one" v-for="(item,index) in machines" :key="index" @click="_openAcDoor(item)">
				<view style="position: relative;">
					<image style="width: 120upx" src="/static/images/ac.png"></image>
					<view><text>{{item.machineName}}</text></view>
				</view>
			</view>
		</view>
		<view class="undo-menu " v-else>
			<view class="menu-item-one">
				<view style="position: relative;">
					<image src="/static/images/nodata.png"></image>
					<view><text>没有门禁</text></view>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		getOwnerAccessControl,
		ownerOpenAccessControlDoor
	} from '../../api/accessControl/accessControlApi.js';
	import {
		getCommunityId
	} from '../../api/community/communityApi.js';
	export default {
		name: "indexAc",
		data() {
			return {
				machines: []
			};
		},
		methods: {
			_initData: function() {
				let _that = this;
				getOwnerAccessControl({
					page: 1,
					row: 30,
					communityId: getCommunityId()
				}).then(_json => {
					_that.machines = _json.data;
				});
			},
			_openAcDoor: function(_machine) {
				ownerOpenAccessControlDoor({
					machineId: _machine.machineId,
					communityId: getCommunityId()
				}).then(_json => {
					uni.showToast({
						icon: 'none',
						title: '开门成功'
					})
				})
			}
		}
	}
</script>

<style lang="scss">
	.index-undo {
		//background-color: #ffffff;

		.undo-title {
			margin-left: 15upx;
			font-size: 36upx;
			color: #323437;
			font-weight: 700;
		}

		.undo-menu {
			margin-top: 10upx;
			.menu-item-one{
				background-color: #FFFFFF;
				border-radius: 10upx;
				width: 100%;
				text-align: center;
				padding: 50upx;
				color: #777777;
				
				image {
					width: 180upx;
					height: 120upx;
				}
			}

			.menu-item {
				background-color: #FFFFFF;
				border-radius: 10upx;
				width: 48%;
				text-align: center;
				padding: 20upx;

				image {
					width: 100upx;
					height: 100upx;
				}
			}
		}
	}
</style>