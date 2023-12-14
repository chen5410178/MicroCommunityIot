<template>
	<view>
		<view class="cu-bar bg-white">
			<view class="action">
				{{title}}
			</view>
			<view class="action">
				{{imgList.length}}/{{imgCount}}
			</view>
		</view>
		<view class="cu-form-group">
			<view class="grid col-4 grid-square flex-sub">
				<view class="bg-img" v-for="(item,index) in imgList" :key="index" @tap="ViewImage"
					:data-url="imgList[index]">
					<image :src="imgList[index]" mode="aspectFill"></image>
					<view class="cu-tag bg-red" @tap.stop="deleteImage" :data-index="index">
						<text class='cuIcon-close'></text>
					</view>
				</view>
				<view class="solids" @tap="ChooseImage" v-if="imgList.length<imgCount">
					<text class='cuIcon-cameraadd'></text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		pathToBase64
	} from 'image-tools'
	export default {
		name: "vcChooseImage",
		data() {
			return {
				photos: [],
				imgList: [],
			};
		},
		props: {
			imgCount: {
				type: Number,
				default: 1
			},
			title:{
				type:String,
				default:'图片上传'
			}
		},
		methods: {
			initImage:function(_url){
				this.imgList.push(_url);
				pathToBase64(_url).then(base64 => {
					this.photos.push(base64);
					this.$emit("changeImage", this.photos);
				})
			},
			deleteImage: function(e) {
				console.log(e);
				let imageArr = this.imgList;
				imageArr.splice(e, 1);
				this.photos.splice(e, 1);
				this.$emit("changeImage", this.photos);
			},
			ChooseImage: function(e) {
				let that = this;
				wx.chooseImage({
					count: 4, //默认9
					sizeType: ['compressed'], //可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], //从相册选择
					success: (res) => {
						that.imgList.push(res.tempFilePaths[0]);
						var tempFilePaths = res.tempFilePaths[0]
						that.translate(tempFilePaths, (url) => {
							that.photos.push(url);
							this.$emit("changeImage", that.photos);
						})
					}
				});
			},
			translate: function(tempFilePaths, callback) {
				//#ifdef H5
				var img = new Image();
				img.src = tempFilePaths;
				img.onload = function() {
					var that = this;
					var h = that.height;
					// 默认按比例压缩
					var w = that.width;
					if (h > 1080 || w > 1080) {
						let _rate = 0;
						if (h > w) {
							_rate = h / 1080;
							h = 1080;
							w = Math.floor(w / _rate);
						} else {
							_rate = w / 1080;
							w = 1080;
							h = Math.floor(h / _rate);
						}
					}
					var canvas = document.createElement('canvas');
					var ctx = canvas.getContext('2d');
					var anw = document.createAttribute("width");
					anw.nodeValue = w;
					var anh = document.createAttribute("height");
					anh.nodeValue = h;
					canvas.setAttributeNode(anw);
					canvas.setAttributeNode(anh);
					ctx.drawImage(that, 0, 0, w, h);
					//压缩比例
					var quality = 0.3;
					var base64 = canvas.toDataURL('image/jpeg', quality);
					canvas = null;
					callback(base64);
				}
				//#endif

				// #ifndef H5
				uni.compressImage({
					src: tempFilePaths,
					quality: 20,
					success: (res) => {
						pathToBase64(res.tempFilePath).then(base64 => {
							callback(base64);
						})
					},
					fail: (err) => {
						reject(err)
					}
				})
				// #endif

			}
		},

	}
</script>

<style>

</style>
