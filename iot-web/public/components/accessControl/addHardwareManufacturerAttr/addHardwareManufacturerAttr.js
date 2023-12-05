(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addHardwareManufacturerAttrInfo: {
                attrId: '',
                hmId: '',
                specCd: '',
                value: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addHardwareManufacturerAttr', 'openAddHardwareManufacturerAttrModal', function (_hmId) {
                $that.addHardwareManufacturerAttrInfo.hmId = _hmId;
                $('#addHardwareManufacturerAttrModel').modal('show');
            });
        },
        methods: {
            addHardwareManufacturerAttrValidate() {
                return vc.validate.validate({
                    addHardwareManufacturerAttrInfo: $that.addHardwareManufacturerAttrInfo
                }, {
                    'addHardwareManufacturerAttrInfo.hmId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂商ID不能为空"
                        },
                    ],
                    'addHardwareManufacturerAttrInfo.specCd': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "规格不能超过12"
                        },
                    ],
                    'addHardwareManufacturerAttrInfo.value': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格值不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "规格值不能超过200"
                        },
                    ],
                });
            },
            saveHardwareManufacturerAttrInfo: function () {
                if (!$that.addHardwareManufacturerAttrValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addHardwareManufacturerAttrInfo);
                    $('#addHardwareManufacturerAttrModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/hm.saveHardwareManufacturerAttr',
                    JSON.stringify($that.addHardwareManufacturerAttrInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addHardwareManufacturerAttrModel').modal('hide');
                            $that.clearAddHardwareManufacturerAttrInfo();
                            vc.emit('hardwareManufacturerAttrManage', 'listHardwareManufacturerAttr', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddHardwareManufacturerAttrInfo: function () {
                $that.addHardwareManufacturerAttrInfo = {
                    attrId: '',
                    hmId: '',
                    specCd: '',
                    value: '',
                };
            }
        }
    });
})(window.vc);
