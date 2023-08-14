(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMachineLocationInfo: {
                locationId: '',
                locationName: '',
                locationType: '',
                locationObjName: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMachineLocation', 'openAddMachineLocationModal', function () {
                $('#addMachineLocationModel').modal('show');
            });
        },
        methods: {
            addMachineLocationValidate() {
                return vc.validate.validate({
                    addMachineLocationInfo: $that.addMachineLocationInfo
                }, {
                    'addMachineLocationInfo.locationName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "位置名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "100",
                            errInfo: "位置名称不能超过100"
                        },
                    ],
                    'addMachineLocationInfo.locationType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "位置类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "位置类型不能超过12"
                        },
                    ],
                    'addMachineLocationInfo.locationObjName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "对象ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "对象ID不能超过256"
                        },
                    ],
                });
            },
            saveMachineLocationInfo: function () {
                if (!$that.addMachineLocationValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.addMachineLocationInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                vc.http.apiPost(
                    '/machineLocation.saveMachineLocation',
                    JSON.stringify($that.addMachineLocationInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addMachineLocationModel').modal('hide');
                            $that.clearAddMachineLocationInfo();
                            vc.emit('machineLocationManage', 'listMachineLocation', {});
                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddMachineLocationInfo: function () {
                $that.addMachineLocationInfo = {
                    locationName: '',
                    locationType: '',
                    locationObjName: '',
                };
            }
        }
    });

})(window.vc);
