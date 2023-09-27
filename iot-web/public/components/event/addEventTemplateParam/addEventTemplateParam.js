(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addEventTemplateParamInfo: {
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
            vc.on('addEventTemplateParam', 'openAddEventTemplateParamModal', function (_data) {
                $that.addEventTemplateParamInfo.templateId = _data.templateId;
                $('#addEventTemplateParamModel').modal('show');
            });
        },
        methods: {
            addEventTemplateParamValidate() {
                return vc.validate.validate({
                    addEventTemplateParamInfo: $that.addEventTemplateParamInfo
                }, {
                    'addEventTemplateParamInfo.templateId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模板ID不能为空"
                        },
                    ],
                    'addEventTemplateParamInfo.specCd': [
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
                    'addEventTemplateParamInfo.paramValue': [
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
                    'addEventTemplateParamInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            saveEventTemplateParamInfo: function () {
                $that.addEventTemplateParamInfo.communityId = vc.getCurrentCommunity().communityId;
                
                if (!$that.addEventTemplateParamValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addEventTemplateParamInfo);
                    $('#addEventTemplateParamModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/eventTemplateParam.saveEventTemplateParam',
                    JSON.stringify($that.addEventTemplateParamInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addEventTemplateParamModel').modal('hide');
                            $that.clearAddEventTemplateParamInfo();
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
            clearAddEventTemplateParamInfo: function () {
                $that.addEventTemplateParamInfo = {
                    paramId: '',
                    templateId: '',
                    specCd: '',
                    paramValue: '',
                    communityId: '',
                };
            }
        }
    });
})(window.vc);
