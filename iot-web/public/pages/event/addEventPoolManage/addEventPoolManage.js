(function (vc) {

    vc.extends({
        data: {
            addEventPoolManageInfo: {
                ruleId: '',
                eventType: '',
                eventMsg: '',
                objType: '',
                fromType: 'PERSON',
                fromName: vc.getData('/nav/getUserInfo').name,
                communityId: '',
                state: 'W',
                remark: '',
                objectIdAndNameList: [],
            },
            objectNameOnlyView: [],
            eventRuleList: [],
            eventTypeList: [],
        },
        _initMethod: function () {
            $that.listEventRules();
            vc.getDict('event_pool', 'event_type', function (_data) {
                $that.eventTypeList = _data;
            });
        },
        _initEvent: function () {
            vc.on('', 'listIotMachineData', function (_data) {
                _data.map(machine =>{
                    $that.addEventPoolManageInfo.objectIdAndNameList.push(
                        {
                            objectId: machine.machineId,
                            objectName: machine.machineName
                        });
                    $that.objectNameOnlyView += machine.machineName + '\r\n';
                })
            });
        },
        methods: {
            addEventPoolManageValidate() {
                return vc.validate.validate({
                    addEventPoolManageInfo: $that.addEventPoolManageInfo
                }, {
                    'addEventPoolManageInfo.ruleId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规则ID不能为空"
                        },
                    ],
                    'addEventPoolManageInfo.eventType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "事件类型不能为空"
                        },
                    ],
                    'addEventPoolManageInfo.eventMsg': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "事件描述不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "事件描述不能超过512"
                        },
                    ],
                    'addEventPoolManageInfo.objType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "对象类型不能为空"
                        },
                    ],
                    'addEventPoolManageInfo.objectIdAndNameList': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备对象不能为空"
                        },
                    ],
                    'addEventPoolManageInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addEventPoolManageInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'addEventPoolManageInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveEventPoolManageInfo: function () {
                $that.addEventPoolManageInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addEventPoolManageValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventPool.saveEventPool',
                    JSON.stringify($that.addEventPoolManageInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that.clearAddEventPoolManageInfo();
                            vc.emit('eventPoolManageManage', 'listEventPoolManage', {});
                            vc.getBack();
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddEventPoolManageInfo: function () {
                $that.addEventPoolManageInfo = {
                    ruleId: '',
                    eventType: '',
                    eventMsg: '',
                    objType: '',
                    fromType: 'PERSON',
                    fromName: vc.getData('/nav/getUserInfo').name,
                    communityId: '',
                    state: 'W',
                    remark: '',
                    objectIdAndNameList: [],
                };
            },
            queryMachineByObjType: function () {
                if (!$that.addEventPoolManageInfo.objType) {
                    vc.toast('未选择对象类型');
                    return;
                }
                vc.emit('eventPoolManage', 'listMachine', $that.addEventPoolManageInfo.objType);
            },
            selectIotMachine: function () {
                $that.addEventPoolManageInfo.objectIdAndNameList = [];
                $that.objectNameOnlyView = '';
                vc.emit('chooseIotMachine', 'openChooseIotMachineModel', '');
            },
            listEventRules: function () {
                let param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/eventRule.listEventRule',
                    param,
                    function (json, res) {
                        let _eventRuleManageInfo = JSON.parse(json);
                        $that.eventRuleList = _eventRuleManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
