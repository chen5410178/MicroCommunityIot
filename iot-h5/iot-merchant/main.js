import Vue from 'vue'
import App from './App'
import Java110Context from './lib/java110/Java110Context.js'

import url from './constant/url.js'
import factory from './lib/java110/factory/factory.js'
import util from './lib/java110/utils/util.js'
import date from './lib/java110/utils/date.js'
import vc from 'lib/java110/vcFramework.js';
//引入vuex
import store from './store'

import cuCustom from './lib/colorui/components/cu-custom.vue'
import {VueJsonp} from 'vue-jsonp'
import {getCurrentShop} from './api/shop/shop.js'
Vue.use(VueJsonp)
Vue.component('cu-custom',cuCustom)
Vue.config.productionTip = false


Vue.config.productionTip = false
Vue.prototype.vc = vc;
Vue.prototype.getCurrentShop = getCurrentShop;
Vue.prototype.java110Context = Java110Context;  
Vue.prototype.java110Constant = Java110Context.constant; 
Vue.prototype.java110Factory = Java110Context.factory; 
Vue.prototype.java110Util = Java110Context.util;
Vue.prototype.$store = store;
Vue.prototype.url = url;
Vue.prototype.factory = factory;
Vue.prototype.util = util;
Vue.prototype.date = date;

App.mpType = 'app'

const app = new Vue({
    ...App,
	store
})
app.$mount()
