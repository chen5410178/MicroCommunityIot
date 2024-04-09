## 配置说明

请到高级设置-》高级网络-》MQTT配置中配置


MQTT配置topic

订阅识别结果: /device/message/up/ivs_result

订阅抓拍结果：/device/message/up/snapshot

发布触发识别：/SN/device/message/down/ivs_trigger

发布触发识别回复：/device/message/down/ivs_trigger/reply

订阅心跳：/device/message/up/keep_alive

发布IO输出事件：/SN/device/message/down/gpio_out

发布IO输出事件回复: /device/message/down/gpio_out/reply

发布串口数据转发：/SN/device/message/down/serial_data

发布串口数据转发回复：/device/message/down/serial_data/reply

其他没有使用到

注意以上topic中的SN 为设备编号