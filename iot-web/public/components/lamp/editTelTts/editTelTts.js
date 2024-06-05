(function (vc, vm) {

    vc.extends({
        data: {
            editTelTtsInfo: {
                ttId: '',
                ttsType: '',
                ttsName: '',
                ttsPath: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editTelTts', 'openEditTelTtsModal', function (_params) {
                $that.refreshEditTelTtsInfo();
                $('#editTelTtsModel').modal('show');
                vc.copyObject(_params, $that.editTelTtsInfo);
                $that.editTelTtsInfo.communityId = vc.getCurrentCommunity().communityId;

                if ($that.editTelTtsInfo.ttsPath) {
                    vc.emit('editTelTts', 'uploadFile', 'notifyVedio', $that.editTelTtsInfo.ttsPath)
                }
            });
            vc.on('editTelTts', 'notifyTts', function (_param) {
                $that.editTelTtsInfo.ttsPath = _param.realFileName;
            })
        },
        methods: {
            editTelTtsValidate: function () {
                return vc.validate.validate({
                    editTelTtsInfo: $that.editTelTtsInfo
                }, {
                    'editTelTtsInfo.ttsType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "类型不能超过12"
                        },
                    ],
                    'editTelTtsInfo.ttsName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "语音名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "128",
                            errInfo: "语音名称不能超过128"
                        },
                    ],
                    'editTelTtsInfo.ttsPath': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "语音地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "语音地址不能超过200"
                        },
                    ],
                    'editTelTtsInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editTelTtsInfo.ttId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editTelTts: function () {
                if (!$that.editTelTtsValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/telTts.updateTelTts',
                    JSON.stringify($that.editTelTtsInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editTelTtsModel').modal('hide');
                            vc.emit('telTts', 'listTelTts', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditTelTtsInfo: function () {
                $that.editTelTtsInfo = {
                    ttId: '',
                    ttsType: '',
                    ttsName: '',
                    ttsPath: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
