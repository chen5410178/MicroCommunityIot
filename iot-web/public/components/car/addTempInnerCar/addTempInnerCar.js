(function(vc) {
    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string,
            //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addTempInnerCarInfo: {
                ticId: '',
                carNum: '',
                startTime: '',
                endTime: '',
                configId: '',
                feeName: '',
                paId: '',
                remark: ''
            }
        },
        _initMethod: function() {
            vc.initDate('addTempInnerCarStartTime', function(_value) {
                $that.addTempInnerCarInfo.startTime = _value;
            });
            vc.initDate('addTempInnerCarEndTime', function(_value) {
                $that.addTempInnerCarInfo.endTime = _value;
            })
        },
        _initEvent: function() {
            vc.on('addTempInnerCar', 'openAddTempInnerCarModal',
                function(_params) {
                    $that.clearAddTempInnerCarInfo();
                    if (_params && _params.configId) {
                        $that.addTempInnerCarInfo.configId = _params.configId;
                        $that.addTempInnerCarInfo.feeName = _params.feeName || '';
                        $that.addTempInnerCarInfo.paId = _params.paId || '';
                    }
                    $('#addTempInnerCarModel').modal('show');
                });
            vc.on('addTempInnerCar', 'notify',
                function(_param) {
                    if (_param.hasOwnProperty('paId')) {
                        $that.addTempInnerCarInfo.paId = _param.paId;
                    }
                });
        },
        methods: {
            addTempInnerCarValidate() {
                return vc.validate.validate({
                    addTempInnerCarInfo: $that.addTempInnerCarInfo
                }, {
                    'addTempInnerCarInfo.carNum': [{
                            limit: "required",
                            param: "",
                            errInfo: "车牌号不能为空"
                        },
                        {
                            limit: "carnumber",
                            errInfo: "车牌号格式不正确"
                        },
                    ],
                    'addTempInnerCarInfo.startTime': [{
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
                    'addTempInnerCarInfo.endTime': [{
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
            saveTempInnerCarInfo: function() {
                if (!$that.addTempInnerCarValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addTempInnerCarInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost('/car.saveTempInnerCar', JSON.stringify($that.addTempInnerCarInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addTempInnerCarModel').modal('hide');
                            $that.clearAddTempInnerCarInfo();
                            vc.emit('tempInnerCar', 'listTempInnerCar', {});
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
            clearAddTempInnerCarInfo: function() {
                $that.addTempInnerCarInfo = {
                    ticId: '',
                    carNum: '',
                    startTime: '',
                    endTime: '',
                    configId: '',
                    feeName: '',
                    paId: '',
                    remark: ''
                };
            },
        }
    });
})(window.vc);

