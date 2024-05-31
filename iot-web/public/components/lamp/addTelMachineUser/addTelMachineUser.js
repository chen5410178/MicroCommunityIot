(function (vc) {

    vc.extends({
        data: {
            addTelMachineUserInfo: {
                tmuId: '',
                machineId: '',
                staffId: '',
                staffName: '',
                masterFlag: '',
                staffs:[]
            }
        },
        _initMethod: function () {
            $that._listStaffs();

        },
        _initEvent: function () {
            vc.on('addTelMachineUser', 'openAddTelMachineUserModal', function (_param) {
                vc.copyObject(_param,$that.addTelMachineUserInfo);
                $('#addTelMachineUserModel').modal('show');
            });
        },
        methods: {
            addTelMachineUserValidate() {
                return vc.validate.validate({
                    addTelMachineUserInfo: $that.addTelMachineUserInfo
                }, {
                    'addTelMachineUserInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "设备ID不能超过30"
                        },
                    ],
                    'addTelMachineUserInfo.staffId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "操作人不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "操作人不能超过30"
                        },
                    ],
                    'addTelMachineUserInfo.masterFlag': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "默认客服不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "默认客服不能超过12"
                        },
                    ],

                });
            },
            saveTelMachineUserInfo: function () {
                if (!$that.addTelMachineUserValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addTelMachineUserInfo.communityId = vc.getCurrentCommunity().communityId;
           

                vc.http.apiPost(
                    '/telMachineUser.saveTelMachineUser',
                    JSON.stringify($that.addTelMachineUserInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addTelMachineUserModel').modal('hide');
                            $that.clearAddTelMachineUserInfo();
                            vc.emit('telMachineUser', 'listTelMachineUser', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddTelMachineUserInfo: function () {
                let _staffs = $that.addTelMachineUserInfo.staffs;
                $that.addTelMachineUserInfo = {
                    machineId: '',
                    staffId: '',
                    staffName: '',
                    masterFlag: '',
                    staffs:_staffs

                };
            },
            _listStaffs: function(_page, _row) {
                let param = {
                    params: {
                        page: 1,
                        row: 300,
                    }
                };
                //发送get请求
                vc.http.apiGet('/query.staff.infos',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.addTelMachineUserInfo.staffs = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);
