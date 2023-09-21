(function(vc) {
    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string,
            //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addCarBlackWhiteInfo: {
                bwId: '',
                blackWhite: '',
                carNum: '',
                startTime: '',
                endTime: '',
                paId: '',
                parkingAreas: [],
            }
        },
        _initMethod: function() {
            vc.initDate('addCarBlackWhiteStartTime', function(_value) {
                $that.addCarBlackWhiteInfo.startTime = _value;
            });
            vc.initDate('addCarBlackWhiteEndTime', function(_value) {
                $that.addCarBlackWhiteInfo.endTime = _value;
            })
        },
        _initEvent: function() {
            vc.on('addCarBlackWhite', 'openAddCarBlackWhiteModal',
                function() {
                    $('#addCarBlackWhiteModel').modal('show');
                    $that._loadAddParkingBoxs();
                });
            vc.on('addCarBlackWhite', 'notify',
                function(_param) {
                    if (_param.hasOwnProperty('paId')) {
                        $that.addCarBlackWhiteInfo.paId = _param.paId;
                    }
                });
        },
        methods: {
            addCarBlackWhiteValidate() {
                return vc.validate.validate({
                    addCarBlackWhiteInfo: $that.addCarBlackWhiteInfo
                }, {
                    'addCarBlackWhiteInfo.blackWhite': [{
                            limit: "required",
                            param: "",
                            errInfo: "名单类型不能为空"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "名单类型格式错误"
                        }
                    ],
                    'addCarBlackWhiteInfo.carNum': [{
                            limit: "required",
                            param: "",
                            errInfo: "车牌号不能为空"
                        },
                        {
                            limit: "carnumber",
                            errInfo: "车牌号格式不正确"
                        },
                    ],
                    'addCarBlackWhiteInfo.paId': [{
                        limit: "required",
                        param: "",
                        errInfo: "停车场不能为空"
                    }],
                    'addCarBlackWhiteInfo.startTime': [{
                            limit: "required",
                            param: "",
                            errInfo: "开始时间不能为空"
                        },
                        {
                            limit: "date",
                            param: "",
                            errInfo: "不是有效的时间格式"
                        }
                    ],
                    'addCarBlackWhiteInfo.endTime': [{
                            limit: "required",
                            param: "",
                            errInfo: "结束时间不能为空"
                        },
                        {
                            limit: "date",
                            param: "",
                            errInfo: "不是有效的时间格式"
                        }
                    ],
                });
            },
            saveCarBlackWhiteInfo: function() {
                if (!$that.addCarBlackWhiteValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addCarBlackWhiteInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost('/carBlackWhite.saveCarBlackWhite', JSON.stringify($that.addCarBlackWhiteInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCarBlackWhiteModel').modal('hide');
                            $that.clearAddCarBlackWhiteInfo();
                            vc.emit('carBlackWhite', 'listCarBlackWhite', {});
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddCarBlackWhiteInfo: function() {
                $that.addCarBlackWhiteInfo = {
                    blackWhite: '',
                    carNum: '',
                    startTime: '',
                    endTime: '',
                    paId: '',
                    parkingAreas: [],
                };
            },
            _loadAddParkingBoxs: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/parkingArea.listParkingArea', param,
                    function(json, res) {
                        let _parkingAreaManageInfo = JSON.parse(json);
                        $that.addCarBlackWhiteInfo.parkingAreas = _parkingAreaManageInfo.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    });
            },
        }
    });
})(window.vc);