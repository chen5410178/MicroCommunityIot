(function (vc, vm) {

    vc.extends({
        data: {
            deleteVisitTypeInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteVisitType', 'openDeleteVisitTypeModal', function (_params) {
                $that.deleteVisitTypeInfo = _params;
                $('#deleteVisitTypeModel').modal('show');
            });
        },
        methods: {
            deleteVisitType: function () {
                vc.http.apiPost(
                    '/visitType.deleteVisitType',
                    JSON.stringify($that.deleteVisitTypeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteVisitTypeModel').modal('hide');
                            vc.emit('visitTypeManage', 'listVisitType', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteVisitTypeModel: function () {
                $('#deleteVisitTypeModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
