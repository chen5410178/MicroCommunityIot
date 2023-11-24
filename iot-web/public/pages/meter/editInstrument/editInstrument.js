(function(vc) {

    vc.extends({
        data: {
            editInstrumentInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                communityId: '',
                typeId: '',
                typeName: '',
                charge: '',
                sign: '',
                upMin: '',
                checkSec: '',
                implBean: '',
                factoryName: '',
                instrumentParamList: [],
                instrumentSensorList: [],
                specs: [],
                sensorList: []
            },
            instrumentTypeList: [],
            factoryList: [],
            sensorTypeList: []
        },
        _initMethod: function() {
            $that.editInstrumentInfo.machineId = vc.getParam("machineId");
            $that._listInstrument();
            $that._listInstrumentType();
            $that._listFactorys();
            vc.getDict('instrument_sensor', 'sensor_type', function (_data) {
                $that.sensorTypeList = _data;
            })
        },
        _initEvent: function() {

        },
        methods: {
            editInstrumentValidate() {
                return vc.validate.validate({
                    editInstrumentInfo: $that.editInstrumentInfo
                }, {
                    'editInstrumentInfo.machineId': [{
                        limit: "required",
                        param: "",
                        errInfo: "仪表ID不能为空"
                        },
                    ],
                    'editInstrumentInfo.machineCode': [{
                        limit: "required",
                        param: "",
                        errInfo: "仪表编码不能为空"
                    },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "仪表编码不能超过64"
                        },
                    ],
                    'editInstrumentInfo.machineName': [{
                            limit: "required",
                            param: "",
                            errInfo: "仪表名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "仪表名称不能超过200"
                        },
                    ],
                    'editInstrumentInfo.communityId': [{
                        limit: "required",
                        param: "",
                        errInfo: "小区不能为空"
                        },
                    ],
                    'editInstrumentInfo.typeId': [{
                            limit: "required",
                            param: "",
                            errInfo: "仪表类型不能为空"
                        },
                    ],
                    'editInstrumentInfo.upMin': [{
                            limit: "required",
                            param: "",
                            errInfo: "上报时间不能为空"
                        },
                        {
                            limit: "min",
                            param: "0",
                            errInfo: "上报时间不能低于0"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "上报时间必须为整数"
                        },
                    ],
                    'editInstrumentInfo.checkSec': [{
                            limit: "required",
                            param: "",
                            errInfo: "采样时间不能为空"
                        },
                        {
                            limit: "min",
                            param: "0",
                            errInfo: "采样时间不能低于0"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "采样时间必须为整数"
                        },
                    ],
                    'editInstrumentInfo.implBean': [{
                            limit: "required",
                            param: "",
                            errInfo: "仪表厂家不能为空"
                        },
                    ],
                });
            },
            saveInstrumentInfo: function() {
                $that.editInstrumentInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.editInstrumentValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/instrument.updateInstrument',
                    JSON.stringify($that.editInstrumentInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            vc.goBack();
                            vc.toast("修改成功");
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            _listInstrument: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.editInstrumentInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/instrument.listInstrument',
                    param,
                    function (json, res) {
                        var _instrumentManageInfo = JSON.parse(json);
                        $that._clearEditInstrumentInfo();
                        vc.copyObject(_instrumentManageInfo.data[0], $that.editInstrumentInfo);
                        $that.editInstrumentInfo.specs = $that.editInstrumentInfo.instrumentParamList;
                        $that.editInstrumentInfo.instrumentSensorList.forEach(
                            instrumentSensor => {
                                let _sensorList = $that.editInstrumentInfo.sensorList;
                                let _sensor = {
                                    id: vc.uuid(),
                                    sensorType: instrumentSensor.sensorType,
                                    paramType: instrumentSensor.minValue ? '1001' : '1002',
                                    minValue: instrumentSensor.minValue,
                                    maxValue: instrumentSensor.maxValue,
                                    units: instrumentSensor.units
                                };
                                _sensorList.push(_sensor);
                                $that.editInstrumentInfo.sensorList = _sensorList;
                            }
                        );
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listInstrumentType: function() {
                let param = {
                    params: {
                        page: -1,
                        row: 500,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/instrumentType.listInstrumentType',
                    param,
                    function(json, res) {
                        let _instrumentTypeManageInfo = JSON.parse(json);
                        $that.instrumentTypeList = _instrumentTypeManageInfo.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listFactorys: function() {
                let param = {
                    params: {
                        page: -1,
                        row: 500,
                    }
                };
                //发送get请求
                vc.http.apiGet('/instrumentFactory.listInstrumentFactory',
                    param,
                    function(json, res) {
                        let _instrumentFactoryManageInfo = JSON.parse(json);
                        $that.factoryList = _instrumentFactoryManageInfo.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _changeFactory: function() {
                let _factorys = $that.factoryList;
                _factorys.forEach(item => {
                    if (item.factoryId === $that.editInstrumentInfo.implBean) {
                        item.instrumentFactorySpecList.forEach(specItem => {
                            specItem.value = "";
                        })
                        $that.editInstrumentInfo.specs = item.instrumentFactorySpecList;
                    }
                });
            },
            _changeParamType: function (_id) {
                let _sensorList = $that.editInstrumentInfo.sensorList;
                _sensorList.forEach(item => {
                    if (_id === item.id) {
                        item.minValue = null;
                        item.maxValue = null;
                        item.units = null;
                    }
                });
            },
            _addInstrumentSensor: function () {
                let _sensorList = $that.editInstrumentInfo.sensorList;
                let _sensor = {
                    id: vc.uuid(),
                    sensorType: '',
                    paramType: '1001',
                    minValue: null,
                    maxValue: null,
                    units: null
                };
                _sensorList.push(_sensor);
                $that.editInstrumentInfo.sensorList = _sensorList;
            },
            _removeInstrumentSensor: function (item) {
                let _sensorList = $that.editInstrumentInfo.sensorList;
                let index = _sensorList.indexOf(item);
                if (index > -1) {
                    _sensorList.splice(index, 1);
                }
            },
            _clearEditInstrumentInfo: function () {
                $that.editInstrumentInfo = {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    communityId: '',
                    typeId: '',
                    typeName: '',
                    charge: '',
                    sign: '',
                    upMin: '',
                    checkSec: '',
                    implBean: '',
                    factoryName: '',
                    instrumentParamList: [],
                    instrumentSensorList: [],
                    specs: [],
                    sensorList: []
                };
            }
        }
    });

})(window.vc);