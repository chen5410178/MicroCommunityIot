<template>
	<view>
		<view class="margin-top" v-if="rooms.length > 0">
			<view class="bg-white margin-bottom padding-sm margin-sm radius-sm" v-for="(item,index) in rooms"
				:key="index">
				<view class="apply-title flex justify-between">
					<view>
						<text class="text-bold">{{item.roomName}}</text>
					</view>
					<view class="flex justify-start" >
						{{item.stateName}}
					</view>
				</view>
				<view class="apply-content flex justify-start flex-wrap">
					<view class="item">
						<text>所属楼栋:</text>
						<text >{{item.floorNum}}</text>
					</view>
					
					<view class="item">
						<text>所属单元:</text>
						<text>{{item.unitNum}}</text>
					</view>
					<view class="item">
						<text>建筑面积:</text>
						<text>{{item.builtUpArea || '-'}}</text>
					</view>
					<view class="item">
						<text>房屋类型:</text>
						<text>{{item.roomSubTypeName || '-'}}</text>
					</view>
				</view>
			</view>
		</view>
		<view v-else>
			<no-data-page></no-data-page>
		</view>
	</view>
</template>

<script>
	import {queryUserRoom} from '@/api/community/roomApi.js';
	import {getCommunityId} from '@/api/community/communityApi.js';
	import noDataPage from '@/components/no-data-page/no-data-page.vue'
	export default {
		data() {
			return {
				rooms:[]
			}
		},
		onLoad(options) {
			this.loadOwnerRooms();
		},
		components:{
			noDataPage,
		},
		methods: {
			loadOwnerRooms:function(){
				let _that =this;
				queryUserRoom({
					communityId:getCommunityId()
				}).then(_data=>{
					_that.rooms = _data;
				})
			}
		}
	}
</script>

<style lang="scss">
.q-query {
		background-color: #FFF;
		padding: 15upx;

		.q-item {
			width: 30%;
			margin-left: 15upx;
			padding: 10upx 15upx;
			border-radius: 15upx;
			background-color: #f1f1f1;

			.q-input {
				height: 40upx;
			}

			margin-bottom: 15upx;
		}

		.q-item-btn {
			width: 30%;
			margin-left: 15upx;

			.q-input {
				height: 60upx;
			}
		}
	}
	.apply-title {
		height: 60upx;
		line-height: 50upx;
		border-bottom: 1upx solid #F1F1F1;
	}
	
	.apply-content {
		.item {
			width: 50%;
			margin-top: 20upx;
		}
	}
	
	.radius-sm {
		border-radius: 16upx;
	}
</style>

