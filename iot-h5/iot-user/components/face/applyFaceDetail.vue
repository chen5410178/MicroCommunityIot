<template>
	<view>
		<view v-for="(item,index) in feeDetails" :key="index"
			class="bg-white margin-bottom-xs margin-right-xs radius margin-left-xs padding">
			<view class="flex padding-bottom-xs solid-bottom justify-between">
				<view>{{item.feeName}}</view>
				<view class="text-gray">{{item.createTime}}</view>
			</view>
			<view class="flex margin-top justify-between">
				<view class="text-gray">收费对象</view>
				<view class="text-gray">{{item.objName}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">缴费编码</view>
				<view class="text-gray">{{item.detailId}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">支付方式</view>
				<view class="text-gray">{{item.primeRate}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">起始时间</view>
				<view class="text-gray">{{item.startTime}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">结束时间</view>
				<view class="text-gray">{{item.endTime}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">缴费金额</view>
				<view class="text-gray">{{item.receivedAmount}}元</view>
			</view>
			<view class="flex margin-top-xs justify-between" v-if="item.preDegrees">
				<view class="text-gray">上期度数</view>
				<view class="text-gray">{{item.preDegrees}}</view>
			</view>
			<view class="flex margin-top-xs justify-between" v-if="item.curDegrees">
				<view class="text-gray">本期度数</view>
				<view class="text-gray">{{item.curDegrees}}</view>
			</view>
			<view class="flex margin-top-xs justify-between" v-if="item.preDegrees">
				<view class="text-gray">使用量</view>
				<view class="text-gray">{{item.curDegrees-item.preDegrees}}</view>
			</view>
			<view class="flex margin-top-xs justify-between">
				<view class="text-gray">备注</view>
				<view class="text-gray">{{item.remark}}</view>
			</view>
		</view>
		<view v-if="!feeDetails || feeDetails.length < 1">
			<no-data-page></no-data-page>
		</view>
	</view>
</template>

<script>
	import noDataPage from '../no-data-page/no-data-page.vue';
	export default {
		name:"applyFaceDetail",
		data() {
			return {
				feeDetails:[]
			};
		},
		components:{
			noDataPage
		},
		methods:{
			loadApplyFaceDetail:function(){
				
			}
		}
	}
</script>

<style>

</style>