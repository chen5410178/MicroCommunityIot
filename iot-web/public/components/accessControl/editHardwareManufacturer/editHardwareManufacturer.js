(function (vc, vm) {

    vc.extends({
        data: {
            editHardwareManufacturerInfo: {
                hmId: '',
                hmName: '',
                version: '',
                protocolImpl: '',
                hmType: '1001',
                author: '',
                link: '',
                license: '',
                prodUrl: '',
                //defaultProtocol: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editHardwareManufacturer', 'openEditHardwareManufacturerModal', function (_params) {
                $that.refreshEditHardwareManufacturerInfo();
                $('#editHardwareManufacturerModel').modal('show');
                vc.copyObject(_params, $that.editHardwareManufacturerInfo);
            });
        },
        methods: {
            editHardwareManufacturerValidate: function () {
                return vc.validate.validate({
                    editHardwareManufacturerInfo: $that.editHardwareManufacturerInfo
                }, {
                    'editHardwareManufacturerInfo.hmId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂商ID不能为空"
                        },
                    ],
                    'editHardwareManufacturerInfo.hmName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂商名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "厂商名称不能超过200"
                        },
                    ],
                    'editHardwareManufacturerInfo.version': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂商协议版本号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "厂商协议版本号不能超过12"
                        },
                    ],
                    'editHardwareManufacturerInfo.protocolImpl': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "协议实现不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "协议实现不能超过200"
                        },
                    ],
                    'editHardwareManufacturerInfo.hmType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "商户类型不能为空"
                        },
                    ],
                    'editHardwareManufacturerInfo.author': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "该协议开发者不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "该协议开发者不能超过64"
                        },
                    ],
                    'editHardwareManufacturerInfo.link': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开发者联系方式不能为空"
                        },
                        {
                            limit: "phone",
                            param: "",
                            errInfo: "开发者联系方式格式错误"
                        },
                    ],
                    'editHardwareManufacturerInfo.license': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "该协议许可不能为空"
                        },
                    ],
                    'editHardwareManufacturerInfo.prodUrl': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "产品官网不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "500",
                            errInfo: "产品官网不能超过500"
                        },
                    ],
                    /*'editHardwareManufacturerInfo.defaultProtocol': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "是否启用该协议不能为空"
                        },
                    ],*/
                });
            },
            editHardwareManufacturer: function () {
                if (!$that.editHardwareManufacturerValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/hm.updateHardwareManufacturer',
                    JSON.stringify($that.editHardwareManufacturerInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editHardwareManufacturerModel').modal('hide');
                            vc.emit('hardwareManufacturerManage', 'listHardwareManufacturer', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditHardwareManufacturerInfo: function () {
                $that.editHardwareManufacturerInfo = {
                    hmId: '',
                    hmName: '',
                    version: '',
                    protocolImpl: '',
                    hmType: '1001',
                    author: '',
                    link: '',
                    license: '',
                    prodUrl: '',
                    //defaultProtocol: '',
                }
            }
        }
    });
})(window.vc, window.$that);
