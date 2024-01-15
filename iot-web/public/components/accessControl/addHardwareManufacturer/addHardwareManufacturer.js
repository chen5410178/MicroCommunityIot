(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addHardwareManufacturerInfo: {
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
            vc.on('addHardwareManufacturer', 'openAddHardwareManufacturerModal', function () {
                $('#addHardwareManufacturerModel').modal('show');
            });
        },
        methods: {
            addHardwareManufacturerValidate() {
                return vc.validate.validate({
                    addHardwareManufacturerInfo: $that.addHardwareManufacturerInfo
                }, {
                    'addHardwareManufacturerInfo.hmName': [
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
                    'addHardwareManufacturerInfo.version': [
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
                    'addHardwareManufacturerInfo.protocolImpl': [
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
                    'addHardwareManufacturerInfo.hmType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "商户类型不能为空"
                        },
                    ],
                    'addHardwareManufacturerInfo.author': [
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
                    'addHardwareManufacturerInfo.link': [
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
                    'addHardwareManufacturerInfo.license': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "该协议许可不能为空"
                        },
                    ],
                    'addHardwareManufacturerInfo.prodUrl': [
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
                    /*'addHardwareManufacturerInfo.defaultProtocol': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "是否启用该协议不能为空"
                        },
                    ],*/
                });
            },
            saveHardwareManufacturerInfo: function () {
                if (!$that.addHardwareManufacturerValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addHardwareManufacturerInfo);
                    $('#addHardwareManufacturerModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/hm.saveHardwareManufacturer',
                    JSON.stringify($that.addHardwareManufacturerInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addHardwareManufacturerModel').modal('hide');
                            $that.clearAddHardwareManufacturerInfo();
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
            clearAddHardwareManufacturerInfo: function () {
                $that.addHardwareManufacturerInfo = {
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
                };
            }
        }
    });
})(window.vc);
