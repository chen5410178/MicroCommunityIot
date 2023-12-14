/**
 * 消息展示
 */
export function toast(_objData){
	uni.showToast({
	  title: _objData.title,
	  icon: _objData.icon,
	  duration: 1500,
	  mask: true
	});
}