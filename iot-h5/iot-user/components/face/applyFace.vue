<template>
	<view>
		<view class="cu-form-group">
			<view class="title">房屋编号</view>
			<input v-model="roomName" placeholder="请输入房屋编号(楼栋-单元-房屋)" class="text-right"></input>
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
	export default {
		name: "applyFace",
		data() {
			return {
				maxPhotoNum: 1,
				roomName: '',
				personName: '',
				personTel: '',
				ownerTypeCd: '',
				ownerTypeCdIndex: 0,
				communityId: '',
				faceUrl: '',
				openId: '',
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
		components: {
			vcUploadAsync
		},
		methods: {
			ownerTypeCdChange: function(e) {
				this.ownerTypeCdIndex = e.target.value
				this.ownerTypeCd = this.ownerTypeCds[this.ownerTypeCdIndex].value;
			},
			sendImagesData: function(_data) {
				this.faceUrl = _data.url;
			},
			submitApplyFace:function(){
				
			}
		}
	}
</script>

<style>

</style>