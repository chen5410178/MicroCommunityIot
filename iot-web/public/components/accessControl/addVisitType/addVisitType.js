(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addVisitTypeInfo: {
                typeId: '',
                name: '',
                visitWay: '',
                auditWay: '',
                visitDay: '',
                remark: '',
                communityId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addVisitType', 'openAddVisitTypeModal', function () {
                $('#addVisitTypeModel').modal('show');
            });
        },
        methods: {
            addVisitTypeValidate() {
                return vc.validate.validate({
                    addVisitTypeInfo: $that.addVisitTypeInfo
                }, {
                    'addVisitTypeInfo.name': [
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
                    'addVisitTypeInfo.visitWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "通行方式不能为空"
                        },
                    ],
                    'addVisitTypeInfo.auditWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核方式不能为空"
                        },
                    ],
                    'addVisitTypeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'addVisitTypeInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            saveVisitTypeInfo: function () {
                if ($that.addVisitTypeInfo.visitDay) {
                    if (!vc.validate.num($that.addVisitTypeInfo.visitDay) || $that.addVisitTypeInfo.visitDay <= 0) {
                        vc.toast("拜访天数必须为大于0的整数");
                        return;
                    }
                }
                $that.addVisitTypeInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addVisitTypeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addVisitTypeInfo);
                    $('#addVisitTypeModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/visitType.saveVisitType',
                    JSON.stringify($that.addVisitTypeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addVisitTypeModel').modal('hide');
                            $that.clearAddVisitTypeInfo();
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
            clearAddVisitTypeInfo: function () {
                $that.addVisitTypeInfo = {
                    typeId: '',
                    name: '',
                    visitWay: '',
                    auditWay: '',
                    visitDay: '',
                    remark: '',
                    communityId: '',
                };
            }
        }
    });
})(window.vc);
