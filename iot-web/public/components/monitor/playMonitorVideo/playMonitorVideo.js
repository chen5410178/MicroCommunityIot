(function (vc) {

    vc.extends({
        data: {
            playMonitorVideoInfo: {
                machineName: '',
                machineId: '',
                communityId: '',
                remark: '',
                jessibuca: {}
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('playMonitorVideo', 'openPlayMonitorVideoModal', function (_machine) {
                vc.copyObject(_machine, $that.playMonitorVideoInfo);
                $('#playMonitorVideoModel').modal('show');
                $that._queryPlayVideoUrl();
            });
        },
        methods: {

            _queryPlayVideoUrl: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.playMonitorVideoInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorMachine.getPlayVideoUrl',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code != 0) {
                            vc.toast(_json.msg);
                            return;
                        }
                        $that._playWsVideo(_json.data);
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _playWsVideo: function (_url) {

                let _jessibuca = $that.playMonitorVideoInfo.jessibuca;
                if (_jessibuca) {
                    try {
                        _jessibuca.destroy();
                    } catch (err) {

                    }
                }

                let image = document.getElementById("receiver1Div");
                let jessibuca = new Jessibuca({
                    container: image,
                    videoBuffer: 0.2, // 缓存时长
                    isResize: false,
                    text: "",
                    loadingText: "",
                    useMSE: false,
                    debug: false,
                    isNotMute: false,
                },);

                $that.playMonitorVideoInfo.jessibuca = jessibuca;

                jessibuca.play(_url);
                jessibuca.on("start", function (data) {
                    setTimeout(function () {
                        const _videoPhoto = jessibuca.screenshot("test", "jpeg", 0.5, 'base64');
                        $that._saveCameraPhoto($that.playMonitorVideoInfo.machineId, _videoPhoto);
                    }, 1 * 1000);

                });
            },
            _saveCameraPhoto: function (_machineId, _photo) {
                console.log(_machineId, _photo)
                let _data = {
                    communityId: vc.getCurrentCommunity().communityId,
                    machineId: _machineId,
                    photo: _photo
                }
                vc.http.apiPost(
                    '/monitorMachine.saveVideoPhoto',
                    JSON.stringify(_data),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                    },
                    function (errInfo, error) {
                    });
            },
            clearPlayMonitorVideoInfo: function () {
                $that.playMonitorVideoInfo = {
                    machineName: '',
                    machineId: '',
                    communityId: '',
                    remark: '',
                    jessibuca: {}
                };
            },
            _closeVideo:function(){
                let _jessibuca = $that.playMonitorVideoInfo.jessibuca;
                if (_jessibuca) {
                    try {
                        _jessibuca.destroy();
                    } catch (err) {

                    }
                }

                $that.clearPlayMonitorVideoInfo();

                $('#playMonitorVideoModel').modal('hide');
            }
        }
    });
})(window.vc);
