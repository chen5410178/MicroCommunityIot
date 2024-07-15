(function (vc) {

    vc.extends({
        data: {
            addMonitorManufactorInfo: {
                mmId: '',
                mmName: '',
                mmUrl: '',
                mmBean: '',
                appId: '',
                appSecure: '',
                remark: '',
                mmBeans:[],

            }
        },
        _initMethod: function () {
            vc.getDict('monitor_manufactor','mm_bean',function(_data){
                $that.addMonitorManufactorInfo.mmBeans = _data;
            });
        },
        _initEvent: function () {
            vc.on('addMonitorManufactor', 'openAddMonitorManufactorModal', function () {
                $('#addMonitorManufactorModel').modal('show');
            });
        },
        methods: {
            addMonitorManufactorValidate() {
                return vc.validate.validate({
                    addMonitorManufactorInfo: $that.addMonitorManufactorInfo
                }, {
                    'addMonitorManufactorInfo.mmName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "厂家名称不能超过64"
                        },
                    ],
                    'addMonitorManufactorInfo.mmUrl': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家系统地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "厂家系统地址不能超过512"
                        },
                    ],
                    'addMonitorManufactorInfo.mmBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "厂家类型不能超过64"
                        },
                    ],
                    'addMonitorManufactorInfo.appId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "APP_ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "APP_ID不能超过64"
                        },
                    ],
                    'addMonitorManufactorInfo.appSecure': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "秘钥不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "秘钥不能超过64"
                        },
                    ],
                    'addMonitorManufactorInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],




                });
            },
            saveMonitorManufactorInfo: function () {
                if (!$that.addMonitorManufactorValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addMonitorManufactorInfo.communityId = vc.getCurrentCommunity().communityId;
              
                vc.http.apiPost(
                    '/monitorManufactor.saveMonitorManufactor',
                    JSON.stringify($that.addMonitorManufactorInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addMonitorManufactorModel').modal('hide');
                            $that.clearAddMonitorManufactorInfo();
                            vc.emit('monitorManufactor', 'listMonitorManufactor', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddMonitorManufactorInfo: function () {
                let _mmBeans = $that.addMonitorManufactorInfo.mmBeans;
                $that.addMonitorManufactorInfo = {
                    mmName: '',
                    mmUrl: '',
                    mmBean: '',
                    appId: '',
                    appSecure: '',
                    remark: '',
                    mmBeans:_mmBeans

                };
            }
        }
    });

})(window.vc);
