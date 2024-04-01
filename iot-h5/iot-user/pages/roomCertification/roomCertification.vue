<template>
	<view>
		<view class="cu-form-group">
			<view class="title">选择小区</view>
			<picker :value="communityIndex" :range="communitys" :range-key="'communityName'" @change="_chooseCommunity">
				<view class="picker">
					{{communitys[communityIndex].communityName}}
				</view>
			</picker>
		</view>
		<view class="cu-form-group">
			<view class="title">房屋编号</view>
			<view class="text-right" @click="_chooseRoom()"><text>{{roomName || '请选择 >'}}</text></view>
		</view>
		<view class="cu-form-group">
			<view class="title">业主名称</view>
			<input v-model="personName" placeholder="请输入人员名称" class="text-right"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">业主手机号</view>
			<input v-model="personTel" placeholder="请输入联系电话" class="text-right"></input>
		</view>
		<view class="margin-top">

		</view>
		<!-- <vc-upload-async ref="vcUploadRef" :communityId="communityId" title="人脸照片" :maxPhotoNum="maxPhotoNum"
			@sendImagesData="sendImagesData"></vc-upload-async> -->

		<view class="flex flex-direction margin-top">
			<button class="cu-btn bg-blue margin-tb-sm lg" @click="submitApplyFace()">提交</button>
		</view>
	</view>
</template>

<script>
	import vcUploadAsync from '../../components/vc-upload-async/vc-upload-async.vue';
	import {saveAppUser} from '../../api/owner/ownerApi.js';
	import {queryCommunitys} from '../../api/community/communityApi.js';
	import {checkPhoneNumber,checkStrLength} from '../../lib/java110/utils/StringUtil.js';
	import {getUserTel} from '../../api/user/userApi.js'
	export default {
		name: "applyFace",
		data() {
			return {
				maxPhotoNum: 1,
				roomName: '',
				roomId:'',
				personName: '',
				personTel: '',
				ownerTypeCd: '',
				faceUrl: '',
				openId: '',
				inviteCode:'',
				msgCode:'',
				
				communitys: [{
					communityId: '0',
					communityName: 'xx小区'
				}],
				communityIndex: 0,
				communityId: "0",
				communityName: "",
			};
		},
		components: {
			vcUploadAsync
		},
	
		methods: {
			sendImagesData: function(_data) {
				this.faceUrl = _data[0].fileId;
			},
			_chooseRoom: function() {
				uni.navigateTo({
					url: '/pages/room/selectRoom?communityId='+this.communityId
				})
			},
			_chooseCommunity: function(e) {
				this.communityIndex = e.target.value
				this.communityId = this.communitys[this.communityIndex].communityId;
				this.communityName = this.communitys[this.communityIndex].communityName;
			},
			setRoom: function(_room) {
				this.roomName = _room.roomName;
				this.roomId = _room.roomId;
			},
			submitApplyFace: function() {
				let _that = this;
				
				let obj = {
					memberId: '-1',
					roomId: this.roomId,
					roomName: this.roomName,
					link: this.personTel,
					ownerName: this.personName,
					communityId: this.communityId,
					communityName: this.communityName,
					faceUrl: this.faceUrl
				};
				
				let msg = "";
				if (obj.roomName == "") {
					msg = "请选择房屋编号";
				} else if (obj.ownerName == "") {
					msg = "请输入人员名称";
				} else if (checkStrLength(obj.ownerName) > 64) {
					msg = "人员名称过长";
				} else if (obj.phoneNumber == "") {
					msg = "请填写手机号";
				} else if (!checkPhoneNumber(obj.link)) {
					msg = "手机号有误";
				// } else if (obj.faceUrl == "") {
				// 	msg = "请上传人脸照片";
				}else{
				}
				
				if (msg != "") {
					wx.showToast({
						title: msg,
						icon: 'none',
						duration: 2000
					});
					return;
				}

				saveAppUser(obj).then(function(data) {
					if (data.code == 0) {
						//_that.resetData();
						wx.showToast({
							title: '提交成功',
							icon: 'none',
							duration: 2000
						});
						uni.navigateBack()
					}
				})
			},
			resetData:function(){
				this.maxPhotoNum= 1;
				this.roomName= '';
				this.roomId='';
				this.personName= '';
				this.personTel= '';
				this.ownerTypeCd= '';
				this.faceUrl= '';
				this.openId= '';
				this.inviteCode='';
				this.msgCode='';
				
				this.communitys= [{
					communityId: '0',
					communityName: 'xx小区'
				}];
				this.communityIndex= 0;
				this.communityId= "0";
				this.communityName= "";
			},
			_loadCommunitys: function() {
				let _that = this;
				queryCommunitys({
					page: -1,
					row: 0,
				}).then((_communitys) => {
					if(_communitys.code == 0) {
						_that.communitys = _communitys.data.map(item => {
							return {
								communityId: item.communityId,
								communityName: item.communityName,
							};
						});
						_that.communityId = _communitys.data[0].communityId;
						_that.communityName = _communitys.data[0].communityName;
					}
				})
			},
		},
		onLoad: function(options) {
			this._loadCommunitys();
			this.personTel = getUserTel();
		},
		onShow: function() {
			let _selectRoom = uni.getStorageSync('selectRoom');
			if(_selectRoom){
				uni.removeStorageSync('selectRoom');
				this.roomId = _selectRoom.roomId;
				this.roomName = _selectRoom.roomName;
			}
		},
	}
</script>

<style>

</style>