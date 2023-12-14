<template>
	<view class="content">
		<view class="cu-bar bg-white solid-bottom ">
			<view class="action">
				<text class="cuIcon-title text-orange "></text>打印机
			</view>
		</view>
		<view class="cu-form-group">
			<view class="title">打印机</view>
			<vc-select ref="printRef" :datas="list" @getValue="getPrint"></vc-select>
		</view>
		<view class="cu-form-group">
			<view class="title">收银台自动打印</view>
			<vc-select ref="autoPrintRef" :datas="autoPrints" @getValue="getAutoPrintValue"></vc-select>
		</view>
		<view class="padding">
			<button class="cu-btn block bg-blue margin-tb-sm lg" @click="_savePrint()">提交</button>
		</view>
	</view>
</template>

<script>
	import vcSelect from '@/components/vc-select/vc-select.vue'
	export default {
		data() {
			return {
				autoPrints:[{
					name:'是',
					value:'Y1'
				},{
					name:'否',
					value:'N1'
				}],
				isSearching: false, //是否正在搜索中
				list: [],
				services: [],
				serviceId: 0,
				writeCharacter: false,
				readCharacter: false,
				notifyCharacter: false,
				BLEInformation: {
					platform: uni.getSystemInfoSync().platform || "",
					deviceId: "",
					writeCharaterId: "",
					writeServiceId: "",
					notifyCharaterId: "",
					notifyServiceId: "",
					readCharaterId: "",
					readServiceId: "",
					autoPrint:'N'
				}
			};
		},
		components:{
			vcSelect
		},
		onLoad() {
			// console.log(this.Bluetooth)
			this.startSearch();
			let _BLEInformation = uni.getStorageSync('BLEInformation');
			let _that = this;
			if(_BLEInformation && _BLEInformation.deviceId){
				setTimeout(function(){
					_that.stopSearch()
					_that.$refs.printRef.initSelect(_BLEInformation.deviceId);
					_that.$refs.autoPrintRef.initSelect(_BLEInformation.autoPrint);
					
				},3000);
			
				_that.BLEInformation = _BLEInformation
				console.log('123',_that.BLEInformation)
			}
		},
		onUnload() {
			//停止搜索蓝牙设备
			if (this.isSearching) {
				uni.stopBluetoothDevicesDiscovery();
			}
		},
		methods: {
			//错误码提示
			errorCodeTip(code) {
				if (code == 0) {
					//正常
				} else if (code == 10000) {
					uni.showToast({
						title: '未初始化蓝牙适配器',
						icon: 'none'
					})
				} else if (code == 10001) {
					uni.showToast({
						title: '当前蓝牙适配器不可用',
						icon: 'none'
					})
				} else if (code == 10002) {
					uni.showToast({
						title: '没有找到指定设备',
						icon: 'none'
					})
				} else if (code == 10003) {
					uni.showToast({
						title: '连接失败',
						icon: 'none'
					})
				} else if (code == 10004) {
					uni.showToast({
						title: '没有找到指定服务',
						icon: 'none'
					})
				} else if (code == 10005) {
					uni.showToast({
						title: '没有找到指定特征值',
						icon: 'none'
					})
				} else if (code == 10006) {
					uni.showToast({
						title: '当前连接已断开',
						icon: 'none'
					})
				} else if (code == 10007) {
					uni.showToast({
						title: '当前特征值不支持此操作',
						icon: 'none'
					})
				} else if (code == 10008) {
					uni.showToast({
						title: '其余所有系统上报的异常',
						icon: 'none'
					})
				} else if (code == 10009) {
					uni.showToast({
						title: 'Android 系统特有，系统版本低于 4.3 不支持 BLE',
						icon: 'none'
					})
				}
			},
			//开始搜索蓝牙
			startSearch() {
				let that = this
				uni.openBluetoothAdapter({
					success(res) {
						uni.getBluetoothAdapterState({
							success(res2) {
								console.log('getBluetoothAdapterState:', res2)
								if (res2.available) {
									that.isSearching = true;
									if (res2.discovering) {
										uni.showToast({
											title: '正在搜索附近打印机设备',
											icon: "none"
										})
										return;
									}
									//获取蓝牙设备信息
									that.getBluetoothDevices()
									// that.checkPemission()
								} else {
									uni.showModal({
										title: '提示',
										content: '本机蓝牙不可用',
									})
								}
							}
						});
					},
					fail() {
						uni.showModal({
							title: '提示',
							content: '蓝牙初始化失败，请打开蓝牙',
						})
					}
				})
			},
			stopSearch() {
				uni.stopBluetoothDevicesDiscovery({
					success: (res) => {
						this.isSearching = false;
						console.log('stop:', res)
					},
					fail: (e) => {
						console.log('stop:', e)
						this.errorCodeTip(e.errCode);
					}
				})
			},
			//校验权限
			checkPemission() {
				let that = this
				that.getBluetoothDevices();
			},
			//获取蓝牙设备信息
			getBluetoothDevices() {
				let that = this
				that.list = [];
				uni.startBluetoothDevicesDiscovery({
					success(res) {
						// console.log(res)
						//蓝牙设备监听 uni.onBluetoothDeviceFound
						plus.bluetooth.onBluetoothDeviceFound((result) => {
							console.log('onBluetoothDeviceFound:', result)
							let arr = that.list;
							let devices = [];
							let list = result.devices;
							for (let i = 0; i < list.length; ++i) {
								if (list[i].name && list[i].name != "未知设备") {
									let arrNew = arr.filter((item) => {
										return item.value == list[i].deviceId;
									});
									// console.log('arrNew:',arrNew.length)
									if (arrNew.length == 0) {
										devices.push({
											name:list[i].name,
											value:list[i].deviceId
										});
									}
								}
							}
							that.list = arr.concat(devices);
						});
						that.time = setTimeout(() => {
							// uni.getBluetoothDevices
							plus.bluetooth.getBluetoothDevices({
								success(res2) {
									let devices = [];
									let list = res2.devices;
									for (let i = 0; i < list.length; ++i) {
										if (list[i].name && list[i].name != "未知设备") {
											//devices.push(list[i]);
											devices.push({
												name:list[i].name,
												value:list[i].deviceId
											});
										}
									}
			
									that.list = devices;
									console.log('getBluetoothDevices:',res2);
								},
							})
							clearTimeout(that.time);
						}, 3000);
					}
				});
			
			},
			//绑定蓝牙
			getPrint(_data) {
				let that = this;
				let title = _data.value;
				this.stopSearch();
				that.serviceId = 0;
				that.writeCharacter = false;
				that.readCharacter = false;
				that.notifyCharacter = false;
				uni.showLoading({
					title: '正在连接',
				})
				// uni.createBLEConnection
				plus.bluetooth.createBLEConnection({
					deviceId: title,
					success(res) {
						console.log('createBLEConnection success:', res)
						that.BLEInformation.deviceId = _data.value;
						uni.hideLoading()
						that.getSeviceId()
					},
					fail(e) {
						that.errorCodeTip(e.errCode);
						uni.hideLoading()
					}
				})
			},
			//获取蓝牙设备所有服务(service)。
			getSeviceId() {
				let that = this;
				console.log('that.BLEInformation.deviceId:',that.BLEInformation.deviceId)
				// uni.getBLEDeviceServices
				let t=setTimeout(()=>{
					plus.bluetooth.getBLEDeviceServices({
						deviceId: that.BLEInformation.deviceId,
						success(res) {
							console.log('getBLEDeviceServices success:',res)
							that.services = res.services;
							that.getCharacteristics()
						},
						fail: function(e) {
							that.errorCodeTip(e.code);	
							console.log('getBLEDeviceServices fail:',e)
						}
					})
					clearTimeout(t);
				},1500)
			},
			getCharacteristics() {
				var that = this
				let {
					services: list,
					serviceId: num,
					writeCharacter: write,
					readCharacter: read,
					notifyCharacter: notify
				} = that;
				// uni.getBLEDeviceCharacteristics
				plus.bluetooth.getBLEDeviceCharacteristics({
					deviceId: that.BLEInformation.deviceId,
					serviceId: list[num].uuid,
					success(res) {
						// console.log(res)
						for (var i = 0; i < res.characteristics.length; ++i) {
							var properties = res.characteristics[i].properties
							var item = res.characteristics[i].uuid
							if (!notify) {
								if (properties.notify) {
									that.BLEInformation.notifyCharaterId = item;
									that.BLEInformation.notifyServiceId = list[num].uuid;	
									notify = true
								}
							}
							if (!write) {
								if (properties.write) {
									that.BLEInformation.writeCharaterId = item;
									that.BLEInformation.writeServiceId = list[num].uuid;
									write = true
								}
							}
							if (!read) {
								if (properties.read) {
									that.BLEInformation.readCharaterId = item;
									that.BLEInformation.readServiceId = list[num].uuid;
									read = true
								}
							}
						}
						if (!write || !notify || !read) {
							num++
							that.writeCharacter = write;
							that.readCharacter = read;
							that.notifyCharacter = notify;
							that.serviceId = num;
							if (num == list.length) {
								uni.showModal({
									title: '提示',
									content: '找不到该读写的特征值',
								})
							} else {
								that.getCharacteristics()
							}
						} else {
							console.log('that.BLEInformation',that.BLEInformation)
						}
					},
					fail: function(e) {
						console.log("getBLEDeviceCharacteristics fail：",e);
						that.errorCodeTip(e.errCode);	
					}
				})
			},
			
			getAutoPrintValue:function(_data){
				this.BLEInformation.autoPrint = _data.value
			},
			_savePrint:function(){
				if(!this.BLEInformation.deviceId){
					uni.showToast({
						icon:'none',
						title:'请选择打印机'
					})
					return;
				}
				if(!this.BLEInformation.autoPrint){
					uni.showToast({
						icon:'none',
						title:'请选择是否自动打印'
					})
					return;
				}
				
				uni.setStorageSync('BLEInformation',this.BLEInformation);
				uni.navigateBack({
					delta:1
				})
			}
		}
	}
</script>

<style lang="less">
	.btn {
		margin-top: 50rpx;
		height: 40px;
		width: 600rpx;
		line-height: 40px;
	}

	.item {
		display: block;
		font-family: Arial, Helvetica, sans-serif;
		font-size: 14px;
		margin: 0 30px;
		margin-top: 10px;
		background-color: #FFA850;
		border-radius: 5px;
		border-bottom: 2px solid #68BAEA;
	}

	.block {
		display: block;
		color: #ffffff;
		padding: 5px;
	}
</style>
