(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addEventTemplateInfo: {
                templateId: '',
                templateName: '',
                eventWay: '',
                communityId: '',
                remark: '',
                eventTemplateParamKetList: [],
            },
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addEventTemplate', 'openAddEventTemplateModal', function () {
                $('#addEventTemplateModel').modal('show');
            });
        },
        methods: {
            addEventTemplateValidate() {
                return vc.validate.validate({
                    addEventTemplateInfo: $that.addEventTemplateInfo
                }, {
                    'addEventTemplateInfo.templateName': [
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
                    'addEventTemplateInfo.eventWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "通知方式不能为空"
                        },
                    ],
                    'addEventTemplateInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addEventTemplateInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveEventTemplateInfo: function () {
                $that.addEventTemplateInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addEventTemplateValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addEventTemplateInfo);
                    $('#addEventTemplateModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/eventTemplate.saveEventTemplate',
                    JSON.stringify($that.addEventTemplateInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addEventTemplateModel').modal('hide');
                            $that.clearAddEventTemplateInfo();
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
            clearAddEventTemplateInfo: function () {
                $that.addEventTemplateInfo = {
                    templateId: '',
                    templateName: '',
                    eventWay: '',
                    communityId: '',
                    remark: '',
                    eventTemplateParamKetList: [],
                };
            },
            _addChangeEventWay: function () {
                if (!$that.addEventTemplateInfo.eventWay) {
                    return;
                }

                let _param = {
                    params:{
                        eventWay: $that.addEventTemplateInfo.eventWay,
                        page: -1,
                        row: 100
                    }

                }
                //发送get请求
                vc.http.apiGet('/eventTemplateParamKey.listEventTemplateParamKey',
                    _param,
                    function (json, res) {
                        let _eventTemplateParamKeyManageInfo = JSON.parse(json);
                        $that.addEventTemplateInfo.eventTemplateParamKetList = _eventTemplateParamKeyManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
