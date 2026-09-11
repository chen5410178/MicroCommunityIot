<template>
	<view>
		
	</view>
</template>

<script>
	import {
		isNotNull
	} from '../../lib/java110/utils/StringUtil.js';
	import {
		refreshUserOpenId,
		getWechatMiniOpenId,
		getCommunityWechatAppId,
		refreshAppUserBindingOwnerOpenId
	} from '../../api/user/userApi.js';
	import {
		isWxOrAli
	} from '../../lib/java110/utils/EnvUtil.js';
	import {
		takeTargetUrl,
		navigateToTargetUrl
	} from '../../lib/java110/utils/TargetUrlUtil.js';
	export default {
		data() {
			return {
				communityId:'',
				appId:'',
				openId:'',
			}
		},
		onLoad(options) {
			this.communityId = options.communityId;
			this.openId = options.openId;
			let _that = this;
			if(this.openId){
				this.saveOpenIdToOwner();
				return;
			}
			this._loadAppId(function(){
				if(!_that.appId || _that.appId.indexOf('wx') < 0){
					_that.navPage();
				}
				//todo 查询openId
				_that._generatorOpenId();
			});
			
		},
		methods: {
			_loadAppId:function(_call){
				let _objType = "1100"; // todo public
				// #ifdef MP-WEIXIN
				_objType = "1000";
				// #endif
				let _that = this;
				getCommunityWechatAppId({
					communityId:this.communityId,
					objType:_objType
				}).then(_data =>{
					_that.appId = _data.data;
					_call();
				})
			},
			_generatorOpenId:function(){
				// #ifdef H5
				//todo h5 情况
				this._refreshWechatOpenId();
				return;
				// #endif
								
				// #ifdef MP-WEIXIN
				this._refreshWechatMiniOpenId();
				// #endif
			},
			_refreshWechatOpenId: function() {	
				//todo 不是微信环境 直接进入
				if(isWxOrAli() != 'WECHAT'){
					this.navPage();
					return ;
				}
				let _redirectUrl = window.location.href;
				refreshUserOpenId({
					redirectUrl: _redirectUrl,
					wAppId: this.appId,
				}).then(_data => {
					if (_data.code == 0) {
						window.location.href = _data.data.openUrl;
						return;
					}
				});
			},
			_refreshWechatMiniOpenId:function(){
				let _that =this;
				wx.login({
					success: function(loginRes) {
						if (!loginRes.code) {
							return;
						}
						let accountInfo = uni.getAccountInfoSync();
						let appId = accountInfo.miniProgram.appId;
							getWechatMiniOpenId({
								code:loginRes.code,
								appId:appId,
							}).then(_data =>{
								if(_data.code != 0){
									uni.showToast({
										icon:'none',
										title:_data.msg
									})
									return;
								}
								_that.openId = _data.data;
								_that.saveOpenIdToOwner();
							})
						
					},
					fail: function(error) {
						// 调用 wx.login 接口失败
						console.log('调用wx.login获取code失败');
						console.log(error);
					}
				});
			},
			saveOpenIdToOwner:function(){
				let _that =this;
				refreshAppUserBindingOwnerOpenId(this,{
					openId:this.openId,
					communityId:this.communityId
				}).then(_data=>{
					_that.navPage();
				},err=>{
					uni.showToast({
						icon:'none',
						title:err
					})
				})
			},
			navPage:function(){
				//todo 是否有跳转地址（读取后立即删除，无值回首页）
				navigateToTargetUrl(takeTargetUrl(),'/pages/index/index');
			}
		}
	}
</script>

<style>

</style>
