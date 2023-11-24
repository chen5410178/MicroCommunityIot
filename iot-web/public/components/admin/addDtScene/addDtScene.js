(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addDtSceneInfo: {
                sceneId: '',
                sceneName: '',
                state: 'Y',
                seq: '1',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addDtScene', 'openAddDtSceneModal', function () {
                $('#addDtSceneModel').modal('show');
            });
        },
        methods: {
            addDtSceneValidate() {
                return vc.validate.validate({
                    addDtSceneInfo: $that.addDtSceneInfo
                }, {
                    'addDtSceneInfo.sceneName': [
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
                    'addDtSceneInfo.state': [
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
                    'addDtSceneInfo.seq': [
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




                });
            },
            saveDtSceneInfo: function () {
                if (!$that.addDtSceneValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addDtSceneInfo.communityId = vc.getCurrentCommunity().communityId;
        
                vc.http.apiPost(
                    '/dtScene.saveDtScene',
                    JSON.stringify($that.addDtSceneInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addDtSceneModel').modal('hide');
                            $that.clearAddDtSceneInfo();
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
            clearAddDtSceneInfo: function () {
                $that.addDtSceneInfo = {
                    sceneName: '',
                    state: 'Y',
                    seq: '1',

                };
            }
        }
    });

})(window.vc);
