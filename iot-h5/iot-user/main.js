import Vue from 'vue';
import App from './App';
import conf from './conf/config.js';

import vc from './core/vcFramework.js'
// 小程序全局分享设置
import share from './common/mixin/shareMixin.js'
Vue.mixin(share)

Vue.config.productionTip = false;

Vue.prototype.vc = vc;  
Vue.prototype.imgUrl = conf.imgUrl;

// 全局注入 imgUrl 到组件 data（小程序端模板无法从 Vue.prototype 取值，必须走 data）
Vue.mixin({
	data() {
		return {
			imgUrl: conf.imgUrl
		}
	}
})

App.mpType = 'app';

const app = new Vue({
    ...App
});
app.$mount();
