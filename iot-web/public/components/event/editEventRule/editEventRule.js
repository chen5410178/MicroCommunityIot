(function (vc, vm) {

    vc.extends({
        data: {
            editEventRuleInfo: {
                ruleId: '',
                ruleName: '',
                templateId: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editEventRule', 'openEditEventRuleModal', function (_params) {
                $that.refreshEditEventRuleInfo();
                $('#editEventRuleModel').modal('show');
                vc.copyObject(_params, $that.editEventRuleInfo);
            });
        },
        methods: {
            editEventRuleValidate: function () {
                return vc.validate.validate({
                    editEventRuleInfo: $that.editEventRuleInfo
                }, {
                    'editEventRuleInfo.ruleId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规则ID不能为空"
                        },
                    ],
                    'editEventRuleInfo.ruleName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规则名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "规则名称不能超过64"
                        },
                    ],
                    'editEventRuleInfo.templateId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模板ID不能为空"
                        },
                    ],
                    'editEventRuleInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editEventRuleInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editEventRule: function () {
                if (!$that.editEventRuleValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventRule.updateEventRule',
                    JSON.stringify($that.editEventRuleInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editEventRuleModel').modal('hide');
                            vc.emit('eventRuleManage', 'listEventRule', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditEventRuleInfo: function () {
                $that.editEventRuleInfo = {
                    ruleId: '',
                    ruleName: '',
                    templateId: '',
                    communityId: '',
                    remark: '',
                }
            }
        }
    });

})(window.vc, window.$that);
