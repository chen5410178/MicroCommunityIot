(function (vc, vm) {

    vc.extends({
        data: {
            editVisitTypeInfo: {
                typeId: '',
                name: '',
                visitWay: '',
                visitDay: '',
                auditWay: '',
                remark: '',
                communityId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editVisitType', 'openEditVisitTypeModal', function (_params) {
                $that.refreshEditVisitTypeInfo();
                $('#editVisitTypeModel').modal('show');
                vc.copyObject(_params, $that.editVisitTypeInfo);
            });
        },
        methods: {
            editVisitTypeValidate: function () {
                return vc.validate.validate({
                    editVisitTypeInfo: $that.editVisitTypeInfo
                }, {
                    'editVisitTypeInfo.typeId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "类型ID不能为空"
                        },
                    ],
                    'editVisitTypeInfo.name': [
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
                    'editVisitTypeInfo.visitWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "通行方式不能为空"
                        },
                    ],
                    'editVisitTypeInfo.auditWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核方式不能为空"
                        },
                    ],
                    'editVisitTypeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editVisitTypeInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            editVisitType: function () {
                if ($that.editVisitTypeInfo.visitDay) {
                    if (!vc.validate.num($that.editVisitTypeInfo.visitDay) || $that.editVisitTypeInfo.visitDay <= 0) {
                        vc.toast("拜访天数必须为大于0的整数");
                        return;
                    }
                }
                if (!$that.editVisitTypeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/visitType.updateVisitType',
                    JSON.stringify($that.editVisitTypeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editVisitTypeModel').modal('hide');
                            vc.emit('visitTypeManage', 'listVisitType', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditVisitTypeInfo: function () {
                $that.editVisitTypeInfo = {
                    typeId: '',
                    name: '',
                    visitWay: '',
                    visitDay: '',
                    auditWay: '',
                    remark: '',
                    communityId: '',
                }
            }
        }
    });
})(window.vc, window.$that);
