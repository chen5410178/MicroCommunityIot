<template>
	<view>
		<picker @change="shopHouseChange" :value="shopHouseIndex" :range="datas" :range-key="'name'">
			<view class="picker">
				{{shopHouseIndex > -1 ? datas[shopHouseIndex].name:'请选择'}}
			</view>
		</picker>
	</view>
</template>

<script>
	export default {
		name: "vcSelect",
		data() {
			return {
				shopHouseIndex: -1,
				name: ''
			};
		},
		props: {
			datas: {
				type: Array,
				require: true
			}
		},

		methods: {
			initSelect: function(_value) {
				let _that = this;
				_that.shopHouseIndex = 0;
				this.datas.forEach(item => {
					if (item.value == _value) {
						console.log(23123213123,_value,item.value)
						return;
					}
					console.log(1111111111111111,_value,item.value)
					_that.shopHouseIndex += 1;


				})
				console.log(_that.shopHouseIndex, _value)
			},
			shopHouseChange(e) {
				this.shopHouseIndex = e.detail.value;
				this.name = this.datas[this.shopHouseIndex].name;
				this.$emit('getValue', this.datas[this.shopHouseIndex]);
			},
		}
	}
</script>

<style>

</style>
