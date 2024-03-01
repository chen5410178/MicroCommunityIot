(function(vc) {

    vc.extends({
        data: {
            worklicensePpsInfo: {
                machineId: '',
                machineCode: '',
                ppsText: '',
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('worklicensePps', 'openWorklicensePpsModal', function() {
                $('#worklicensePpsModel').modal('show');
            });
        },
        methods: {
            worklicensePpsValidate() {
                return vc.validate.validate({
                    worklicensePpsInfo: $that.worklicensePpsInfo
                }, {
                    'worklicensePpsInfo.machineId': [{
                            limit: "required",
                            param: "",
                            errInfo: "设备编码不能为空"
                        }
                    ],
                    'worklicensePpsInfo.ppsText': [{
                            limit: "required",
                            param: "",
                            errInfo: "语音不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "语音不能超过200"
                        },
                    ],
                });
            },
            _playTts: function() {
                if (!$that.worklicensePpsValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/accessControl.saveAccessControl',
                    JSON.stringify($that.worklicensePpsInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#worklicensePpsModel').modal('hide');
                            $that.clearWorklicensePpsInfo();
                            vc.emit('workLicenseMachine', 'listWorkLicenseMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearWorklicensePpsInfo: function() {
                $that.worklicensePpsInfo = {
                    machineId: '',
                    machineCode: '',
                    ppsText: '',
                };
            },
        }
    });

})(window.vc);