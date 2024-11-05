
export function _playWsVideo(_data){
    let _jessibuca = _data.jessibuca;
    if (_jessibuca) {
        try {
            _jessibuca.destroy();
        } catch (err) {

        }

    }
    let image = document.getElementById(_data.id);
    let jessibuca = new Jessibuca({
        container: image,
        videoBuffer: 0.2, // 缓存时长
        isResize: false,
        text: "",
        loadingText: "",
        useMSE: false,
        debug: false,
        isNotMute: false,
        supportDblclickFullscreen: true,
        // operateBtns: {
        //     fullscreen: true,
        //     screenshot: true,
        //     play: true,
        //     audio: false,
        //     recorder: false
        // },
    },);

    _data.jessibuca = jessibuca;

    jessibuca.on("videoInfo", function (data) {
        _data.encType = data.encType;
        _data.width = data.width;
        _data.height = data.height;
    });
    jessibuca.on("kBps", function (data) {
        _data.kBps = data;
    });
    jessibuca.on("stats", function (s) {
        //console.log("stats is", s)
        _data.buf = s.buf;
        _data.fps = s.fps;
        _data.abps = s.abps;
        _data.vbps = s.vbps;
        _data.ts = s.ts;
    })


    jessibuca.play(_data.url);
}

export function _getCurMachineInfo(_machineId,_data) {
    let param = {
        params: {
            page: 1,
            row: 1,
            machineId:_machineId,
            communityId:vc.getCurrentCommunity().communityId
        }
    };

    //发送get请求
    vc.http.apiGet('/monitorMachine.getPlayVideoUrl',
        param,
        function (json, res) {
            let _json = JSON.parse(json);
            _data.url = _json.data;
            _playWsVideo(_data);
        }, function (errInfo, error) {
            console.log('请求失败处理');
        }
    );
}

export function toScreenPage(_count){
    vc.jumpToPage('/mv.html#/pages/index/monitorVideo?screenCount='+_count);
}