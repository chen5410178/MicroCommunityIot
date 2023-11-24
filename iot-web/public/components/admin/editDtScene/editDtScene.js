(function (vc, vm) {

    vc.extends({
        data: {
            editDtSceneInfo: {
                sceneId: '',
                sceneName: '',
                state: '',
                seq: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editDtScene', 'openEditDtSceneModal', function (_params) {
                $that.refreshEditDtSceneInfo();
                $('#editDtSceneModel').modal('show');
                vc.copyObject(_params, $that.editDtSceneInfo);
                $that.editDtSceneInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editDtSceneValidate: function () {
                return vc.validate.validate({
                    editDtSceneInfo: $that.editDtSceneInfo
                }, {
                    'editDtSceneInfo.sceneName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "场景名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "场景名称不能超过64"
                        },
                    ],
                    'editDtSceneInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "状态不能超过12"
                        },
                    ],
                    'editDtSceneInfo.seq': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "场景顺序不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "场景顺序不能超过12"
                        },
                    ],
                    'editDtSceneInfo.sceneId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editDtScene: function () {
                if (!$that.editDtSceneValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/dtScene.updateDtScene',
                    JSON.stringify($that.editDtSceneInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editDtSceneModel').modal('hide');
                            vc.emit('dtScene', 'listDtScene', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditDtSceneInfo: function () {
                $that.editDtSceneInfo = {
                    sceneId: '',
                    sceneName: '',
                    state: '',
                    seq: '',

                }
            }
        }
    });

})(window.vc, window.$that);
