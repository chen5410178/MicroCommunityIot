(function (vc, vm) {

    vc.extends({
        data: {
            editEventTemplateInfo: {
                templateId: '',
                templateName: '',
                eventWay: '',
                communityId: '',
                remark: '',
                eventTemplateParamKetList: [],
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editEventTemplate', 'openEditEventTemplateModal', function (_params) {
                $that.listEventTemplateParamKey(_params);
                $that.refreshEditEventTemplateInfo();
                vc.copyObject(_params, $that.editEventTemplateInfo);
                $('#editEventTemplateModel').modal('show');
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
                    eventTemplateParamKetList: [],
                }
            },
            listEventTemplateParamKey: function (_params) {
                if (!_params.eventWay) {
                    vc.toast('错误,缺少通知方式');
                    return;
                }

                let _param = {
                    params:{
                        eventWay: _params.eventWay,
                        page: -1,
                        row: 100
                    }

                }
                //发送get请求
                vc.http.apiGet('/eventTemplateParamKey.listEventTemplateParamKey',
                    _param,
                    function (json, res) {
                        let _eventTemplateParamKeyManageInfo = JSON.parse(json);
                        for (let i = 0; i < _eventTemplateParamKeyManageInfo.data.length; i++) {
                            for (let j = 0; j < _params.eventTemplateParamDtoList.length; j++) {
                                if (_eventTemplateParamKeyManageInfo.data[i].specCd === _params.eventTemplateParamDtoList[j].specCd) {
                                    _params.eventTemplateParamDtoList[j].specName = _eventTemplateParamKeyManageInfo.data[i].specName;
                                    break;
                                }
                            }
                        }
                        $that.editEventTemplateInfo.eventTemplateParamKetList = _params.eventTemplateParamDtoList;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc, window.$that);
