/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 1;
    vc.extends({
        data: {
            cameraControlVideoInfo: {
                machines: [],
                cameraCount: 4
            }
        },
        _initMethod: function () {
            let _cameraCount = vc.getParam('cameraCount');
            if (_cameraCount) {
                $that.cameraControlVideoInfo.cameraCount = _cameraCount;
            }
            $that._initCamera();
        },
        _initEvent: function () {
            vc.on('cameraControlVideo', 'notify', function (param) {
                $that.cameraControlVideoInfo.machines = param.machines;
                $that.applyViewCamera(_item);
            })
        },
        methods: {
            _initCamera: function () {
                let _machines = [];
                let _cameraCount = $that.cameraControlVideoInfo.cameraCount;
                for (let i = 0; i < _cameraCount; i++) {
                    _machines.push({
                        id: 'cameraVideo' + (i + 1) + 'Div',
                        machineName: '',
                        url: ''
                    });
                }
                $that.cameraControlVideoInfo.machines = _machines;
            },
            _openSelectVideo: function (_item) {
                _item.callback = function (_machine) {
                    console.log(_machine);
                    _item.machineName = _machine.machineName;

                    if (_item.jessibuca) {
                        try {
                            _item.jessibuca.destroy();
                        } catch (err) {

                        }
                    }

                    let param = {
                        params: {
                            page: 1,
                            row: 1,
                            communityId: vc.getCurrentCommunity().communityId,
                            machineId: _machine.machineId
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
                            let image = document.getElementById(_item.id);
                            let jessibuca = new Jessibuca({
                                container: image,
                                videoBuffer: 0.2, // 缓存时长
                                isResize: false,
                                text: "",
                                loadingText: "",
                                useMSE: false,
                                debug: false,
                                isNotMute: false,
                                supportDblclickFullscreen:true,
                                operateBtns: {
                                    fullscreen: true,
                                    screenshot: true,
                                    play: true,
                                    audio: false,
                                    recorder: false
                                },
                            },);
                            _item.jessibuca = jessibuca;
                            jessibuca.play(_json.data);
                            jessibuca.on("start", function (data) {
                                setTimeout(function () {
                                    const _videoPhoto = jessibuca.screenshot("test", "jpeg", 0.5, 'base64');
                                    $that._saveCameraPhoto(_machine.machineId, _videoPhoto);
                                }, 1 * 1000);

                            });

                        }, function (errInfo, error) {
                            console.log('请求失败处理');
                        }
                    );

                }
                vc.emit('selectVideoMachine', 'openSelectVideo', _item);
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

            _changeCount: function (_count) {
                $that.cameraControlVideoInfo.cameraCount = _count;
                vc.jumpToPage('/#/pages/monitor/cameraControlVideo?cameraCount=' + _count)
            }
        }
    });
})(window.vc);