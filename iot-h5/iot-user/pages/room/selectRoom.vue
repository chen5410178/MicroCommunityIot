<template>
	<view>
		<view class="cu-form-group">
			<view class="title">选择楼栋</view>
			<picker :value="floorIndex" :range="floors" :range-key="'floorNum'" @change="floorChange">
				<view class="picker">
					{{floorIndex == -1 ? '请选择' :floors[floorIndex].floorNum}}
				</view>
			</picker>
		</view>
		<view class="cu-form-group">
			<view class="title">选择单元</view>
			<picker :value="unitIndex" :range="units" :range-key="'unitNum'" @change="unitChange">
				<view class="picker">
					{{unitIndex == -1 ? '请选择' :units[unitIndex].unitNum}}
				</view>
			</picker>
		</view>

		<view class="cu-list menu margin-top">
			<view class="cu-item" v-for="(room, idx) in rooms" :key="idx">
				<view class="content padding-tb-sm" is-link @click="chooseRoom(room)">
					<view>
						<text class="cuIcon-clothesfill text-blue margin-right-xs"></text> {{room.roomName}}
					</view>
					<view class="text-gray text-sm">
						<text class="cuIcon-infofill margin-right-xs"></text>{{room.roomId}}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		queryFloors,
		queryUnits,
		queryRoomsByApp
	} from '../../api/community/communityApi.js';
	export default {
		data() {
			return {
				communityId: '',
				floors: [],
				floorIndex: -1,
				floorId: '',
				units: [],
				unitIndex: -1,
				unitId: '',
				rooms: [],
			}
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this._loadFloors();
		},
		methods: {
			_loadFloors: function() {
				let _that = this;
				queryFloors({
					page: 1,
					row: 50,
					communityId: this.communityId,
				}).then((_floors) => {
					_that.floors = _floors;
				})
			},
			_loadUnits: function() {
				let _that = this;
				queryUnits({
					page: 1,
					row: 50,
					communityId: this.communityId,
					floorId: this.floorId
				}).then((_units) => {
					_that.units = _units;
				})
			},
			_loadRooms: function() {
				let _that = this;
				queryRoomsByApp({
					page: 1,
					row: 200,
					communityId: this.communityId,
					unitId: this.unitId
				}).then((_rooms) => {
					_that.rooms = _rooms;
				})
			},
			floorChange: function(e) {
				this.floorIndex = e.target.value
				this.floorId = this.floors[this.floorIndex].floorId;
				this._loadUnits();
			},
			unitChange: function(e) {
				this.unitIndex = e.target.value
				this.unitId = this.units[this.unitIndex].unitId;
				this._loadRooms();
			},
			chooseRoom: function(_room) {
				uni.setStorageSync("selectRoom", _room);
				uni.navigateBack();
			}
		}
	}
</script>

<style>

</style>