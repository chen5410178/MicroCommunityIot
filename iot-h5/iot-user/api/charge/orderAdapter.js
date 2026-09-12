/**
 * 充电订单接口字段 → 页面字段适配（订单列表 / 订单详情 / 首页共用）
 * 接口：charge.queryPhoneChargeOrder（GET）
 * 订单详情在充视图复用同一适配结果（startTime/slot/portName/machineId/portId/communityId 为停止充电补充字段）
 */
function pad(n) {
	return ('0' + n).slice(-2)
}

// 时间格式化为 MM-DD HH:mm（跨年保留原文）
function fmtTime(t) {
	if (!t) return ''
	if (typeof t === 'number') {
		const d = new Date(t < 1e12 ? t * 1000 : t)
		if (isNaN(d.getTime())) return String(t)
		return pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes())
	}
	const s = String(t)
	const m = /^(\d{4})[-/](\d{2})[-/](\d{2})[ T](\d{2}):(\d{2})/.exec(s)
	if (m) return m[2] + '-' + m[3] + ' ' + m[4] + ':' + m[5]
	return s
}

// 单条订单适配
export function adaptOrder(o) {
	const state = o.state || ''
	const hours = parseFloat(o.chargeHours || 0)
	const isMonthCard = o.acctDetailId === '-1'
	return {
		orderId: o.orderId || '',
		pile: o.machineName || '',
		date: fmtTime(o.createTime),
		mins: isNaN(hours) ? 0 : Math.round(hours * 60),
		fee: parseFloat(o.amount || 0) || 0,
		way: isMonthCard ? '月卡' : '余额',
		acctDetailId: o.acctDetailId || '',
		st: state === '2002' ? '已完成' : state === '1001' ? '充电中' : state === '4004' ? '已停止' : state === '3003' ? '充电失败' : (o.stateName || ''),
		state: state,
		type: o.chargeType === '1002' ? 'car' : 'bike',
		kwh: parseFloat(o.energy || 0) || 0,
		held: 0,
		// 在充订单补充字段（历史订单为空不影响展示）
		startTime: o.startTime || '',
		endTime: o.endTime || '',
		slot: o.portName || o.portCode || '',
		portName: o.portName || '',
		portCode: o.portCode || '',
		// 停止充电接口必填参数
		machineId: o.machineId || '',
		portId: o.portId || '',
		communityId: o.communityId || '',
		// 订单归属校验
		personId: o.personId || '',
		personTel: o.personTel || ''
	}
}

// 订单列表适配
export function adaptOrderList(list) {
	return (list || []).map(adaptOrder)
}
