(function(vc) {

    vc.extends({
        data: {
            worklicenseWhiteLineInfo: {
                machineId: '',
                machineCode: '',
                tel1: '',
                tel2: '',
                tel3: '',
                tel4: '',
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('worklicenseWhiteLine', 'openWorklicenseWhiteLineModal', function(_data) {
                vc.copyObject(_data,$that.worklicenseWhiteLineInfo)
                $that._listWorkLicenseWhiteList($that.worklicenseWhiteLineInfo.machineId);
                $('#worklicenseWhiteLineModel').modal('show');
            });
        },
        methods: {
            worklicenseWhiteLineValidate() {
                return vc.validate.validate({
                    worklicenseWhiteLineInfo: $that.worklicenseWhiteLineInfo
                }, {
                    'worklicenseWhiteLineInfo.machineId': [{
                            limit: "required",
                            param: "",
                            errInfo: "设备编码不能为空"
                        }
                    ],
                    'worklicenseWhiteLineInfo.tel1': [{
                            limit: "required",
                            param: "",
                            errInfo: "号码1不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "13",
                            errInfo: "长度不能超过13"
                        },
                    ],
                    'worklicenseWhiteLineInfo.tel2': [
                        {
                            limit: "maxLength",
                            param: "13",
                            errInfo: "长度不能超过13"
                        },
                    ],
                    'worklicenseWhiteLineInfo.tel3': [
                        {
                            limit: "maxLength",
                            param: "13",
                            errInfo: "长度不能超过13"
                        },
                    ],
                });
            },
            _listWorkLicenseWhiteList: function (machineId) {
                let param = {
                    params: {page:1,row:1,machineId:machineId}
                };
                //发送get请求
                vc.http.apiGet('/whiteList.listWhiteList',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.total == 1){
                            $that.worklicenseWhiteLineInfo = _json.data[0];
                        }else{
                            $that.worklicenseWhiteLineInfo.tel1 = "";
                            $that.worklicenseWhiteLineInfo.tel2 = "";
                            $that.worklicenseWhiteLineInfo.tel3 = "";
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _saveWhiteList: function() {
                if (!$that.worklicenseWhiteLineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/workLicense.saveWhiteList',
                    JSON.stringify($that.worklicenseWhiteLineInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#worklicenseWhiteLineModel').modal('hide');
                            $that.clearWorklicenseWhiteLineInfo();
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
            clearWorklicenseWhiteLineInfo: function() {
                $that.worklicenseWhiteLineInfo = {
                    machineId: '',
                    machineCode: '',
                    tel1: '',
                    tel2: '',
                    tel3: '',
                    tel4: '',
                };
            },
        }
    });

})(window.vc);