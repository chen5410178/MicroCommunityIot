(function (vc) {

    vc.extends({
        data: {
            addLiftCameraInfo: {
                lcId: '',
                cameraName: '',
                cameraCode: '',
                factoryId: '',
                liftName: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLiftCamera', 'openAddLiftCameraModal', function () {
                $('#addLiftCameraModel').modal('show');
            });
        },
        methods: {
            addLiftCameraValidate() {
                return vc.validate.validate({
                    addLiftCameraInfo: $that.addLiftCameraInfo
                }, {
                    'addLiftCameraInfo.cameraName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "相机名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "相机名称不能超过64"
                        },
                    ],
                    'addLiftCameraInfo.cameraCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "相机编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "相机编号不能超过64"
                        },
                    ],
                    'addLiftCameraInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "相机厂家不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "相机厂家不能超过30"
                        },
                    ],
                    'addLiftCameraInfo.liftName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "电梯名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "电梯名称不能超过64"
                        },
                    ],
                });
            },
            saveLiftCameraInfo: function () {
                if (!$that.addLiftCameraValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addLiftCameraInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/liftCamera.saveLiftCamera',
                    JSON.stringify($that.addLiftCameraInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addLiftCameraModel').modal('hide');
                            $that.clearAddLiftCameraInfo();
                            vc.emit('liftCamera', 'listLiftCamera', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLiftCameraInfo: function () {
                $that.addLiftCameraInfo = {
                    cameraName: '',
                    cameraCode: '',
                    factoryId: '',
                    liftName: '',

                };
            }
        }
    });

})(window.vc);
