export function getPlatform(){
	// #ifdef H5
	return 'H5'
	// #endif
	
	// #ifdef MP-WEIXIN
	return 'MP-WEIXIN'
	// #endif
	
	// #ifdef APP-PLUS
	return 'APP-PLUS'
	// #endif
}