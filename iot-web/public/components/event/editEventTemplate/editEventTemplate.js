(function (vc, vm) {

    vc.extends({
        data: {
            editEventTemplateInfo: {
                templateId: '',
                templateName: '',
                eventWay: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editEventTemplate', 'openEditEventTemplateModal', function (_params) {
                $that.refreshEditEventTemplateInfo();
                $('#editEventTemplateModel').modal('show');
                vc.copyObject(_params, $that.editEventTemplateInfo);
                $that.editEventTemplateInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editEventTemplateValidate: function () {
                return vc.validate.validate({
                    editEventTemplateInfo: $that.editEventTemplateInfo
                }, {
                    'editEventTemplateInfo.templateId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模板ID不能为空"
                        },
                    ],
                    'editEventTemplateInfo.templateName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模板名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "模板名称不能超过64"
                        },
                    ],
                    'editEventTemplateInfo.eventWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "通知方式不能为空"
                        },
                    ],
                    'editEventTemplateInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editEventTemplateInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editEventTemplate: function () {
                if (!$that.editEventTemplateValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventTemplate.updateEventTemplate',
                    JSON.stringify($that.editEventTemplateInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editEventTemplateModel').modal('hide');
                            vc.emit('eventTemplateManage', 'listEventTemplate', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditEventTemplateInfo: function () {
                $that.editEventTemplateInfo = {
                    templateId: '',
                    templateName: '',
                    eventWay: '',
                    communityId: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
