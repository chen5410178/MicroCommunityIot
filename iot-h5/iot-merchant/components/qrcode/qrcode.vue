<template>
	<view>

	</view>
</template>

<script>
	var barcode = null;
	var currentWebview = null
	export default {
		name: "qrcode",
		data() {
			return {
				name: '', //要在扫码界面自定义的内容
				flash: true, //是否打开摄像头
				type: 'scan-listener',
				cacame: {}
			};
		},
		mounted() {
			var pages = getCurrentPages();
			var page = pages[pages.length - 1];
			// #ifdef APP-PLUS
			plus.navigator.setFullscreen(false); //全屏
			currentWebview = page.$getAppWebview();
			this.createBarcode(currentWebview); //创建二维码窗口
			this.createView(currentWebview); //创建操作按钮及tips界面
			// #endif
			
			uni.$on('qrcode',flag=>{
				this.closeQrcode();
				if(!flag){
					this.startQrcode(); //创建二维码窗口
				}
			})
		},
		methods: {
			onmarked(type, result) {
				var text = '未知: ';
				switch (type) {
					case plus.barcode.QR:
						text = 'QR: ';
						break;
					case plus.barcode.EAN13:
						text = 'EAN13: ';
						break;
					case plus.barcode.EAN8:
						text = 'EAN8: ';
						break;
				}
				plus.navigator.setFullscreen(false);
				this.$emit('resultText', {
					result: result
				})
				uni.$emit(this.type, {
					result: result
				})
				console.log(this.type, result, 'this.type');
				setTimeout(function(){
					barcode.start();
				},3000)
			},
			// 创建二维码窗口
			createBarcode(currentWebview) {
				//自定义窗口大小 , plus.barcode.EAN13, plus.barcode.EAN8
				//条码类型常量数组，默认情况支持QR、EAN13、EAN8类型。 通过此参数可设置扫码识别支持的条码类型（注意：设置支持的条码类型越多，扫描识别速度可能将会降低）
				barcode = plus.barcode.create('barcode',  [plus.barcode.QR, plus.barcode.EAN13,plus.barcode.EAN8], {
					top: '0px',
					left: '0px',
					width: '100%',
					height: '300px',
					scanbarColor: '#1DA7FF',
					position: 'static',
					frameColor: '#1DA7FF'
				});
				barcode.onmarked = this.onmarked;
				barcode.setFlash(this.flash);
				currentWebview.append(barcode);
				barcode.start();
			},
			createView(currentWebview) {
				// 创建返回原生按钮
				var backVew = new plus.nativeObj.View('backVew', {
						top: '0px',
						left: '0px',
						height: '300px',
						width: '100%'
					},
					[{
						tag: 'img',
						id: 'backBar',
						src: 'static/image/goods.png',
						position: {
							bottom:'10px',
							right: '10px',
							width: '32px',
							height: '35px'
						}
					}]);
				backVew.interceptTouchEvent(true);
				currentWebview.append(backVew);
				let _that = this;
				backVew.addEventListener("click", function(e) { //返回按钮
					_that.$emit('selectGoods', {
					})
				}, false);
			},
			closeQrcode:function(){
				console.log(barcode)
				if(!barcode){
					return ;
				}
				barcode.close();
			},
			startQrcode:function(){
				this.createBarcode(currentWebview); //创建二维码窗口
			}

		},
		destroyed() {
			// #ifdef APP-PLUS
			barcode.close();
			console.log("onUnload");
			plus.navigator.setFullscreen(false);
			// #endif
		},


	}
</script>

<style>

</style>
