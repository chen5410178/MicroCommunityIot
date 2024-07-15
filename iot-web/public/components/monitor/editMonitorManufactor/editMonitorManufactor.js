(function (vc, vm) {

    vc.extends({
        data: {
            editMonitorManufactorInfo: {
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
                $that.editMonitorManufactorInfo.mmBeans = _data;
            });
        },
        _initEvent: function () {
            vc.on('editMonitorManufactor', 'openEditMonitorManufactorModal', function (_params) {
                $that.refreshEditMonitorManufactorInfo();
                $('#editMonitorManufactorModel').modal('show');
                vc.copyObject(_params, $that.editMonitorManufactorInfo);
                $that.editMonitorManufactorInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editMonitorManufactorValidate: function () {
                return vc.validate.validate({
                    editMonitorManufactorInfo: $that.editMonitorManufactorInfo
                }, {
                    'editMonitorManufactorInfo.mmName': [
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
                    'editMonitorManufactorInfo.mmUrl': [
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
                    'editMonitorManufactorInfo.mmBean': [
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
                    'editMonitorManufactorInfo.appId': [
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
                    'editMonitorManufactorInfo.appSecure': [
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
                    'editMonitorManufactorInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editMonitorManufactorInfo.mmId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editMonitorManufactor: function () {
                if (!$that.editMonitorManufactorValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/monitorManufactor.updateMonitorManufactor',
                    JSON.stringify($that.editMonitorManufactorInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editMonitorManufactorModel').modal('hide');
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
            refreshEditMonitorManufactorInfo: function () {
                let _mmBeans = $that.editMonitorManufactorInfo.mmBeans;
                $that.editMonitorManufactorInfo = {
                    mmId: '',
                    mmName: '',
                    mmUrl: '',
                    mmBean: '',
                    appId: '',
                    appSecure: '',
                    remark: '',
                    mmBeans:_mmBeans
                }
            }
        }
    });

})(window.vc, window.$that);
