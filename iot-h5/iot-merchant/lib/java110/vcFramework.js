/**
 * vcFramework
 * 
 * @author 吴学文
 * 
 * @version 0.3
 * 
 * @description uni-app 开发工具
 * 
 * @time 2020-03-04
 * 
 * @qq 928255095
 * 
 * @mail 928255095@qq.com
 * 
 */

// 存储相关
import {getStorageSync,setStorageSync} from './utils/StorageUtil.js'
// 日志相关
import {debug,info,error,warn} from './utils/LogUtil.js'

//路由相关
import {navigateTo,navigateBack,navigateHome} from './vcRoute.js'

//字符串相关
import {isEmpty} from './utils/StringUtil.js'

// 获取平台
import {getPlatform} from './utils/SystemUtil.js'

//统一请求
import {request} from './java110Request.js'

//消息提示
import {toast} from './utils/VcShow.js'

//序列
import {generatorSeq} from './utils/SeqUtil.js'

//onload
import {onLoad,getCurrentUser} from './init/initPage.js'

import {hasLogin,hasUserOpenId,loginAuth} from './auth/Java110Auth.js'


export default{
	getStorageSync:getStorageSync,
	setStorageSync:setStorageSync,
	debug:debug,
	info:info,
	error:error,
	warn:warn,
	onLoad:onLoad,
	navigateTo:navigateTo,
	navigateBack:navigateBack,
	isEmpty:isEmpty,
	getPlatform:getPlatform,
	toast:toast,
	generatorSeq:generatorSeq,
	hasLogin:hasLogin,
	getCurrentUser:getCurrentUser,
	hasUserOpenId:hasUserOpenId,
	loginAuth:loginAuth,
	navigateHome:navigateHome
}