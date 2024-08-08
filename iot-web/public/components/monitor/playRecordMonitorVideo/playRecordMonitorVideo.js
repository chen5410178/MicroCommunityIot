(function (vc) {

    vc.extends({
        data: {
            playRecordMonitorVideoInfo: {
                machineName: '',
                machineId: '',
                rvId: '',
                communityId: '',
                remark: '',
                jessibuca: {}
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('playRecordMonitorVideo', 'openPlayMonitorVideoModal', function (_machine) {
                vc.copyObject(_machine, $that.playRecordMonitorVideoInfo);
                $('#playRecordMonitorVideoModel').modal('show');
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
                        machineId: $that.playRecordMonitorVideoInfo.machineId,
                        rvId: $that.playRecordMonitorVideoInfo.rvId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorMachine.getRecordPlayVideoUrl',
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

                let _jessibuca = $that.playRecordMonitorVideoInfo.jessibuca;
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

                $that.playRecordMonitorVideoInfo.jessibuca = jessibuca;

                jessibuca.play(_url);
                // jessibuca.on("start", function (data) {
                //     setTimeout(function () {
                //         const _videoPhoto = jessibuca.screenshot("test", "jpeg", 0.5, 'base64');
                //         $that._saveCameraPhoto($that.playRecordMonitorVideoInfo.machineId, _videoPhoto);
                //     }, 1 * 1000);

                // });
            },
           
            clearPlayMonitorVideoInfo: function () {
                $that.playRecordMonitorVideoInfo = {
                    machineName: '',
                    machineId: '',
                    rvId: '',
                    communityId: '',
                    remark: '',
                    jessibuca: {}
                };
            },
            _closeVideo:function(){
                let _jessibuca = $that.playRecordMonitorVideoInfo.jessibuca;
                if (_jessibuca) {
                    try {
                        _jessibuca.destroy();
                    } catch (err) {

                    }
                }

                $that.clearPlayMonitorVideoInfo();

                $('#playRecordMonitorVideoModel').modal('hide');
            }
        }
    });
})(window.vc);
