<template>
	<view>
		<view class="margin-top">
			<view class="cu-list menu">
				<view class="cu-item arrow" @tap="_printSetting()">
					<view class="content">
						<text class="lg text-gray cuIcon-friendaddfill"></text>
						<text class="text-grey">蓝牙打印机</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			}
		},
		methods: {
			_printSetting:function(){
				uni.navigateTo({
					url:"/pages/settings/printSettings"
				});
			},
		}
	}
</script>

<style>

</style>
