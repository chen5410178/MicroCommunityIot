(function(vc) {

    vc.extends({
        data: {
            addInstrumentInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                communityId: '',
                typeId: '',
                upMin: '',
                checkSec: '',
                implBean: '',
                specs: [],
                sensorList: []
            },
            instrumentTypeList: [],
            factoryList: [],
            sensorTypeList: []
        },
        _initMethod: function() {
            $that._listInstrumentType();
            $that._listFactorys();
            vc.getDict('instrument_sensor', 'sensor_type', function (_data) {
                $that.sensorTypeList = _data;
            })
        },
        _initEvent: function() {

        },
        methods: {
            addInstrumentValidate() {
                return vc.validate.validate({
                    addInstrumentInfo: $that.addInstrumentInfo
                }, {
                    'addInstrumentInfo.machineCode': [{
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
                    'addInstrumentInfo.machineName': [{
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
                    'addInstrumentInfo.communityId': [{
                        limit: "required",
                        param: "",
                        errInfo: "小区不能为空"
                        },
                    ],
                    'addInstrumentInfo.typeId': [{
                            limit: "required",
                            param: "",
                            errInfo: "仪表类型不能为空"
                        },
                    ],
                    'addInstrumentInfo.upMin': [{
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
                    'addInstrumentInfo.checkSec': [{
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
                    'addInstrumentInfo.implBean': [{
                            limit: "required",
                            param: "",
                            errInfo: "仪表厂家不能为空"
                        },
                    ],
                });
            },
            saveInstrumentInfo: function() {
                $that.addInstrumentInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addInstrumentValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/instrument.saveInstrument',
                    JSON.stringify($that.addInstrumentInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            vc.goBack();
                            vc.toast("添加成功");
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
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
                    if (item.factoryId === $that.addInstrumentInfo.implBean) {
                        item.instrumentFactorySpecList.forEach(specItem => {
                            specItem.value = "";
                        })
                        $that.addInstrumentInfo.specs = item.instrumentFactorySpecList;
                    }
                });
            },
            _changeParamType: function (_id) {
                let _sensorList = $that.addInstrumentInfo.sensorList;
                _sensorList.forEach(item => {
                    if (_id === item.id) {
                        item.minValue = null;
                        item.maxValue = null;
                        item.units = null;
                    }
                });
            },
            _addInstrumentSensor: function () {
                let _sensorList = $that.addInstrumentInfo.sensorList;
                let _sensor = {
                    id: vc.uuid(),
                    sensorType: '',
                    paramType: '1001',
                    minValue: null,
                    maxValue: null,
                    units: null
                };
                _sensorList.push(_sensor);
                $that.addInstrumentInfo.sensorList = _sensorList;
            },
            _removeInstrumentSensor: function (item) {
                let _sensorList = $that.addInstrumentInfo.sensorList;
                let index = _sensorList.indexOf(item);
                if (index > -1) {
                    _sensorList.splice(index, 1);
                }
            }
        }
    });

})(window.vc);