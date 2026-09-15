/**
 * 富文本工具类
 *
 * 将后台（PC端 注册协议 Quill 编辑器）保存的 HTML 串转为
 * uni-app rich-text 组件可解析的 nodes
 */

/**
 * 将后台富文本 HTML 转为 uni-app rich-text 可解析的 nodes
 * @param {String} _html 协议 HTML 串
 * @param {Object} _opts { fontSize: 30, lineHeight: 1.9, color: '#475569' }
 */
export function toRichNodes(_html, _opts) {
	let html = (_html || '').trim()
	if (!html) {
		return ''
	}
	let cfg = Object.assign({
		fontSize: 30,
		lineHeight: 1.9,
		color: '#475569'
	}, _opts || {})
	html = html.replace(/<script[\s\S]*?<\/script>/gi, '')
		.replace(/<style[\s\S]*?<\/style>/gi, '')
		.replace(/<iframe[\s\S]*?<\/iframe>/gi, '')
		.replace(/\son\w+\s*=\s*("[^"]*"|'[^']*')/gi, '')
		.replace(/<img([^>]*?)>/gi, (m, attr) => /style=/i.test(attr) ?
			`<img${attr.replace(/style="[^"]*"/i, 'style="max-width:100%;height:auto;"')}>` :
			`<img${attr} style="max-width:100%;height:auto;">`)
	if (!/<[a-zA-Z][^>]*>/.test(html)) {
		html = html.replace(/\n/g, '<br/>')
	}
	return `<div style="font-size:${cfg.fontSize}rpx;line-height:${cfg.lineHeight};color:${cfg.color};word-break:break-all;">${html}</div>`
}
