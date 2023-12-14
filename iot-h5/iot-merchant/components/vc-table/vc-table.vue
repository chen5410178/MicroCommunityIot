<template>
   <view class="content">
   	<view class="genaral-area">
   		<view class="flex-box tc thead">
   			<view :style="{ textAlign: align, backgroundColor: titleBg,flex:'1' }" class="item" v-for="(item, index) in title" :key="index" v-if="isSave">
   				<text>{{ item }}</text>
   			</view>
   		</view>
   		<view class="flex-box tc" v-for="(item, index) in tableData" :key="index">
   			<view class="item" @tap="_selectData(item)" :style="{ textAlign: align,flex:'1',display:'flex'}" v-for="(zItem, zIndex) in title" :key="zIndex" v-if="isSave">
   				<template><view :style="{flex:1}"><text style="word-break: break-all;">{{ item[zItem] || '-' }}</text></view> </template>
   			</view>
   		</view>
   	</view>
   
   </view>
</template>

<script>
export default {
   data() {
   	return {
   	};
   },
   props: {
   	tableData: {
   		type: Array,
   		required: true
   	},
   	title: {
   		type: Array,
   		required: true
   	},
   	align:{
   		type:String,
   		default:'center'
   	},
   	titleBg:{
   		type:String,
   		default:'#fff'
   	},
   	isSave:{
   		type:Boolean,
   		default:true
   	}
   },
   methods: {
   	//事件触发
   	pullEvent(event, data) {
   		this.$emit(event, data);
   	},
	_selectData:function(_item){
		this.$emit('selectData',_item);
	}
   },
};
</script>

<style lang="scss">
$color: #e0e0e0;
.content {
   display: flex;
   flex: 1;
   flex-direction: column;
   // background-color: #fbf9fe;
}

.flex-box {
   display: flex;
   flex-wrap: wrap;
   justify-content: center ;
   &:last-child {
   	border-bottom: 1upx solid $color;
   }
}

.flex-box > .item {
   flex: 1;
}


.title {
   margin: 20upx 0;
   // color: red;
}
.genaral-area {
   .item {
   	// font-size: 26upx;
   	border: 1upx solid $color;
   	border-width: 1upx 1upx 0 0;
   	padding: 16upx 0;
   	line-height: 48upx;
   	box-sizing: border-box;
   	text-align: center;
   	&:first-child {
   		border-left-width: 1upx;
   	}
   }

   .thead {
   	.item {
   		font-weight: bold;
   		box-sizing: border-box;
   		background-color: #eef1f6;
   	}
   }

   .table {
   	&:last-child {
   		border-bottom: 1upx solid $color;
   	}
   }
}
</style>

