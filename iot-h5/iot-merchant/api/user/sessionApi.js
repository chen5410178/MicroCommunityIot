const LOGIN_FLAG = 'loginFlag'; //登录标识

const TOKEN = "token";


/**
 * 是否 登录
 */
export function hasLogin() {
	let loginFlag = wx.getStorageSync(LOGIN_FLAG);
	let nowDate = new Date();
	if (loginFlag && loginFlag.expireTime > nowDate.getTime()) {
		return true;
	} else {
		return false;
	}
}

/**
 * 是否有回话
 */
export function hasSession(){
	if(!hasLogin()){
		uni.navigateTo({
			url:'/pages/login/login'
		})
	}
}

export function getToken(){
	return uni.getStorageSync(TOKEN);
}

export function saveLoginFlag(_userId,_token){
	let date = new Date();
	let year = date.getFullYear(); //获取当前年份
	let mon = date.getMonth(); //获取当前月份
	let da = date.getDate(); //获取当前日
	let h = date.getHours() + 1; //获取小时
	let m = date.getMinutes(); //获取分钟
	let s = date.getSeconds(); //获取秒
	let afterOneHourDate = new Date(year, mon, da, h, m, s); //30s之后的时间
	wx.setStorageSync(LOGIN_FLAG, {
		sessionKey: _userId,
		expireTime: afterOneHourDate.getTime()
	});
	wx.setStorageSync(TOKEN, _token);
}