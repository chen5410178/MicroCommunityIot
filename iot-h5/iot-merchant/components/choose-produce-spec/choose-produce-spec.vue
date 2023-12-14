<template>
	<view>
		<view class="cu-modal" :class="isChoose?'show':''" @tap="hideModal">
			<view class="cu-dialog" @tap.stop="">
				<radio-group class="block" @change="_radioChange">
					<view class="cu-list menu text-left">
						<view class="cu-item" v-for="(item,index) in productSpecs" :key="index">
							<label class="flex justify-between align-center flex-sub">
								<view class="flex-sub">{{item.specName}}>{{_getSpecDetailName(item.productSpecDetails)}}</view>
								<radio class="round" 
								 :value="item.specId"></radio>
							</label>
						</view>
					</view>
				</radio-group>
				<view class="padding">
					<button class="cu-btn block bg-white margin-tb-sm lg" @tap="_doProductSpecClose" type="">关闭</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	
	import {
		queryProductSpec
	} from "../../api/product/productApi.js";
	export default {
		name:"chooseProduceSpec",
		data() {
			return {
				productSpecs:[],				
			};
		},
		props:{
			isChoose:{
				type:Boolean,
				default:false
			}
		},
		computed:{
			
		},
		created() {
			this._loadproductSpecs();
		},
		methods:{
			_loadproductSpecs:function(){
				let _that = this
				let params = {
					page: 1,
					row: 50,
					shopId: _that.shopId
				}
				queryProductSpec(_that, params)
					.then(function(result) {
						_that.productSpecs = result.data;
					})
			},
			_radioChange:function(e){
				let _that = this;
				if(!this.productSpecs || this.productSpecs.length<1){
					_that.$emit('getProductSpec',{});
					return ;
				}
				this.productSpecs.forEach(_item=>{
					if(_item.specId == e.detail.value){
						_that.$emit('getProductSpec',_item)
						return ;
					}
				})
			},
			_getSpecDetailName:function(_details){
				if(!_details || _details.length <1){
					return '';
				}
				let _value = '';
				_details.forEach(_item=>{
					_value +=(_item.detailName+":"+_item.detailValue+";")
				})
				return _value;
			},
			_doProductSpecClose:function(){
				this.$emit('productSpecClose',{})
			}
			
		}
	}
</script>

<style>

</style>