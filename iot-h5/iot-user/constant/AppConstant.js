/**
 * 应用相关常量
 * 
 * add by wuxw 2019-12-28
 */
import conf from '../conf/config.js'
class AppConstant {
	
  static wAppId= conf.wAppId; //微信AppId
  /**
  * 应用信息，主要是用来和后端服务交互时的时候用
  */
  static appId = "992020061452450002";
  static appSecurity = "";
}

module.exports = AppConstant;