(function (vc, vm) {

    vc.extends({
        data: {
            editMonitorAreaInfo: {
                maId: '',
                maName: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editMonitorArea', 'openEditMonitorAreaModal', function (_params) {
                $that.refreshEditMonitorAreaInfo();
                $('#editMonitorAreaModel').modal('show');
                vc.copyObject(_params, $that.editMonitorAreaInfo);
            });
        },
        methods: {
            editMonitorAreaValidate: function () {
                return vc.validate.validate({
                    editMonitorAreaInfo: $that.editMonitorAreaInfo
                }, {
                    'editMonitorAreaInfo.maId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "区域ID不能为空"
                        },
                    ],
                    'editMonitorAreaInfo.maName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "区域名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "区域名称不能超过64"
                        },
                    ],
                    'editMonitorAreaInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editMonitorAreaInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editMonitorArea: function () {
                if (!$that.editMonitorAreaValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/monitorArea.updateMonitorArea',
                    JSON.stringify($that.editMonitorAreaInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editMonitorAreaModel').modal('hide');
                            vc.emit('monitorAreaManage', 'listMonitorArea', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditMonitorAreaInfo: function () {
                $that.editMonitorAreaInfo = {
                    maId: '',
                    maName: '',
                    communityId: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
