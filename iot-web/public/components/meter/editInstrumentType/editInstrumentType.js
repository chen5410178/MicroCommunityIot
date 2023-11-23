(function (vc, vm) {

    vc.extends({
        data: {
            editInstrumentTypeInfo: {
                typeId: '',
                name: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editInstrumentType', 'openEditInstrumentTypeModal', function (_params) {
                $that.refreshEditInstrumentTypeInfo();
                $('#editInstrumentTypeModel').modal('show');
                vc.copyObject(_params, $that.editInstrumentTypeInfo);
            });
        },
        methods: {
            editInstrumentTypeValidate: function () {
                return vc.validate.validate({
                    editInstrumentTypeInfo: $that.editInstrumentTypeInfo
                }, {
                    'editInstrumentTypeInfo.typeId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "仪表类型不能为空"
                        },
                    ],
                    'editInstrumentTypeInfo.name': [
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
                    'editInstrumentTypeInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editInstrumentTypeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "备注不能超过256"
                        },
                    ],
                });
            },
            editInstrumentType: function () {
                if (!$that.editInstrumentTypeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/instrumentType.updateInstrumentType',
                    JSON.stringify($that.editInstrumentTypeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editInstrumentTypeModel').modal('hide');
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
            refreshEditInstrumentTypeInfo: function () {
                $that.editInstrumentTypeInfo = {
                    typeId: '',
                    name: '',
                    communityId: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
