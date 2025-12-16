<template>
	<view>
		<view class="bg-white success-msg">
			<view class="  ">
				<view class=" padding flex align-center">
					<view class="flex-sub text-center">
						<view class="text-xsl">
							<text class="cuIcon-roundcheckfill text-green"></text>
						</view>
						<view class=" text-msg">{{msg || '处理成功'}}</view>
					</view>
				</view>
			</view>

			<view class="padding flex flex-direction margin-top">
				<button class="cu-btn bg-green lg" @click="_indexPage()">确认</button>
			</view>
		</view>

		<view class="home_wrap">
			<success-ads :objType="objType"></success-ads>
		</view>
			<success-goods :objType="objType"></success-goods>


	</view>
</template>

<script>
	import successAds from '@/components/success/success-ads.vue';
	import successGoods from '@/components/success/success-goods.vue';
	export default {
		data() {
			return {
				msg: '',
				objType:'3003',
				imgs: [],
				goods: []
			}
		},
		onLoad(options) {
			this.msg = options.msg;
			this.objType = options.objType;
		},
		components: {
			successAds,
			successGoods
		},
		methods: {
			_indexPage:function(){
				// 优先检查是否有需要返回的业务页面（使用与登录一致的 targetUrl 字段）
				try{
					const _targetUrl = uni.getStorageSync('targetUrl');
					if (_targetUrl) {
						uni.removeStorageSync('targetUrl');
						uni.redirectTo({
							url: _targetUrl
						});
						return;
					}
				}catch(e){
					console.error('读取 targetUrl 失败：', e);
				}
				// 默认返回首页
				uni.switchTab({
					url: "/pages/index/index"
				});
			}
		}
	}
</script>

<style>
	.success-msg {
		padding-top: 100upx;
		padding-bottom: 50upx;
	}

	.text-xsl {
		font-size: 180upx;
		line-height: 1.2;
	}

	.text-msg {
		font-size: 40upx;
	}

	.home_wrap {
		padding: 20upx 20upx 0;
	}
</style>
