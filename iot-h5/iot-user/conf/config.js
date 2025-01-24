/**
 * HC智慧家园配置文件
 * 
 * 本项目只有这里修改相应配置信息，如果不是二次开发 请不要修改其他文件内容
 * 
 * @website http://www.homecommunity.cn/
 * @author 吴学文
 * @QQ 928255095
 */
let _systemConfig = uni.getStorageSync('java110SystemConfig');
// #ifdef H5
// 服务器域名 公众号时，配置为 / 就可以
const baseUrl = '/';
// #endif

// #ifndef H5
//服务器域名 小程序 或者 app 时 后端地址
const baseUrl = 'http://192.168.100.108:9999/';
// #endif

let commonBaseUrl = 'http://iot.crsdt.cn/';

const logLevel = "DEBUG"; // 日志级别

let systemName="业主版";

if(_systemConfig){
	commonBaseUrl = _systemConfig.imgUrl;
	systemName = _systemConfig.ownerTitle;
}

export default {
	baseUrl: baseUrl,
	commonBaseUrl: commonBaseUrl,
	logLevel: logLevel,
	imgUrl:commonBaseUrl,
	systemName:systemName
}
