(function (vc) {

    vc.extends({
        data: {
            addLiftMachineAcInfo: {
                lmaId: '',
                machineId: '',
                machineName:'',
                acMachineId: '',
                acType: '',
                acMachines:[],

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLiftMachineAc', 'openAddLiftMachineAcModal', function (_param) {
                vc.copyObject(_param,$that.addLiftMachineAcInfo);
                $that._listAddAccessControls();
                $('#addLiftMachineAcModel').modal('show');
            });
        },
        methods: {
            addLiftMachineAcValidate() {
                return vc.validate.validate({
                    addLiftMachineAcInfo: $that.addLiftMachineAcInfo
                }, {
                    'addLiftMachineAcInfo.machineId': [
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
                    'addLiftMachineAcInfo.acMachineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁设备ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "门禁设备ID不能超过30"
                        },
                    ],
                    'addLiftMachineAcInfo.acType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "门禁类型不能超过12"
                        },
                    ],
                });
            },
            saveLiftMachineAcInfo: function () {
                if (!$that.addLiftMachineAcValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addLiftMachineAcInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/liftMachineAc.saveLiftMachineAc',
                    JSON.stringify($that.addLiftMachineAcInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addLiftMachineAcModel').modal('hide');
                            $that.clearAddLiftMachineAcInfo();
                            vc.emit('liftMachineAc', 'listLiftMachineAc', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);

                    });
            },
            clearAddLiftMachineAcInfo: function () {
                $that.addLiftMachineAcInfo = {
                    machineId: '',
                    machineName:'',
                    acMachineId: '',
                    acType: '',
                    acMachines:[],
                };
            },
            _listAddAccessControls: function () {
                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addLiftMachineAcInfo.acMachines = _json.data;
               
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);
