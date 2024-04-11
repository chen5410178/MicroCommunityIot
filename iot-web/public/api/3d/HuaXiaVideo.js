
export default class HuaXiaVideo {

    constructor(_url, _imageId) {
        this.url = _url
        this.ws = null;
        this.img_1 = document.getElementById(_imageId);
        this.lockReconnect = false;
        this.refreshFlag = true;
        this._initHeartbeat();
    }


    playVideo() {

        if (this.ws) {
            this.ws.close()
        }

        let _that = this;

        this.ws = new WebSocket(this.url);
        this.ws.onopen = function (event) {
            _that.heartCheck.reset().start();
            console.log("open...");
        }
        // let img = new Image();
        // img.onload = function () {
        //     _that.img_1.src = img.src;
        // }
        let oldtime = new Date().valueOf();
        this.ws.onmessage = function (event) {
            let curtime = new Date().valueOf()
            logtime = curtime - oldtime;
            //console.log("当前时间:"+curtime+'-------'+"差值:"+logtime)
            oldtime = curtime;
            _that.heartCheck.reset().start();
            if (typeof (event.data) == "string") { //字符串数据
                //var result = JSON.parse(event.data);
                //console.log(event.data)
            } else { //视频图片流数据  
                let reader = new FileReader();
                reader.readAsDataURL(event.data);
                reader.onload = function (eve) {
                    _that.img_1.src = this.result;
                };
            }
        };
        this.ws.onerror = function (event) {
            console.log("error");
            _that.reconnect();
        }
        this.ws.onclose = function (event) {
            console.log("close");
            _that.reconnect();
        }


    }


    //心跳检测
    _initHeartbeat() {
        let _that = this;
        this.heartCheck = {
            timeout: 3000, //3秒发一次心跳
            timeoutObj: null,
            serverTimeoutObj: null,
            reset: function () {
                clearTimeout(this.timeoutObj);
                clearTimeout(this.serverTimeoutObj);
                return this;
            },
            start: function () {
                let self = this;
                this.timeoutObj = setTimeout(function () {
                    //这里发送一个心跳，后端收到后，返回一个心跳消息，
                    //onmessage拿到返回的心跳就说明连接正常
                    //ws.send("ping");
                    console.log('ping9999')
                    self.serverTimeoutObj = setTimeout(function () {
                        //如果超过一定时间还没重置，说明后端主动断开了
                        //如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                        _that.playVideo()
                    }, self.timeout)
                }, this.timeout)
            }
        }
    }


    reconnect() {
        let _that =this;
        if (this.lockReconnect) return;
        this.lockReconnect = true;
        setTimeout(function () { //没连接上会一直重连，设置延迟避免请求过多
            console.log('重连9999')
            _that.playVideo()
            _that.lockReconnect = false;
        }, 2000);
    }

    getQueryStringValue(name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        let r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    }

    getDecodeQueryStringValue(name) {
        let ser = decodeURI(window.location.search)
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        let r = ser.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    };
}