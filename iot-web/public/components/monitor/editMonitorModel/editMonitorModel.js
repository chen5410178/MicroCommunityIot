(function (vc, vm) {

    vc.extends({
        data: {
            editMonitorModelInfo: {
                modelId: '',
                modelName: '',
                modelAdapt: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editMonitorModel', 'openEditMonitorModelModal', function (_params) {
                $that.refreshEditMonitorModelInfo();
                $('#editMonitorModelModel').modal('show');
                vc.copyObject(_params, $that.editMonitorModelInfo);
                $that.editMonitorModelInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editMonitorModelValidate: function () {
                return vc.validate.validate({
                    editMonitorModelInfo: $that.editMonitorModelInfo
                }, {
                    'editMonitorModelInfo.modelId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型ID不能为空"
                        },
                    ],
                    'editMonitorModelInfo.modelName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "模型名称不能超过200"
                        },
                    ],
                    'editMonitorModelInfo.modelAdapt': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型处理类不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "模型处理类不能超过64"
                        },
                    ],
                });
            },
            editMonitorModel: function () {
                if (!$that.editMonitorModelValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/monitorModel.updateMonitorModel',
                    JSON.stringify($that.editMonitorModelInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editMonitorModelModel').modal('hide');
                            vc.emit('monitorModelManage', 'listMonitorModel', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditMonitorModelInfo: function () {
                $that.editMonitorModelInfo = {
                    modelId: '',
                    modelName: '',
                    modelAdapt: '',
                }
            }
        }
    });
})(window.vc, window.$that);
