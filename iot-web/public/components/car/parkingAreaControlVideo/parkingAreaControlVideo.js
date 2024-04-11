/**
 入驻小区
 **/
import HuaXiaVideo from "api/3d/HuaXiaVideo.js";
(function (vc) {
    vc.extends({
        data: {
            parkingAreaControlVideoInfo: {
                boxId: '',
                inMachineId: '',
                outMachineId: '',
                inMachines: [],
                outMachines: [],
                inMachineCarNum: '',
                inMachineInOutTime: '',
                inMachineOpen: '',
                inMachineOpenMsg: '',
                outMachineCarNum: '',
                outMachineInOutTime: '',
                outMachineOpen: '',
                outMachineOpenMsg: '',
                jessibuca: {}

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('parkingAreaControlVideo', 'notify', function (param) {
                if (param.hasOwnProperty('boxId')) {
                    $that.parkingAreaControlVideoInfo.boxId = param.boxId;
                    $that._listMachines();
                }
            });

            vc.on('parkingAreaControlVideo', 'carIn', function (param) {
                if ($that.parkingAreaControlVideoInfo.inMachineId != param.machineId) {
                    return;
                }
                $that.parkingAreaControlVideoInfo.inMachineCarNum = param.carNum;
                $that.parkingAreaControlVideoInfo.inMachineInOutTime = param.inOutTime;
                $that.parkingAreaControlVideoInfo.inMachineOpen = param.open;
                $that.parkingAreaControlVideoInfo.inMachineOpenMsg = param.remark;
            });

            vc.on('parkingAreaControlVideo', 'carOut', function (param) {
                if ($that.parkingAreaControlVideoInfo.outMachineId != param.machineId) {
                    return;
                }
                $that.parkingAreaControlVideoInfo.outMachineCarNum = param.carNum;
                $that.parkingAreaControlVideoInfo.outMachineInOutTime = param.inOutTime;
                $that.parkingAreaControlVideoInfo.outMachineOpen = param.open;
                $that.parkingAreaControlVideoInfo.outMachineOpenMsg = param.remark;
            });
        },
        methods: {
            _listMachines: function () {
                let param = {
                    params: {
                        boxId: $that.parkingAreaControlVideoInfo.boxId,
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                }
                //发送get请求
                vc.http.apiGet('/barrier.listBarrier',
                    param,
                    function (json, res) {
                        let _machineManageInfo = JSON.parse(json);
                        let _machines = _machineManageInfo.data;
                        $that.parkingAreaControlVideoInfo.inMachines = [];
                        $that.parkingAreaControlVideoInfo.outMachines = [];
                        _machines.forEach(item => {
                            if (item.direction == '3306') {
                                $that.parkingAreaControlVideoInfo.inMachines.push(item);
                            } else {
                                $that.parkingAreaControlVideoInfo.outMachines.push(item);
                            }
                        });

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _swatchVedio: function () {
                //创建一个socket实例
                let wsUrl = "";
                let _enterMachineId = $that.parkingAreaControlVideoInfo.inMachineId;
                vc.emit('parkingAreaControl', 'notify', {
                    inMachineId: _enterMachineId
                });
                let _hmId = "";
                $that.parkingAreaControlVideoInfo.inMachines.forEach((item) => {
                    if (item.machineId == _enterMachineId) {
                        wsUrl = item.machineIp;
                        _hmId = item.hmId;
                    }
                });

                if(_hmId == "24"){
                    wsUrl = "ws://" + wsUrl;
                    $that._showHuaXiaCameraVideo(wsUrl, 'receiver1');
                    return;
                }

                wsUrl = wsUrl.replace(':8131', ':9080');

                if (wsUrl.indexOf(":") < 0) {
                    wsUrl = wsUrl + ":9080";
                }
                wsUrl += "/ws.flv";
                wsUrl = "ws://" + wsUrl;
                $that._showCameraVideo(wsUrl, 'receiver1')
            },
            _swatchOutVedio: function () {
                //创建一个socket实例
                let wsUrl = "";
                let _outMachineId = $that.parkingAreaControlVideoInfo.outMachineId;
                vc.emit('parkingAreaControl', 'notify', {
                    outMachineId: _outMachineId
                });
                vc.emit('parkingAreaControlFee', 'changeMachine', {
                    machineId: _outMachineId,
                    boxId: $that.parkingAreaControlVideoInfo.boxId
                });

                vc.emit('parkingAreaControlInCar', 'changeMachine', {
                    machineId: _outMachineId,
                    boxId: $that.parkingAreaControlVideoInfo.boxId
                });

                vc.emit('parkingAreaControlCarInouts', 'changeMachine', {
                    machineId: _outMachineId,
                    boxId: $that.parkingAreaControlVideoInfo.boxId
                });



                let paId = "";
                let _hmId = "";
                $that.parkingAreaControlVideoInfo.outMachines.forEach((item) => {
                    if (item.machineId == _outMachineId) {
                        wsUrl = item.machineIp;
                        paId = item.locationObjId;
                        _hmId = item.hmId;
                    }
                });
                if(_hmId == "24"){
                    wsUrl = "ws://" + wsUrl;
                    $that._showHuaXiaCameraVideo(wsUrl, 'receiver2');
                    return;
                }

                wsUrl = wsUrl.replace(':8131', ':9080')
                let _protocol = window.location.protocol;

                if (wsUrl.indexOf(":") < 0) {
                    wsUrl = wsUrl + ":9080";
                }
                wsUrl += "/ws.flv";
                wsUrl =
                    "ws://" + wsUrl;
                $that._showCameraVideo(wsUrl, 'receiver2');
            },
            _showCameraVideo: function (wsUrl, _div) {

                let image = document.getElementById(_div + "Div");
                let jMap = $that.parkingAreaControlVideoInfo.jessibuca;
                let jessibuca = jMap[_div];
                if (jessibuca) {
                    jessibuca.destroy();
                }

                jessibuca = new Jessibuca({
                    container: image,
                    videoBuffer: 0.2, // 缓存时长
                    isResize: false,
                    text: "",
                    loadingText: "",
                    useMSE: false,
                    debug: false,
                    isNotMute: false,
                },);

                jessibuca.play(wsUrl);

                jMap[_div] = jessibuca;

            },
            _showHuaXiaCameraVideo: function (wsUrl, _div) {
                let _huaXiaVideo = new HuaXiaVideo(wsUrl,_div);
                _huaXiaVideo.playVideo();
            },
            _openDoor: function (_inOut) {
                let _machines = [];
                let _machineId = "";
                if (_inOut == 'in') {
                    _machines = $that.parkingAreaControlVideoInfo.inMachines;
                    _machineId = $that.parkingAreaControlVideoInfo.inMachineId;
                } else {
                    _machines = $that.parkingAreaControlVideoInfo.outMachines;
                    _machineId = $that.parkingAreaControlVideoInfo.outMachineId;
                }

                if (_machines.length == 0) {
                    vc.toast('请先选择设备');
                    return;
                }
                let _machineCode = '';
                _machines.forEach(item => {
                    if (item.machineId == _machineId) {
                        _machineCode = item.machineCode;
                    }
                })
                let _data = {
                    "machineCode": _machineCode,
                    "stateName": "开门",
                    "state": "1500",
                    "url": "/barrier.openBarrierDoor",
                    "userRole": "staff",
                    "communityId": vc.getCurrentCommunity().communityId
                };
                vc.http.apiPost('/barrier.openBarrierDoor',
                    JSON.stringify(_data), {
                    emulateJSON: true
                },
                    function (json, res) {
                        let _data = JSON.parse(json);
                        if (_data.code != 0) {
                            vc.toast(_data.msg);
                        } else {
                            vc.toast('已请求设备');
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            _closeDoor: function (_inOut) {
                let _machines = [];
                let _machineId = "";
                if (_inOut == 'in') {
                    _machines = $that.parkingAreaControlVideoInfo.inMachines;
                    _machineId = $that.parkingAreaControlVideoInfo.inMachineId;
                } else {
                    _machines = $that.parkingAreaControlVideoInfo.outMachines;
                    _machineId = $that.parkingAreaControlVideoInfo.outMachineId;
                }

                if (_machines.length == 0) {
                    vc.toast('请先选择设备');
                    return;
                }
                let _machineCode = '';
                _machines.forEach(item => {
                    if (item.machineId == _machineId) {
                        _machineCode = item.machineCode;
                    }
                })
                let _data = {
                    "machineCode": _machineCode,
                    "stateName": "关门",
                    "state": "1500",
                    "url": "/barrier.closeBarrierDoor",
                    "userRole": "staff",
                    "communityId": vc.getCurrentCommunity().communityId
                };
                vc.http.apiPost('/barrier.closeBarrierDoor',
                    JSON.stringify(_data), {
                    emulateJSON: true
                },
                    function (json, res) {
                        let _data = JSON.parse(json);
                        if (_data.code != 0) {
                            vc.toast(_data.msg);
                        } else {
                            vc.toast('已请求设备');
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            customCarIn: function (_type) {
                let _machineId = $that.parkingAreaControlVideoInfo.inMachineId;
                if (_type != '1101') {
                    _machineId = $that.parkingAreaControlVideoInfo.outMachineId;
                }
                if (!_machineId) {
                    vc.toast('请选择摄像头');
                    return;
                }
                vc.emit('parkingAreaControlCustomCarInout', 'open', {
                    type: _type,
                    machineId: _machineId,
                    boxId: $that.parkingAreaControlVideoInfo.boxId
                })
            },
            unlicensedCarIn: function (_type) {
                let _machineId = $that.parkingAreaControlVideoInfo.inMachineId;

                if (!_machineId) {
                    vc.toast('请选择入场摄像头');
                    return;
                }
                vc.emit('unlicensedCarMachineQrCode', 'open', {
                    type: '1101',
                    machineId: _machineId
                })
            },
            _outPayFeeQrCode: function () {
                let _machineId = $that.parkingAreaControlVideoInfo.outMachineId;

                if (!_machineId) {
                    vc.toast('请选择出场摄像头');
                    return;
                }

                vc.emit('barrierGateMachineQrCode', 'openQrCodeModal', {
                    machineId: _machineId,
                    locationObjId: $that.parkingAreaControlVideoInfo.boxId
                });
            },
            openCloudFlvViedo: function (_inOut) {
                let _machineId = '';
                if (_inOut == 'receiver1') {
                    _machineId = $that.parkingAreaControlVideoInfo.inMachineId;
                } else {
                    _machineId = $that.parkingAreaControlVideoInfo.outMachineId;
                }
                let _param = {
                    params: {
                        "machineId": _machineId,
                        "communityId": vc.getCurrentCommunity().communityId
                    }

                };
                vc.http.apiGet('/barrier.getCloudFlvVideo',
                    _param,
                    function (json, res) {
                        let _data = JSON.parse(json);
                        if (_data.code != 0) {
                            vc.toast(_data.msg);
                            return;
                        }
                        console.log(_data)
                        $that._showCameraVideo(_data.data.url, _inOut);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            }


        }
    });
})(window.vc);