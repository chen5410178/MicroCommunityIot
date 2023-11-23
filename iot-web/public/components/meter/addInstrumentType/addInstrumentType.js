(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addInstrumentTypeInfo: {
                typeId: '',
                name: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addInstrumentType', 'openAddInstrumentTypeModal', function () {
                $('#addInstrumentTypeModel').modal('show');
            });
        },
        methods: {
            addInstrumentTypeValidate() {
                return vc.validate.validate({
                    addInstrumentTypeInfo: $that.addInstrumentTypeInfo
                }, {
                    'addInstrumentTypeInfo.name': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "类型名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "类型名称不能超过30"
                        },
                    ],
                    'addInstrumentTypeInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addInstrumentTypeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "备注不能超过256"
                        },
                    ],
                });
            },
            saveInstrumentTypeInfo: function () {
                $that.addInstrumentTypeInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addInstrumentTypeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addInstrumentTypeInfo);
                    $('#addInstrumentTypeModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/instrumentType.saveInstrumentType',
                    JSON.stringify($that.addInstrumentTypeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addInstrumentTypeModel').modal('hide');
                            $that.clearAddInstrumentTypeInfo();
                            vc.emit('instrumentTypeManage', 'listInstrumentType', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddInstrumentTypeInfo: function () {
                $that.addInstrumentTypeInfo = {
                    typeId: '',
                    name: '',
                    communityId: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
