(function (vc, vm) {
    vc.extends({
        data: {
            editMeterTypeInfo: {
                typeId: '',
                typeName: '',
                remark: '',
                price: '',
                typeCd:''
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('editMeterType', 'openEditMeterTypeModal', function (_params) {
                $that.refreshEditMeterTypeInfo();
                $('#editMeterTypeModel').modal('show');
                vc.copyObject(_params, $that.editMeterTypeInfo);
                $that.editMeterTypeInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editMeterTypeValidate: function () {
                return vc.validate.validate({
                    editMeterTypeInfo: $that.editMeterTypeInfo
                }, {
                    'editMeterTypeInfo.typeName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "名称不能超过12"
                        }
                    ],
                    'editMeterTypeInfo.price': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "单价不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "单价不能超过30"
                        }
                    ],
                    'editMeterTypeInfo.remark': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "说明不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "说明不能超过200"
                        }
                    ],
                    'editMeterTypeInfo.typeId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "抄表类型不能为空"
                        }
                    ]
                });
            },
            editMeterType: function () {
                if (!$that.editMeterTypeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/meterType.updateMeterType',
                    JSON.stringify($that.editMeterTypeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editMeterTypeModel').modal('hide');
                            vc.emit('meterTypeManage', 'listMeterType', {});
                            vc.toast("修改成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(errInfo);
                    });
            },
            refreshEditMeterTypeInfo: function () {
                $that.editMeterTypeInfo = {
                    typeId: '',
                    typeName: '',
                    remark: '',
                    price: '',
                    typeCd:''
                }
            }
        }
    });
})(window.vc, window.$that);
