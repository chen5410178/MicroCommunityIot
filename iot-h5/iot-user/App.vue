<script>
	//app.js

	import {
		requestNoAuth
	} from './lib/java110/java110Request.js';


	import url from './conf/url.js'

	export default {
		// 小程序启动生命周期
		onLaunch: function() {
			let that = this; // 检查登录状态

			//loginFactory.checkLoginStatus(); // 获取用户地理位置
			//userFactory.getUserLocation();

			requestNoAuth({
				url: url.listSystemInfo,
				method: "GET",
				data: {
					page: 1,
					row: 1
				},
				success: function(res) {
					console.log(res.data);
					if (res.data.data && res.data.data.length > 0) {
						uni.setStorageSync('java110SystemConfig', res.data.data[0])
					}
					//reslove(res);
				},
				fail: function(e) {}

			});
		},
		// app全局数据
		globalData: {
			userInfo: null
		},
		methods: {}
	};
</script>
<style>
	@import "./app.css";
	@import "./style/charge.css";

	/* #ifdef H5 */
	uni-page-head {
		display: none;
	}

	/* #endif */
</style>
