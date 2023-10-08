(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addEventRuleInfo: {
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
            vc.on('addEventRule', 'openAddEventRuleModal', function () {
                $('#addEventRuleModel').modal('show');
            });
        },
        methods: {
            addEventRuleValidate() {
                return vc.validate.validate({
                    addEventRuleInfo: $that.addEventRuleInfo
                }, {
                    'addEventRuleInfo.ruleName': [
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
                    'addEventRuleInfo.templateId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模板ID不能为空"
                        },
                    ],
                    'addEventRuleInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addEventRuleInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveEventRuleInfo: function () {
                $that.addEventRuleInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addEventRuleValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addEventRuleInfo);
                    $('#addEventRuleModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/eventRule.saveEventRule',
                    JSON.stringify($that.addEventRuleInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addEventRuleModel').modal('hide');
                            $that.clearAddEventRuleInfo();
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
            clearAddEventRuleInfo: function () {
                $that.addEventRuleInfo = {
                    ruleId: '',
                    ruleName: '',
                    templateId: '',
                    communityId: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
