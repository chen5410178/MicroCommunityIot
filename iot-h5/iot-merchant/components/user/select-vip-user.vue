<template>
	<view>
		<view class="cu-modal bottom-modal" :class="isShowSelectVipUser?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white search ">
					<view class="search-form round">
						<text class="cuIcon-search"></text>
						<input type="number" placeholder="输入会员手机号" v-model="name"  confirm-type="search"></input>
					</view>
					<view class="action">
						<button class="cu-btn line-blue shadow-blur round sm margin-right-sm"
							@tap="_searchVipUser()">搜索</button>
						<button class="cu-btn line-blue round sm" @click="_closeVipUser()">关闭</button>
					</view>
				</view>
				<scroll-view scroll-y style="height: 700upx;">
					<block>
						<view>
							<view class="cu-list menu-avatar no-padding">
								<view class="cu-item arrow" v-for="(items,sub) in vipUsers" :key="sub"
									@click="_selectVipUser(items)">
									<view class="cu-avatar round lg">{{items.vipName[0]}}</view>
									<view class="content">
										<view class="text-grey"><text class="text-abc">{{items.vipName}}</text></view>
										<view class="text-gray text-sm flex">
											{{items.vipTel}}
										</view>
									</view>
									<view class="action">
										<button class="cu-btn line-blue round sm">选择</button>
									</view>
								</view>
							</view>
						</view>
					</block>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		queryVipUser
	} from '@/api/user/userApi.js'
	export default {
		name: "selectVipUser",
		data() {
			return {
				vipUsers: [],
				name: '',
			};
		},
		props: {
			isShowSelectVipUser: {
				type: Boolean,
				default: false
			}
		},
		methods: {
			_loadVipUser: function() {
				let _that = this;
				queryVipUser({
						vipTel: this.name,
						shopId: this.getCurrentShop().shopId,
						page: 1,
						row: 100
					}, this)
					.then((data) => {
						_that.vipUsers = data;
					}, err => {

					})
			},
			_searchVipUser: function() {
				this._loadVipUser();
			},
			_selectVipUser: function(_product) {
				this.$emit('selectVipUser', _product)
			},
			_closeVipUser: function() {
				this.$emit('closeVipUser', {})

			}
		}
	}
</script>

<style>

</style>
