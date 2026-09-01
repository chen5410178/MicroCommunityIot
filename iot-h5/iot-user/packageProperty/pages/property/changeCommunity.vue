<template>
	<view>
		<view>
			<view class="padding">小区信息</view>
			<view class="cu-list menu " v-if="communitys.length>0">
				<view class="cu-item" v-for="(item,sub) in communitys" :key="sub" @tap="_doChangeCommunity(item, sub)">
					<view class="content padding-tb-sm">
						<view>
							<text class="lg  cuIcon-homefill text-blue margin-right-xs"></text> {{item.communityName}}
						</view>
						<view class="text-gray text-sm">
							<text class="lg text-gray cuIcon-location margin-right-xs"></text> {{item.address || item.cityName || ''}}
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
		queryCommunitys,
		saveCurrentCommunity
	} from '@/api/community/communityApi.js';
	import chargeStore from '@/api/charge/chargeStore.js';
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
		onShow() {
			this._loadCommunitys();
		},
		methods: {
			// 重新查询所有小区（与首页同源）
			_loadCommunitys: function() {
				queryCommunitys({
					page: 1,
					row: 50
				}).then(res => {
					if (!res || res.code != 0 || !res.data) return
					this.communitys = res.data.map(c => ({
						communityId: c.communityId || '',
						communityName: c.name || c.communityName || '',
						name: c.name || c.communityName || '',
						address: c.address || '',
						cityName: c.cityName || '',
						dist: c.dist || 0
					}))
				}).catch(() => {})
			},
			// 点击小区：缓存所选小区 + 同步首页共享状态 + 返回
			_doChangeCommunity: function(_community, _idx) {
				saveCurrentCommunity(_community)
				chargeStore.communities = this.communitys.slice()
				chargeStore.setCommIdx(_idx >= 0 ? _idx : 0)
				uni.navigateBack({
					delta: 1
				})
			}
		}
	}
</script>

<style>

</style>
