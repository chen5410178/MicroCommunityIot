<template>
	<view class="msg-page" :class="{ 'charge-theme-dark': themeDark }">
		<!-- 普通模式工具栏 -->
		<view class="msg-toolbar" v-if="!editMode">
			<text class="c-text-sub">共 <text style="color:#0f172a;font-weight:700;">{{ list.length }}</text> 条消息</text>
			<view class="msg-toolbar-actions">
				<text class="msg-action" style="color:#059669;" @click="markAllRead">👁 全部已读</text>
				<text class="msg-action" @click="enterEditMode">✏️ 管理</text>
			</view>
		</view>
		<!-- 管理模式工具栏 -->
		<view class="msg-toolbar" v-else>
			<text class="msg-action" @click="selectAll">
				<view class="msg-sel-box" :class="{ on: selected.size === list.length && list.length > 0 }">
					<text v-if="selected.size === list.length && list.length > 0">✓</text>
				</view>
				全选
			</text>
			<view class="msg-toolbar-actions">
				<text class="msg-action" style="color:#f43f5e;" @click="delSelected">🗑 删除 <text style="font-weight:700;">{{ selected.size }}</text></text>
				<text class="msg-action" style="color:#3b82f6;" @click="exitEditMode">完成</text>
			</view>
		</view>

		<!-- 消息列表 -->
		<view class="msg-list">
			<view v-for="m in list" :key="m.id" class="msg-item" :class="{ unread: !m.read }" @click="onItemClick(m)">
				<view class="msg-ic" :style="{ background: msgMeta[m.type] ? msgMeta[m.type].bg : '#f1f5f9', color: msgMeta[m.type] ? msgMeta[m.type].fg : '#64748b' }">
					<text>{{ msgMeta[m.type] ? msgMeta[m.type].icon : '🔔' }}</text>
				</view>
				<view style="flex:1;min-width:0;">
					<view class="msg-title-row">
						<text class="msg-title" :class="{ unread: !m.read }">{{ m.title }}</text>
						<view class="msg-unread-dot" v-if="!m.read"></view>
					</view>
					<text class="msg-text" :class="{ unread: !m.read }">{{ m.text }}</text>
					<text class="msg-time">{{ m.time }}</text>
				</view>
				<view v-if="editMode" class="msg-check" :class="{ on: selected.has(m.id) }" @click.stop="toggleSelect(m.id)">
					<text v-if="selected.has(m.id)">✓</text>
				</view>
			</view>
			<view class="msg-empty" v-if="!list.length">
				<text style="font-size:48rpx;">📭</text>
				<text class="c-text-sub" style="margin-top:16rpx;">暂无消息</text>
			</view>
		</view>

		<!-- 删除确认弹窗 -->
		<view class="c-mask" v-if="delShow" @click="delShow = false"></view>
		<view class="c-dialog" v-if="delShow">
			<view class="msg-dialog-head">
				<view class="msg-dialog-ic">🗑</view>
				<view>
					<text class="c-title" style="display:block;">删除选中消息</text>
					<text class="c-text-sub" style="display:block;margin-top:6rpx;">删除后不可恢复，确定删除吗？</text>
				</view>
			</view>
			<view class="msg-dialog-warn">将删除选中的 <text style="font-weight:700;">{{ selected.size }}</text> 条消息，删除后不可恢复。</view>
			<view class="msg-dialog-btns">
				<view class="c-btn-ghost" style="flex:1;" @click="delShow = false">取消</view>
				<view class="msg-del-btn" style="flex:1;" @click="confirmDel">确认删除</view>
			</view>
		</view>

		<!-- 消息详情弹窗 -->
		<view class="c-mask" v-if="detail" @click="detail = null"></view>
		<view class="c-dialog" v-if="detail">
			<view class="msg-detail-head">
				<view class="msg-detail-ic" :style="{ background: msgMeta[detail.type] ? msgMeta[detail.type].bg : '#f1f5f9', color: msgMeta[detail.type] ? msgMeta[detail.type].fg : '#64748b' }">
					<text style="font-size:36rpx;">{{ msgMeta[detail.type] ? msgMeta[detail.type].icon : '🔔' }}</text>
				</view>
				<view style="flex:1;min-width:0;">
					<text class="c-title" style="display:block;font-size:36rpx;">{{ detail.title }}</text>
					<text class="c-text-sub" style="display:block;margin-top:8rpx;">{{ detail.time }}</text>
				</view>
				<view class="msg-detail-close" @click="detail = null">✕</view>
			</view>
			<view class="msg-detail-text">{{ detail.text }}</view>
			<view class="c-btn-primary" @click="detail = null">知道了</view>
		</view>
	</view>
</template>

<script>
	import chargeStore from '@/api/charge/chargeStore.js'
	import {
		msgMeta
	} from '@/packageCharge/api/mock/messageData.js'

	export default {
		data() {
			return {
				msgMeta,
				editMode: false,
				selected: new Set(),
				delShow: false,
				detail: null
			}
		},
		computed: {
			themeDark() {
				return chargeStore.themeDark
			},
			list() {
				return chargeStore.messages
			}
		},
		onShow() {
			this.editMode = false
			this.selected.clear()
		},
		methods: {
			onItemClick(m) {
				if (this.editMode) {
					this.toggleSelect(m.id)
					return
				}
				if (!m.read) {
					m.read = true
					chargeStore.saveMessages()
				}
				this.detail = m
			},
			markAllRead() {
				this.list.forEach(m => m.read = true)
				chargeStore.saveMessages()
				this._toast('全部消息已读')
			},
			enterEditMode() {
				if (!this.list.length) {
					this._toast('暂无消息可管理')
					return
				}
				this.editMode = true
				this.selected.clear()
			},
			exitEditMode() {
				this.editMode = false
				this.selected.clear()
			},
			toggleSelect(id) {
				if (this.selected.has(id)) this.selected.delete(id)
				else this.selected.add(id)
			},
			selectAll() {
				if (this.selected.size === this.list.length) {
					this.selected.clear()
				} else {
					this.list.forEach(m => this.selected.add(m.id))
				}
			},
			delSelected() {
				if (!this.selected.size) {
					this._toast('请先勾选要删除的消息')
					return
				}
				this.delShow = true
			},
			confirmDel() {
				chargeStore.messages = chargeStore.messages.filter(m => !this.selected.has(m.id))
				chargeStore.saveMessages()
				const n = this.selected.size
				this.selected.clear()
				this.delShow = false
				this._toast('已删除 ' + n + ' 条消息')
				this.exitEditMode()
			},
			_toast(msg) {
				uni.showToast({
					title: msg,
					icon: 'none'
				})
			}
		}
	}
</script>

<style>
	.msg-page {
		padding-bottom: 60rpx;
	}

	.msg-toolbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 24rpx 28rpx 0;
	}

	.msg-toolbar-actions {
		display: flex;
		align-items: center;
		gap: 28rpx;
	}

	.msg-action {
		font-size: 22rpx;
		font-weight: 600;
		color: #64748b;
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.msg-sel-box {
		width: 32rpx;
		height: 32rpx;
		border-radius: 50%;
		border: 2rpx solid #cbd5e1;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 18rpx;
		color: #fff;
	}

	.msg-sel-box.on {
		background: #10b981;
		border-color: #10b981;
	}

	.msg-list {
		margin: 20rpx 28rpx 0;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.msg-item {
		background: #fff;
		border-radius: 28rpx;
		padding: 24rpx;
		display: flex;
		align-items: flex-start;
		gap: 20rpx;
		border: 1rpx solid #f1f5f9;
		box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, .05);
	}

	.msg-item.unread {
		border-color: rgba(16, 185, 129, .3);
	}

	.msg-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
		flex-shrink: 0;
	}

	.msg-title-row {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.msg-title {
		font-size: 24rpx;
		color: #64748b;
		font-weight: 500;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		max-width: 480rpx;
	}

	.msg-title.unread {
		color: #0f172a;
		font-weight: 700;
	}

	.msg-unread-dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background: #ef4444;
		flex-shrink: 0;
	}

	.msg-text {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		font-size: 20rpx;
		color: #94a3b8;
		margin-top: 8rpx;
		line-height: 1.5;
	}

	.msg-text.unread {
		color: #64748b;
	}

	.msg-time {
		display: block;
		font-size: 18rpx;
		color: #cbd5e1;
		margin-top: 12rpx;
	}

	.msg-check {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		border: 2rpx solid #cbd5e1;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 22rpx;
		color: #fff;
		flex-shrink: 0;
	}

	.msg-check.on {
		background: #10b981;
		border-color: #10b981;
	}

	.msg-empty {
		background: #fff;
		border-radius: 28rpx;
		padding: 80rpx 0;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.msg-dialog-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.msg-dialog-ic {
		width: 68rpx;
		height: 68rpx;
		border-radius: 50%;
		background: rgba(239, 68, 68, .1);
		color: #ef4444;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
	}

	.msg-dialog-warn {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: rgba(239, 68, 68, .08);
		padding: 20rpx 24rpx;
		color: #ef4444;
		font-size: 22rpx;
		line-height: 1.6;
	}

	.msg-dialog-btns {
		display: flex;
		gap: 20rpx;
		margin-top: 28rpx;
	}

	.msg-del-btn {
		background: #ef4444;
		color: #fff;
		font-size: 26rpx;
		font-weight: 700;
		border-radius: 24rpx;
		height: 84rpx;
		line-height: 84rpx;
		text-align: center;
	}

	.msg-detail-head {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.msg-detail-ic {
		width: 88rpx;
		height: 88rpx;
		border-radius: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	.msg-detail-close {
		width: 52rpx;
		height: 52rpx;
		border-radius: 50%;
		background: #f1f5f9;
		color: #94a3b8;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 20rpx;
	}

	.msg-detail-text {
		margin-top: 24rpx;
		border-radius: 20rpx;
		background: #f8fafc;
		padding: 24rpx;
		font-size: 26rpx;
		color: #475569;
		line-height: 1.8;
		max-height: 480rpx;
		overflow-y: auto;
	}
</style>
