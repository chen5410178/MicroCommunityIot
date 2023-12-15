

/**
 * 停车功能 websocket
 */
export class parkingWebSocket {

    constructor(_url,retDataFunc) {
        this.clientId = vc.uuid();
        this.timeout = 30000;
        this.serverTimeoutObj = null;
        this.pingTime = new Date().getTime();
        this.url = _url;
        this.websocket = null;
        let _that = this;
        

        this.init();

        this.retDataFunc = retDataFunc;

        try{
            vc.on('parkingWebSocket', 'release', function () {
                _that.resetJob();
                _that.release();
            })
        }catch(err){
            
        }
    }

    init() {
        //todo 初始化 websocket
        this.initWebsocket();
    }

    initWebsocket() {

        let _that = this;
        let _protocol = window.location.protocol;
        let url = window.location.host + this.url + "/" + this.clientId;
        if (_protocol.startsWith('https')) {
            url =
                "wss://" + url;
        } else {
            url =
                "ws://" + url;
        }
        if ("WebSocket" in window) {
            this.websocket = new WebSocket(url);
        } else if ("MozWebSocket" in window) {
            this.websocket = new MozWebSocket(url);
        } else {
            this.websocket = new SockJS(url);
        }

        //连接成功建立的回调方法
        this.websocket.onopen = function () {
            _that.startJob();
            _that.onopen();
        };
        //接收到消息的回调方法
        this.websocket.onmessage = function (event) {
            _that.startJob();
            _that.onmessage(event);
        };
        //连接关闭的回调方法
        this.websocket.onclose = function () {
            _that.close();
        };
    }

    onopen() {
        console.log("ws初始化成功");
    }

    onmessage(event) {
        console.log("event", event);
        let _data = event.data;
        try {
            _data = JSON.parse(_data);
        } catch (err) {
            return;
        }
        this.retDataFunc(_data);
    }

    close() {
        console.log("websocket 连接关闭");
    }

    startJob() {
        //todo 重置
        this.resetJob();
        let _that = this;
        this.serverTimeoutObj = setInterval(function () {
            if (_that.websocket.readyState == 1) {
                console.log(_that.url,"连接状态，发送消息保持连接");
                let _pingTime = new Date().getTime();
                //保护，以防 异常
                if (_pingTime - _that.pingTime < 15 * 1000) {
                    return;
                }
                _that.websocket.send("{'cmd':'ping'}");
                _that.pingTime = _pingTime;
                //_that.startJob();
            } else {
                console.log(_that.url,"断开状态，尝试重连");
                _that.release();
                //重新初始化
                _that.init();
            }
        }, this.timeout)
    }


    resetJob() {
        if (!this.serverTimeoutObj) {
            return;
        }
        clearInterval(this.serverTimeoutObj);
    }

    release() {
        if (this.websocket) {
            this.websocket.close();
        }
    }
}