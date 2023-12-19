<template>
	<view>
		<view class="cu-form-group">
			<view class="title">房屋编号</view>
			<view class="text-right" @click="_chooseRoom()"><text>{{roomName || '请选择 >'}}</text></view>
		</view>
		<view class="cu-form-group">
			<view class="title">人员名称</view>
			<input v-model="personName" placeholder="请输入人员名称" class="text-right"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">联系电话</view>
			<input v-model="personTel" placeholder="请输入联系电话" class="text-right"></input>
		</view>
		<view class="cu-form-group">
			<view class="title">人员身份</view>
			<picker :value="ownerTypeCdIndex" :range="ownerTypeCds" :range-key="'name'" @change="ownerTypeCdChange">
				<view class="picker">
					{{ownerTypeCds[ownerTypeCdIndex].name}}
				</view>
			</picker>
		</view>
		<view class="margin-top">

		</view>
		<vc-upload-async ref="vcUploadRef" :communityId="communityId" title="人脸照片" :maxPhotoNum="maxPhotoNum"
			@sendImagesData="sendImagesData"></vc-upload-async>

		<view class="flex flex-direction margin-top">
			<button class="cu-btn bg-blue margin-tb-sm lg" @click="submitApplyFace()">提交</button>
		</view>
	</view>
</template>

<script>
	import vcUploadAsync from '../vc-upload-async/vc-upload-async.vue';
	import {savePersonFace} from '../../api/owner/ownerApi.js';
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
				ownerTypeCdIndex: 0,
				faceUrl: '',
				openId: '',
				inviteCode:'',
				msgCode:'',
				ownerTypeCds: [{
						name: '请选择',
						value: ''
					},
					{
						name: '业主本人',
						value: '1001'
					},
					{
						name: '家庭成员',
						value: '1002'
					}
				]
			};
		},
		props: {
			communityId: {
				type: String,
			},
			pfqId:{
				type:String,
			},
			openId:{
				type:String,
			}
		},
		components: {
			vcUploadAsync
		},
		methods: {
			ownerTypeCdChange: function(e) {
				this.ownerTypeCdIndex = e.target.value
				this.ownerTypeCd = this.ownerTypeCds[this.ownerTypeCdIndex].value;
			},
			sendImagesData: function(_data) {
				this.faceUrl = _data[0].fileId;
			},
			_chooseRoom: function() {
				uni.navigateTo({
					url: '/pages/room/selectRoom?communityId='+this.communityId
				})
			},
			setRoom: function(_room) {
				this.roomName = _room.roomName;
				this.roomId = _room.roomId;
			},
			submitApplyFace: function() {
				
				let _that = this;

				savePersonFace({
					pfqId:this.pfqId,
					communityId:this.communityId,
					roomName: this.roomName,
					roomId:this.roomId,
					personName: this.personName,
					personTel: this.personTel,
					ownerTypeCd: this.ownerTypeCd,
					faceUrl: this.faceUrl,
					openId: this.openId,
					inviteCode:this.inviteCode,
					msgCode:this.msgCode,
				}).then(_data=>{
					_that.resetData();
				})
			},
			resetData:function(){
				this.maxPhotoNum= 1;
				this.roomName= '';
				this.roomId='';
				this.personName= '';
				this.personTel= '';
				this.ownerTypeCd= '';
				this.ownerTypeCdIndex= 0;
				this.faceUrl= '';
				this.openId= '';
				this.inviteCode='';
				this.msgCode='';
				this.ownerTypeCds= [{
						name: '请选择',
						value: ''
					},
					{
						name: '业主本人',
						value: '1001'
					},
					{
						name: '家庭成员',
						value: '1002'
					}
				];
				
				this.$refs.vcUploadRef.resetPhoto();
			}
		}
	}
</script>

<style>

</style>