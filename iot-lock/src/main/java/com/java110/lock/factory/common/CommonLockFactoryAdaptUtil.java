package com.java110.lock.factory.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class CommonLockFactoryAdaptUtil {

    public static final int ERRCODE_INVALID_ACCESS_TOKEN = 10004;

    public static Map<Integer, String> errorMap = new HashMap<>();
    static {
        errorMap.put(-1007, "该锁不存在键盘密码数据");
        errorMap.put(-2009, "密码不存在");
        errorMap.put(-3006, "密码长度非法，必需为4-9位");
        errorMap.put(-3007, "已存在相同的密码，请更换");
        errorMap.put(-3008, "无法修改从未在锁上使用过的密码");
        errorMap.put(-3009, "自定义密码空间已满，请删除无用密码后再试");
        errorMap.put(1,	"操作失败");
        errorMap.put(10000,	"client_id不存在");
        errorMap.put(10001,	"无效的client，client_id或client_secret错");
        errorMap.put(10003,	"token不存在");
        errorMap.put(10004,	"token无授权，token已失效或被撤销授权");
        errorMap.put(10007,	"username或password错误");
        errorMap.put(10011,	"refresh_token无效");
        errorMap.put(20002,	"不是锁管理员");
        errorMap.put(30002,	"用户名只能包含数字和字母");
        errorMap.put(30003,	"用户已存在");
        errorMap.put(30004,	"要删除的用户的userid不合法，只能删除当前应用注册的账号");
        errorMap.put(30005,	"密码必需MD5加密");
        errorMap.put(30006,	"超过接口调用次数限制");
        errorMap.put(80000,	"请求时间必需为当前时间前后五分钟以内");
        errorMap.put(80002,	"JSON格式不正确");
        errorMap.put(90000,	"系统内部错误");
        errorMap.put(-3, "参数不合法");
        errorMap.put(-2018,	"没有权限");
        errorMap.put(-4063,	"请先删除或转移账号里所有的锁");
        errorMap.put(-1003,	"锁不存在");
        errorMap.put(-2025,	"锁已被冻结，目前无法操作");
        errorMap.put(-3011,	"不能把锁转移给自己");
        errorMap.put(-4043,	"此锁不支持该操作");
        errorMap.put(-4056,	"存储空间已满,操作失败");
        errorMap.put(-4067,	"NB设备未注册，无法发起NB操作");
        errorMap.put(-4082,	"自动闭锁时间超限");
        errorMap.put(-1008,	"钥匙不存在");
        errorMap.put(-1016,	"组名已存在，请重新输入");
        errorMap.put(-1018,	"分组不存在");
        errorMap.put(-1027,	"此账号已被绑定在其它账号上，无法接收电子钥匙");
        errorMap.put(-2019,	"不能给自己的账号发送钥匙");
        errorMap.put(-2020,	"不能发送钥匙给管理员");
        errorMap.put(-2023,	"当前不允许修改钥匙期限");
        errorMap.put(-4064,	"发送失败，接收者账号未注册，请注册后再试");
        errorMap.put(-2012,	"锁附近没有可用的网关");
        errorMap.put(-3002,	"网关离线，请检查后再试");
        errorMap.put(-3003,	"网关正忙，请稍后再试");
        errorMap.put(-3016,	"不能把网关转移给自己");
        errorMap.put(-3034,	"Wifi锁未配置网络，请配置网络后重试");
        errorMap.put(-3035,	"Wifi处于省电模式，请关闭省电模式后重试");
        errorMap.put(-3036,	"锁离线，请检查后再试");
        errorMap.put(-3037,	"锁正忙，请稍后再试");
        errorMap.put(-4037,	"网关不存在");
        errorMap.put(-1021,	"该IC卡已不存在");
        errorMap.put(-1023,	"该指纹已不存在");
    }

    private static Logger logger = LoggerFactory.getLogger(CommonLockFactoryAdaptUtil.class);

}
