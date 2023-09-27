(function (vc, vm) {

    vc.extends({
        data: {
            deleteEventTemplateParamInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteEventTemplateParam', 'openDeleteEventTemplateParamModal', function (_params) {
                vc.component.deleteEventTemplateParamInfo = _params;
                $('#deleteEventTemplateParamModel').modal('show');
            });
        },
        methods: {
            deleteEventTemplateParam: function () {
                vc.http.apiPost(
                    '/eventTemplateParam.deleteEventTemplateParam',
                    JSON.stringify(vc.component.deleteEventTemplateParamInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteEventTemplateParamModel').modal('hide');
                            vc.emit('eventTemplateParamManage', 'listEventTemplateParam', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteEventTemplateParamModel: function () {
                $('#deleteEventTemplateParamModel').modal('hide');
            }
        }
    });
})(window.vc, window.vc.component);
