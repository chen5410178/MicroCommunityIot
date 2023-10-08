(function (vc) {

    vc.extends({
        data: {
            addEventObjectInfo: {
                eoId: '',
                ruleId: '',
                objType: '',
                communityId: '',
                state: '',
                remark: '',
                objectIdAndNameList: [],
            },
            objectNameOnlyView: [],
        },
        _initMethod: function () {
            $that.addEventObjectInfo.ruleId = vc.getParam('ruleId');
        },
        _initEvent: function () {
            vc.on('addEventObject', 'openAddEventObjectModal', function () {
                $('#addEventObjectModel').modal('show');
            });
            vc.on('', 'listIotMachineData', function (_data) {
                _data.map(machine =>{
                    $that.addEventObjectInfo.objectIdAndNameList.push(
                        {
                            objectId: machine.machineId,
                            objectName: machine.machineName
                        });
                    $that.objectNameOnlyView += machine.machineName + '\r\n';
                })
            });
        },
        methods: {
            addEventObjectValidate() {
                return vc.validate.validate({
                    addEventObjectInfo: $that.addEventObjectInfo
                }, {
                    'addEventObjectInfo.ruleId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规则ID不能为空"
                        },
                    ],
                    'addEventObjectInfo.objType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "对象类型不能为空"
                        },
                    ],
                    'addEventObjectInfo.objectIdAndNameList': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备对象不能为空"
                        },
                    ],
                    'addEventObjectInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addEventObjectInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'addEventObjectInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveEventObjectInfo: function () {

                $that.addEventObjectInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addEventObjectValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventObject.saveEventObject',
                    JSON.stringify($that.addEventObjectInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that.clearAddEventObjectInfo();
                            vc.emit('eventObjectManage', 'listEventObject', {});
                            vc.getBack();
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddEventObjectInfo: function () {
                $that.addEventObjectInfo = {
                    eoId: '',
                    ruleId: '',
                    objType: '',
                    communityId: '',
                    state: '',
                    remark: '',
                    objectIdAndNameList: [],
                };
            },
            queryMachineByObjType: function () {
                if (!$that.addEventObjectInfo.objType) {
                    vc.toast('未选择对象类型');
                    return;
                }
                vc.emit('eventObject', 'listMachine', $that.addEventObjectInfo.objType);
            },
            selectIotMachine: function () {
                $that.addEventObjectInfo.objectIdAndNameList = [];
                $that.objectNameOnlyView = '';
                vc.emit('chooseIotMachine', 'openChooseIotMachineModel', '');
            },
        }
    });

})(window.vc);
