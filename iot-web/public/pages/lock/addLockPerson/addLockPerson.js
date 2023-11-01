(function (vc) {

    vc.extends({
        data: {
            addLockPersonInfo: {
                lpId: '',
                machineId: '',
                personId: '',
                name: '',
                startTime: '',
                endTime: '',
                openModel: '',
                state: '1001',
                communityId: '',
                cardNumber: '',
            },
            lockMachineList: [],
        },
        _initMethod: function () {
            $that._listLockMachines();
            vc.initDate('addStartTime', function(_value) {
                $that.addLockPersonInfo.startTime = _value;
            });
            vc.initDate('addEndTime', function(_value) {
                $that.addLockPersonInfo.endTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('addLockPerson', 'chooseOwner', function(_owner) {
                $that.addLockPersonInfo.name = _owner.name;
                $that.addLockPersonInfo.personId = _owner.ownerId;
            });
        },
        methods: {
            addLockPersonValidate() {
                return vc.validate.validate({
                    addLockPersonInfo: $that.addLockPersonInfo
                }, {
                    'addLockPersonInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁设备不能为空"
                        },
                    ],
                    'addLockPersonInfo.personId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "业主不能为空"
                        },
                    ],
                    'addLockPersonInfo.name': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "业主不能为空"
                        },
                    ],
                    'addLockPersonInfo.startTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开始时间不能为空"
                        },
                    ],
                    'addLockPersonInfo.endTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "结束时间不能为空"
                        },
                    ],
                    'addLockPersonInfo.openModel': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开锁方式不能为空"
                        },
                    ],
                    'addLockPersonInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "授权状态不能为空"
                        },
                    ],
                    'addLockPersonInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addLockPersonInfo.cardNumber': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "密码不能超过64"
                        },
                    ],
                });
            },
            saveLockPersonInfo: function () {
                $that.addLockPersonInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addLockPersonValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lockPerson.saveLockPerson',
                    JSON.stringify($that.addLockPersonInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLockPersonModel').modal('hide');
                            $that.clearAddLockPersonInfo();
                            vc.toast("授权成功");
                            vc.goBack();
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLockPersonInfo: function () {
                $that.addLockPersonInfo = {
                    lpId: '',
                    machineId: '',
                    personId: '',
                    name: '',
                    startTime: '',
                    endTime: '',
                    openModel: '',
                    state: '1001',
                    communityId: '',
                    cardNumber: '',
                };
            },
            _listLockMachines: function (_page, _rows) {
                if (!vc.getCurrentCommunity()) {
                    vc.toast("缺少小区信息");
                    return;
                }
                var param = {
                    params: {
                        page: -1,
                        row: 1000,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/lockMachine.listLockMachine',
                    param,
                    function (json, res) {
                        var _lockMachineManageInfo = JSON.parse(json);
                        $that.lockMachineList = _lockMachineManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openChooseOwner: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {});
            },
        }
    });
})(window.vc);
