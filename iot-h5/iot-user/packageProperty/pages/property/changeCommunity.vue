<template>
	<view>
		<view>
			<view class="padding">小区信息</view>
			<view class="cu-list menu " v-if="communitys.length>0">
				<view class="cu-item" v-for="(item,sub) in communitys" :key="sub" @tap="_doChangeCommunity(item)">
					<view class="content padding-tb-sm">
						<view>
							<text class="lg  cuIcon-homefill text-blue margin-right-xs"></text> {{item.communityName}}
						</view>
						<view class="text-gray text-sm">
							<text class="lg text-gray cuIcon-location margin-right-xs"></text> {{item.roomName}}({{item.ownerName}})
						</view>
					</view>
				</view>
			</view>
			<block v-else>
				<view class="cu-list menu">
					<view class="cu-item">
						<view class="content">
							<text class="cuIcon-notification text-grey"></text>
							<text class="text-grey">暂无小区信息</text>
						</view>
						<view class="action">
						</view>
					</view>
				</view>
			</block>
		</view>
	</view>
</template>

<script>
	import {
		getAppUsers
	} from '@/api/user/appUser.js';
	export default {
		data() {
			return {
				communityName: '',
				communitys: []
			}
		},
		onLoad() {
			this._loadCommunitys();
		},
		methods: {
			_loadCommunitys: function() {
				let _that = this;
				getAppUsers().then(_data => {
					_that.communitys = _data;
				})
			},
			_doChangeCommunity: function(_community) {
				uni.setStorageSync("currentCommunityInfo", _community);
				// uni.setStorageSync("ownerInfo",{
				// 	memberId:_community.memberId,
				// 	ownerName:_community.name,
				// 	ownerId:_community.ownerId,
				// 	ownerTel:_community.link,
				// 	communityId:_community.communityId,
				// 	link:_community.link
				// })
				uni.navigateBack({
					delta: 1
				})
			}
		}
	}
</script>

<style>

</style>