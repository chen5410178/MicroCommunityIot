/**
 * 充电业务 mock 数据：小区/桩/套餐/分时电价/预扣规则/充值档位/订单/车辆/钱包流水
 * 无后端接口（或接口字段不满足原型）时使用，统一标注，便于后续切换真实接口
 * // TODO 后端接口就绪后替换
 */

// 电瓶车桩插座生成器：n 个插座（编号 1~N，busyIdx 为已占用插座）
export const mkSlots = (n, busyIdx = []) => Array.from({ length: n }, (_, i) => ({
	n: String(i + 1),
	busy: busyIdx.includes(i)
}))

// 小区 + 充电桩 mock（原型数据）
export const mockCommunities = [{
	name: '阳光花园',
	zone: '3 栋 · 5 栋 · 8 栋',
	dist: 500,
	piles: [{
		name: 'A 区 · 03 号桩',
		loc: '3 栋楼下 · 充电区 A03',
		free: true,
		used: 0,
		sockets: 12,
		slots: mkSlots(12)
	}, {
		name: 'B 区 · 12 号桩',
		loc: '3 栋楼下 · 充电区 B12',
		free: true,
		used: 0,
		sockets: 20,
		slots: mkSlots(20)
	}, {
		name: 'C 区 · 07 号桩',
		loc: '5 栋地下车库 · 充电区 C07',
		free: false,
		used: 1,
		sockets: 10,
		slots: mkSlots(10, [3])
	}, {
		name: 'D 区 · 01 号桩',
		loc: '8 栋架空层 · 充电区 D01',
		free: true,
		used: 0,
		sockets: 12,
		slots: mkSlots(12),
		online: false
	}],
	carPiles: [{
		name: 'E 区 · 01 号桩',
		loc: '5 栋地下车库 · 充电区 E01',
		free: true,
		used: 0,
		sockets: 2,
		guns: [{ n: 'A', busy: false }, { n: 'B', busy: false }]
	}, {
		name: 'F 区 · 02 号桩',
		loc: '8 栋地下车库 · 充电区 F02',
		free: false,
		used: 1,
		sockets: 1,
		guns: [{ n: 'A', busy: true }]
	}]
}, {
	name: '翡翠湖畔',
	zone: '1 栋 · 2 栋 · 6 栋',
	dist: 1200,
	piles: [{
		name: 'A 区 · 05 号桩',
		loc: '1 栋楼下 · 充电区 A05',
		free: true,
		used: 0,
		sockets: 12,
		slots: mkSlots(12)
	}, {
		name: 'B 区 · 09 号桩',
		loc: '2 栋楼下 · 充电区 B09',
		free: false,
		used: 2,
		sockets: 20,
		slots: mkSlots(20, [4, 11])
	}, {
		name: 'C 区 · 02 号桩',
		loc: '6 栋架空层 · 充电区 C02',
		free: true,
		used: 0,
		sockets: 12,
		slots: mkSlots(12),
		online: false
	}],
	carPiles: [{
		name: 'E 区 · 03 号桩',
		loc: '1 栋地下车库 · 充电区 E03',
		free: true,
		used: 0,
		sockets: 2,
		guns: [{ n: 'A', busy: false }, { n: 'B', busy: false }]
	}, {
		name: 'F 区 · 05 号桩',
		loc: '6 栋地下车库 · 充电区 F05',
		free: true,
		used: 0,
		sockets: 2,
		guns: [{ n: 'A', busy: false }, { n: 'B', busy: false }],
		online: false
	}]
}, {
	name: '云栖小镇',
	zone: '7 栋 · 9 栋 · 12 栋',
	dist: 2800,
	piles: [{
		name: 'A 区 · 11 号桩',
		loc: '7 栋楼下 · 充电区 A11',
		free: true,
		used: 0,
		sockets: 12,
		slots: mkSlots(12)
	}, {
		name: 'B 区 · 04 号桩',
		loc: '9 栋楼下 · 充电区 B04',
		free: true,
		used: 0,
		sockets: 20,
		slots: mkSlots(20),
		online: false
	}, {
		name: 'C 区 · 08 号桩',
		loc: '12 栋地下车库 · 充电区 C08',
		free: true,
		used: 1,
		sockets: 10,
		slots: mkSlots(10, [7])
	}],
	carPiles: [{
		name: 'E 区 · 06 号桩',
		loc: '9 栋地下车库 · 充电区 E06',
		free: true,
		used: 0,
		sockets: 2,
		guns: [{ n: 'A', busy: false }, { n: 'B', busy: false }]
	}, {
		name: 'F 区 · 08 号桩',
		loc: '12 栋地下车库 · 充电区 F08',
		free: false,
		used: 1,
		sockets: 1,
		guns: [{ n: 'A', busy: true }]
	}]
}]

// 充电类型配置：bike 电瓶车（220W）/ car 电动车（7kW · 分时电价）
export const chgTypes = {
	bike: {
		label: '电瓶车充电',
		power: 220,
		powerText: '220W',
		unit: 'W',
		price: 0.60,
		rule: '电费按度 · 服务费按小时',
		kind: '220W 交流',
		elecPrice: 0.60,
		servPrice: 0.10,
		kwhPerH: 0.22,
		volt: 220,
		amp: 1.02
	},
	car: {
		label: '电动车充电',
		power: 7000,
		powerText: '7.0kW',
		unit: 'kW',
		price: 1.20,
		rule: '按电量计费',
		kind: '7kW 交流慢充',
		deposit: 10,
		volt: 220,
		amp: 31.8,
		leftMin: 1.2
	}
}

// 电瓶车充电时长档位（小时，0 = 充满自停）
export const durOptions = [1, 2, 4, 8, 10, 0]

// 电动车充电金额档位（元，0 = 自定义）
export const amtOptions = [20, 30, 50, 80, 100, 0]

// 电瓶车组合费率文案
export const bikePriceText = cfg => '电费 ¥' + cfg.elecPrice.toFixed(2) + '/度 + 服务费 ¥' + cfg.servPrice.toFixed(2) + '/小时'

/**
 * 电瓶车预扣金额：按最高功率 220W（0.22 度/时）满所选时长估算，向上取整到分
 * @param {Number} dur 时长小时（0 = 充满自停，按 8 小时上限预扣）
 */
export const calcBikeDeposit = (dur) => {
	const c = chgTypes.bike
	const h = dur > 0 ? dur : 8
	return Math.ceil(h * (c.kwhPerH * c.elecPrice + c.servPrice) * 100) / 100
}

// 充电月卡套餐
export const mockPlans = [{
	name: '畅充月卡',
	price: 29.9,
	tag: '推荐',
	desc: '30 天不限次 · 每日首充前 4 小时免费',
	color: 'linear-gradient(135deg,#fb923c,#f59e0b)',
	type: 'month'
}, {
	name: '次卡 · 10 次',
	price: 19.9,
	tag: '划算',
	desc: '10 次充电 · 单次上限 8 小时 · 90 天有效',
	color: 'linear-gradient(135deg,#34d399,#10b981)',
	type: 'times'
}, {
	name: '次卡 · 30 次',
	price: 49.9,
	tag: '超值',
	desc: '30 次充电 · 单次上限 8 小时 · 180 天有效',
	color: 'linear-gradient(135deg,#818cf8,#6366f1)',
	type: 'times'
}]

// 钱包充值档位
export const rechargeTiers = [{
	v: 10,
	gift: 0
}, {
	v: 20,
	gift: 0
}, {
	v: 50,
	gift: 0
}]

// 分时电价（尖峰平谷 · 只读，费率由 PC 后台设置）
export const defaultTariffs = [{
	name: '尖段',
	time: '11:00-12:00 · 18:00-19:00',
	elec: 1.50,
	serv: 0.30,
	rate: 1.80,
	color: '#ef4444'
}, {
	name: '峰段',
	time: '08:00-11:00 · 19:00-22:00',
	elec: 1.20,
	serv: 0.30,
	rate: 1.50,
	color: '#f97316'
}, {
	name: '平段',
	time: '07:00-08:00 · 12:00-18:00 · 22:00-23:00',
	elec: 0.95,
	serv: 0.25,
	rate: 1.20,
	color: '#eab308'
}, {
	name: '谷段',
	time: '23:00-07:00',
	elec: 0.40,
	serv: 0.20,
	rate: 0.60,
	color: '#22c55e'
}]

// 当前小时所处时段
export const tariffAt = (h, tariffs) => {
	if ((h >= 11 && h < 12) || (h >= 18 && h < 19)) return tariffs[0]
	if ((h >= 8 && h < 11) || (h >= 19 && h < 22)) return tariffs[1]
	if ((h >= 7 && h < 8) || (h >= 12 && h < 18) || (h >= 22 && h < 23)) return tariffs[2]
	return tariffs[3]
}

// 车辆（我的车辆 · 多车管理 + 月租续费）
export const mockVehicles = [{
	id: 1,
	plate: '浙AD·8K9T2',
	model: '新能源小型汽车',
	comm: '阳光花园',
	bindDate: '2026-03-12',
	expire: '2026-09-08'
}, {
	id: 2,
	plate: '浙BF·5F7M1',
	model: '新能源 SUV',
	comm: '阳光花园',
	bindDate: '2026-05-20',
	expire: ''
}]

// 车辆月租套餐
export const carPlans = [{
	name: '月度套餐',
	months: 1,
	price: 150,
	tag: '最受欢迎'
}, {
	name: '季度套餐',
	months: 3,
	price: 420,
	tag: '省 ¥30'
}, {
	name: '半年套餐',
	months: 6,
	price: 780,
	tag: '省 ¥120'
}]

// 月租续费订单
export const mockRentOrders = [{
	id: 1,
	plate: '浙AD·8K9T2',
	plan: '季度套餐',
	months: 3,
	fee: 420,
	from: '2026-06-09',
	to: '2026-09-08',
	time: '2026-06-09 10:24',
	st: '已完成'
}, {
	id: 2,
	plate: '浙AD·8K9T2',
	plan: '月度套餐',
	months: 1,
	fee: 150,
	from: '2026-05-09',
	to: '2026-06-08',
	time: '2026-05-09 09:12',
	st: '已完成'
}]

// 月卡购买订单（初始演示数据）
export const mockCardOrders = [{
	name: '次卡 · 10 次',
	price: 19.9,
	type: 'times',
	date: '06-15 10:30',
	expire: '7月15日',
	st: '已过期'
}]

// 充电订单（初始演示数据）
export const mockRecords = [{
	pile: 'B 区 · 12 号桩',
	date: '今天 08:12',
	mins: 186,
	fee: 0.75,
	way: '余额',
	st: '已完成',
	held: 2.00,
	type: 'bike',
	kwh: 0.68
}, {
	pile: 'A 区 · 03 号桩',
	date: '昨天 19:40',
	mins: 240,
	fee: 0,
	way: '畅充月卡',
	st: '已完成',
	held: 0,
	type: 'bike',
	kwh: 0.88
}, {
	pile: 'C 区 · 07 号桩',
	date: '07-28 21:05',
	mins: 212,
	fee: 0.85,
	way: '余额',
	st: '已完成',
	held: 1.00,
	type: 'bike',
	kwh: 0.78
}, {
	pile: 'E 区 · 06 号桩',
	date: '08-05 18:22',
	mins: 120,
	fee: 21.60,
	way: '余额',
	st: '已完成',
	held: 30.00,
	type: 'car',
	kwh: 14.00,
	inv: 1,
	invNo: 'INV20260725001',
	invTime: '08-06 10:12',
	invTitle: '个人 · 李明'
}, {
	pile: 'F 区 · 08 号桩',
	date: '08-02 09:15',
	mins: 95,
	fee: 16.80,
	way: '余额',
	st: '已完成',
	held: 20.00,
	type: 'car',
	kwh: 11.10,
	inv: 1,
	invNo: 'INV20260720002',
	invTime: '08-03 08:40',
	invTitle: '个人 · 李明'
}, {
	pile: 'A 区 · 03 号桩',
	date: '07-12 18:22',
	mins: 198,
	fee: 0.60,
	way: '余额',
	st: '已完成',
	held: 1.00,
	type: 'bike',
	kwh: 0.73,
	inv: 1,
	invNo: 'INV20260712001',
	invTime: '07-13 09:12',
	invTitle: '个人 · 李明'
}, {
	pile: 'D 区 · 02 号桩',
	date: '07-30 14:18',
	mins: 186,
	fee: 0,
	way: '次卡抵扣',
	st: '已完成',
	held: 0,
	type: 'bike',
	kwh: 0.71
}]

// 钱包交易明细（充值/支出）
export const mockWalletLogs = [{
	name: '扫码充电 · B 区 12 号桩',
	date: '今天 08:12',
	amt: 0.75,
	in: false,
	days: 0,
	recIdx: 0
}, {
	name: '微信充值',
	date: '2026-07-28 20:30',
	amt: 30,
	in: true,
	days: 8
}, {
	name: '扫码充电 · C 区 07 号桩',
	date: '2026-07-28 21:05',
	amt: 0.85,
	in: false,
	days: 8,
	recIdx: 2
}, {
	name: '微信充值',
	date: '2026-07-15 09:10',
	amt: 50,
	in: true,
	days: 21
}, {
	name: '扫码充电 · A 区 03 号桩',
	date: '2026-07-12 18:22',
	amt: 0.60,
	in: false,
	days: 24,
	recIdx: 5
}, {
	name: '微信充值',
	date: '2026-07-02 14:00',
	amt: 10,
	in: true,
	days: 34
}]

// 消息中心初始数据
export const mockMessages = [{
	id: 1,
	type: 'sys',
	title: '系统公告 · 分时电价调整',
	text: '8 月 1 日起尖峰时段费率上调至 ¥1.20/度，建议错峰充电更省钱。',
	time: '今天 10:02',
	read: false
}, {
	id: 2,
	type: 'charge',
	title: '充电完成 · 已结账',
	text: 'B 区 12 号桩 · 用时 45 分钟 · 费用 ¥0.75，剩余预扣金额已退回余额。',
	time: '今天 08:15',
	read: false
}, {
	id: 3,
	type: 'wallet',
	title: '余额变动提醒',
	text: '充值成功！¥50.00 已到账，当前余额 ¥76.50，随时为您的出行充满能量。',
	time: '昨天 20:31',
	read: true
}, {
	id: 4,
	type: 'card',
	title: '充电月卡即将到期',
	text: '您的充电月卡将于 8 月 15 日到期，请您尽快续费。',
	time: '昨天 09:00',
	read: true
}, {
	id: 5,
	type: 'charge',
	title: '充电已开始',
	text: 'A 区 03 号桩 插座 5 已连接 · 预扣 ¥1.00 · 充满自动断电。',
	time: '07-30 18:22',
	read: true
}]
