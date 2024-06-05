(function (vc) {

    vc.extends({
        data: {
            addTelTtsInfo: {
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
            vc.on('addTelTts', 'openAddTelTtsModal', function () {
                $('#addTelTtsModel').modal('show');
            });
            vc.on('addTelTts', 'notifyTts', function (_param) {
                $that.addTelTtsInfo.ttsPath = _param.realFileName;
            })
        },
        methods: {
            addTelTtsValidate() {
                return vc.validate.validate({
                    addTelTtsInfo: $that.addTelTtsInfo
                }, {
                    'addTelTtsInfo.ttsType': [
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
                    'addTelTtsInfo.ttsName': [
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
                    'addTelTtsInfo.ttsPath': [
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
                    'addTelTtsInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveTelTtsInfo: function () {
                if (!$that.addTelTtsValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addTelTtsInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/telTts.saveTelTts',
                    JSON.stringify($that.addTelTtsInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addTelTtsModel').modal('hide');
                            $that.clearAddTelTtsInfo();
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
            clearAddTelTtsInfo: function () {
                vc.emit('addTelTts', 'uploadFile', 'clearVedio', {})

                $that.addTelTtsInfo = {
                    ttsType: '',
                    ttsName: '',
                    ttsPath: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);
