(function (vc) {

    vc.extends({
        data: {
            addDispatchFaceInfo: {
                personName: '',
                faceUrl: '',
                communityId: '',
                startTime: '',
                endTime: '',
                personType:'dispatch'
            }
        },
        _initMethod: function () {
            vc.initDateTime('addStartTime', function (_value) {
                $that.addDispatchFaceInfo.startTime = _value;
            });
            vc.initDateTime('addEndTime', function (_value) {
                $that.addDispatchFaceInfo.endTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('addDispatchFace', 'openAddDispatchFaceModal', function (_param) {
                vc.copyObject(_param, $that.addDispatchFaceInfo);
                $('#addDispatchFaceModel').modal('show');
                vc.emit('addDispatchFace','uploadImageUrl', 'clearImage',{});
            });
            vc.on("addDispatchFace", "notifyUploadImage", function (_param) {
                if (_param.length > 0) {
                    $that.addDispatchFaceInfo.faceUrl = _param[0].url;
                } else {
                    $that.addDispatchFaceInfo.faceUrl = ''
                }
            });
        },
        methods: {
            addDispatchFaceValidate() {
                return vc.validate.validate({
                    addDispatchFaceInfo: $that.addDispatchFaceInfo
                }, {
                    'addDispatchFaceInfo.personName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "人员名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "人员名称不能超过64"
                        },
                    ],
                    'addDispatchFaceInfo.faceUrl': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "人脸地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "人脸地址不能超过256"
                        },
                    ],
                    'addDispatchFaceInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "项目ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "项目ID不能超过64"
                        },
                    ],
                    'addDispatchFaceInfo.startTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "startTime不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "32",
                            errInfo: "startTime不能超过32"
                        },
                    ],
                    'addDispatchFaceInfo.endTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "endTime不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "32",
                            errInfo: "endTime不能超过32"
                        },
                    ],
                });
            },
            savePersonFaceInfo: function () {
                $that.addDispatchFaceInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addDispatchFaceValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }


                vc.http.apiPost(
                    '/ai.saveDispatchFace',
                    JSON.stringify($that.addDispatchFaceInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addDispatchFaceModel').modal('hide');
                            $that.clearAddPersonFaceInfo();
                            vc.emit('dispatchFace', 'listDispatchFace', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddPersonFaceInfo: function () {
                $that.addDispatchFaceInfo = {
                    personName: '',
                    faceUrl: '',
                    communityId: '',
                    startTime: '',
                    endTime: '',
                    personType:'dispatch'

                };
            }
        }
    });

})(window.vc);
