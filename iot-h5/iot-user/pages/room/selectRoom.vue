<template>
	<view>
		<view class="cu-form-group">
			<view class="title">选择楼栋</view>
			<picker :value="floorIndex" :range="floors" :range-key="'name'" @change="floorChange">
				<view class="picker">
					{{floors[floorIndex].name}}
				</view>
			</picker>
		</view>
		<view class="cu-form-group" v-if="units && units.length>0">
			<view class="title">选择单元</view>
			<picker :value="unitIndex" :range="units" :range-key="'name'" @change="unitChange">
				<view class="picker">
					{{units[unitIndex].name}}
				</view>
			</picker>
		</view>
	</view>
</template>

<script>
	import {queryFloors,queryUnits} from '../../api/community/communityApi.js';
	export default {
		data() {
			return {
				communityId:'',
				floors:[],
				floorIndex:0,
				floorId:'',
				units:[],
				unitIndex:0,
				unitId:''
			}
		},
		onLoad(options) {
			this.communityId = options.communityId;
		},
		methods: {
			_loadFloors:function(){
				let _that =this;
				queryFloors({
					page:1,
					row:50,
					communityId:this.communityId,
				}).then((_floors)=>{
					_that.floors = _floors;
				})
			},
			_loadUnits:function(){
				let _that =this;
				queryUnits({
					page:1,
					row:50,
					communityId:this.communityId,
					unitId:this.unitId
				}).then((_units)=>{
					_that.units = _units;
				})
			},
			floorChange: function(e) {
				this.floorIndex = e.target.value
				this.floorId = this.floors[this.floorIndex].value;
				this._loadUnits();
			},
			unitChange: function(e) {
				this.unitIndex = e.target.value
				this.unitId = this.units[this.unitIndex].value;
			},
		}
	}
</script>

<style>

</style>
