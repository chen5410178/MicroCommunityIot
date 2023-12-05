(function (vc) {

    vc.extends({
        data: {
            addDtScriptInfo: {
                scriptId: '',
                scriptName: '',
                scriptType: '',
                jsContent: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addDtScript', 'openAddDtScriptModal', function () {
                $('#addDtScriptModel').modal('show');
            });
        },
        methods: {
            addDtScriptValidate() {
                return vc.validate.validate({
                    addDtScriptInfo: $that.addDtScriptInfo
                }, {
                    'addDtScriptInfo.scriptName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "教程名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "教程名称不能超过200"
                        },
                    ],
                    'addDtScriptInfo.scriptType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "脚本类型不能为空"
                        },
                    ],
                    'addDtScriptInfo.jsContent': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "脚本不能为空"
                        }
                    ],
                });
            },
            saveDtScriptInfo: function () {
                if (!$that.addDtScriptValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/dtScript.saveDtScript',
                    JSON.stringify($that.addDtScriptInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addDtScriptModel').modal('hide');
                            $that.clearAddDtScriptInfo();
                            vc.emit('dtScript', 'listDtScript', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);

                    });
            },
            clearAddDtScriptInfo: function () {
                $that.addDtScriptInfo = {
                    scriptName: '',
                    scriptType: '',
                    jsContent: '',

                };
            }
        }
    });

})(window.vc);
