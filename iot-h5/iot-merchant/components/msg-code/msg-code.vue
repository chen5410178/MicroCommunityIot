<template>
	<view>
		<view class="cu-form-group">
			<view class="title">验证码</view>
			<input v-model="code" placeholder="请输入短信验证码" name="input"></input>
			<button class='cu-btn bg-green shadow' :disabled="btnDisabled" @click="sendMsgCode()">{{btnValue}}</button>
		</view>
	</view>
</template>

<script>
	import url from '@/constant/url.js'
	export default {
		name: "msgCode",
		data() {
			return {
				code: '',
				btnValue: '验证码',
				btnDisabled: false,
				second: 60,
			};
		},
		props: {
			link: {
				type: String,
				required: true
			}
		},
		methods: {
			getMsgCode: function() {
				return this.code;
			},
			sendMsgCode: function() {
				var _that = this;
				let obj = {
					tel: this.link
				};
				let msg = "";
				if (obj.tel == '') {
					msg = '请输入手机号';
				}
				if (msg != "") {
					wx.showToast({
						title: msg,
						icon: 'none',
						duration: 2000
					});
					return;
				}
				uni.showLoading({
					title: '加载中',
					mask: true
				});
				uni.request({
					url: url.sendTelMessageCode,
					header: this.context.getHeaders(),
					method: "POST",
					data: obj, //动态数据
					success: function(res) {
						uni.hideLoading();
						//成功情况下跳转
						if (res.statusCode == 200) {
							wx.showToast({
								title: res.data,
								icon: 'none',
								duration: 2000
							});
							wx.hideLoading();
							_that.timer();
							return;
						}
						wx.showToast({
							title: res.data,
							icon: 'none',
							duration: 2000
						});
					},
					fail: function(e) {
						wx.hideLoading();
						console.log(e);
						wx.showToast({
							title: "服务器异常了",
							icon: 'none',
							duration: 2000
						})
					}
				});
			},
			timer: function() {
				let promise = new Promise((resolve, reject) => {
					let setTimer = setInterval(
						() => {
							var second = this.second - 1;
							this.second = second;
							this.btnValue = second + '秒';
							this.btnDisabled = true;
							if (this.second <= 0) {
								this.second = 60;
								this.btnValue = '获取验证码';
								this.btnDisabled = false;
								resolve(setTimer)
							}
						}, 1000)
				})
				promise.then((setTimer) => {
					clearInterval(setTimer)
				})
			}
		}
	}
</script>

<style>

</style>
