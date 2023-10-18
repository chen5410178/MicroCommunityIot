(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMonitorAreaInfo: {
                maId: '',
                maName: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMonitorArea', 'openAddMonitorAreaModal', function () {
                $('#addMonitorAreaModel').modal('show');
            });
        },
        methods: {
            addMonitorAreaValidate() {
                return vc.validate.validate({
                    addMonitorAreaInfo: $that.addMonitorAreaInfo
                }, {
                    'addMonitorAreaInfo.maName': [
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
                    'addMonitorAreaInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addMonitorAreaInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveMonitorAreaInfo: function () {
                $that.addMonitorAreaInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addMonitorAreaValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addMonitorAreaInfo);
                    $('#addMonitorAreaModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/monitorArea.saveMonitorArea',
                    JSON.stringify($that.addMonitorAreaInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addMonitorAreaModel').modal('hide');
                            $that.clearAddMonitorAreaInfo();
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
            clearAddMonitorAreaInfo: function () {
                $that.addMonitorAreaInfo = {
                    maId: '',
                    maName: '',
                    communityId: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
