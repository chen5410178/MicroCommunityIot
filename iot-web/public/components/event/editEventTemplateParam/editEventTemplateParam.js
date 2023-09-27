(function (vc, vm) {

    vc.extends({
        data: {
            editEventTemplateParamInfo: {
                paramId: '',
                templateId: '',
                specCd: '',
                paramValue: '',
                communityId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editEventTemplateParam', 'openEditEventTemplateParamModal', function (_params) {
                vc.component.refreshEditEventTemplateParamInfo();
                $('#editEventTemplateParamModel').modal('show');
                vc.copyObject(_params, vc.component.editEventTemplateParamInfo);
                vc.component.editEventTemplateParamInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editEventTemplateParamValidate: function () {
                return vc.validate.validate({
                    editEventTemplateParamInfo: vc.component.editEventTemplateParamInfo
                }, {
                    'editEventTemplateParamInfo.paramId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "参数ID不能为空"
                        },
                    ],
                    'editEventTemplateParamInfo.templateId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模板ID不能为空"
                        },
                    ],
                    'editEventTemplateParamInfo.specCd': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "参数规格不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "参数规格不能超过12"
                        },
                    ],
                    'editEventTemplateParamInfo.paramValue': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "参数值不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "参数值不能超过512"
                        },
                    ],
                    'editEventTemplateParamInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            editEventTemplateParam: function () {
                if (!vc.component.editEventTemplateParamValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventTemplateParam.updateEventTemplateParam',
                    JSON.stringify(vc.component.editEventTemplateParamInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editEventTemplateParamModel').modal('hide');
                            vc.emit('eventTemplateParamManage', 'listEventTemplateParam', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditEventTemplateParamInfo: function () {
                vc.component.editEventTemplateParamInfo = {
                    paramId: '',
                    templateId: '',
                    specCd: '',
                    paramValue: '',
                    communityId: '',
                }
            }
        }
    });
})(window.vc, window.vc.component);
