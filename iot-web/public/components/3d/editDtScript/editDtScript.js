(function (vc, vm) {

    vc.extends({
        data: {
            editDtScriptInfo: {
                scriptId: '',
                scriptName: '',
                scriptType: '',
                jsContent: '',
                scriptTypes:[],
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editDtScript', 'openEditDtScriptModal', function (_params) {
                $that.refreshEditDtScriptInfo();
                vc.getDict('dt_modal', 'modal_type', function (_data) {
                    $that.editDtScriptInfo.scriptTypes = _data;
                })
                $('#editDtScriptModel').modal('show');
                vc.copyObject(_params, $that.editDtScriptInfo);
            });
        },
        methods: {
            editDtScriptValidate: function () {
                return vc.validate.validate({
                    editDtScriptInfo: $that.editDtScriptInfo
                }, {
                    'editDtScriptInfo.scriptName': [
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
                    'editDtScriptInfo.scriptType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "脚本类型不能为空"
                        },
                    ],
                    'editDtScriptInfo.jsContent': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "脚本不能为空"
                        },
                    ],
                    'editDtScriptInfo.scriptId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editDtScript: function () {
                if (!$that.editDtScriptValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/dtScript.updateDtScript',
                    JSON.stringify($that.editDtScriptInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editDtScriptModel').modal('hide');
                            vc.emit('dtScript', 'listDtScript', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        vc.toast(errInfo);
                    });
            },
            refreshEditDtScriptInfo: function () {
                $that.editDtScriptInfo = {
                    scriptId: '',
                    scriptName: '',
                    scriptType: '',
                    jsContent: '',
                    scriptTypes:[],
                }
            }
        }
    });

})(window.vc, window.$that);
