import {
	request,
	requestNoAuth
} from '../../lib/java110/java110Request.js'
import url from '../../conf/url.js'
import {
	formatTime
} from '../../lib/java110/utils/DateUtil.js'

/**
 * 查询访客列表
 */
export function listOwnerVisit(_data) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listOwnerVisit,
			method: "GET",
			data: _data,
			success: function(res) {
				if (res.statusCode == 200) {
					//成功情况下跳转
					let _visits = res.data.visits;
					if (_visits == null || _visits == undefined) {
						_visits = [];
					} else {
						_visits.forEach(function(_visit) {
							let _createTime = new Date(_visit.createTime.replace(/\-/g,
								"/"));
							_visit.createTime = formatTime(_createTime);
							if (_visit.hasOwnProperty("departureTime")) {
								let _departureTime = new Date(_visit.departureTime.replace(
									/\-/g, "/"));
								_visit.departureTime = formatTime(_departureTime);
							}
							if (_visit.hasOwnProperty("visitTime")) {
								let _visitTime = new Date(_visit.visitTime.replace(/\-/g,
									"/"));
								_visit.visitTime = formatTime(_visitTime);
							}
						});
					}
					resolve(res.data);
				}
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}
/**
 * 访客预约提交
 */
export function saveAddVisit(_data) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.saveAddVisit,
			method: "POST",
			data: _data,
			success: function(res) {
				resolve(res.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}
/**
 * 查询访客类型
 */
export function listVisitType(_data) {
	return new Promise((resolve, reject) => {
		requestNoAuth({
			url: url.listVisitType,
			method: "get",
			data: _data,
			success: function(res) {
				resolve(res.data);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}

/**
 * 查询访客列表
 */
export function getOwnerVisit(_data) {
	return new Promise((resolve, reject) => {
		request({
			url: url.getOwnerVisit,
			method: "GET",
			data: _data,
			success: function(res) {
				//成功情况下跳转
				let _visits = res.data.visits;
				if (!_visits) {
					_visits = [];
				} else {
					_visits.forEach(function(_visit) {
						let _createTime = new Date(_visit.createTime.replace(/\-/g, "/"));
						_visit.createTime = formatTime(_createTime);
						if (_visit.hasOwnProperty("departureTime")) {
							let _departureTime = new Date(_visit.departureTime.replace(
								/\-/g, "/"));
							_visit.departureTime = formatTime(_departureTime);
						}
						if (_visit.hasOwnProperty("visitTime")) {
							let _visitTime = new Date(_visit.visitTime.replace(/\-/g, "/"));
							_visit.visitTime = formatTime(_visitTime);
						}
					});
				}
				resolve(_visits);
			},
			fail: function(res) {
				reject(res);
			}
		});
	})
}