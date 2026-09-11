import {isNull} from './StringUtil.js'
export function encodeUrl(_url){
	if(isNull(_url)){
		return _url;
	}
	_url = _url.replace("?","**");
	_url = _url.replaceAll("&","@@");
	_url = _url.replace("#","!!");
	return _url;
}

export function decodeUrl(_url){
	if(isNull(_url)){
		return _url;
	}
	if(_url.indexOf("?")>-1){
		_url = _url.replaceAll("**", "&"); //已经有？了
	}else{
		_url = _url.replace("**", "?"); //已经有？了
	}
	_url = _url.replace("!!", "#");
	_url = _url.replace("$$", "#");
	_url = _url.replaceAll("@@", "&");
	_url = _url.replaceAll("**", "&"); //已经有？了
	
	return _url;
}

export function getPageUrl(){
	    let pages = getCurrentPages()
	    if (!pages || pages.length < 1) {
		return '';
	    }
	    let curPage = pages[pages.length - 1]
	    let curParam = curPage.options || {} //获取当前页面参数
	    let param = []
	    for (let key in curParam) { //获取key=value键值对格式数组
		let _value = curParam[key]
		if (_value === null || typeof _value == 'undefined') {
			_value = ''
		}
		param.push(key + '=' + encodeURIComponent(_value))
	    }
	    let url = '/' + curPage.route //最终格式**/pages/index/index?id=11&name='boyyang'&sex='man'**
	    if (param.length < 1) { //无参数时不再拼出 undefined
		return url;
	    }
	    return url + '?' + param.join('&');
}
