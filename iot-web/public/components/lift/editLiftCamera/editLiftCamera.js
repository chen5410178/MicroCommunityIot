(function (vc, vm) {

    vc.extends({
        data: {
            editLiftCameraInfo: {
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
            vc.on('editLiftCamera', 'openEditLiftCameraModal', function (_params) {
                $that.refreshEditLiftCameraInfo();
                $('#editLiftCameraModel').modal('show');
                vc.copyObject(_params, $that.editLiftCameraInfo);
                $that.editLiftCameraInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editLiftCameraValidate: function () {
                return vc.validate.validate({
                    editLiftCameraInfo: $that.editLiftCameraInfo
                }, {
                    'editLiftCameraInfo.cameraName': [
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
                    'editLiftCameraInfo.cameraCode': [
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
                    'editLiftCameraInfo.factoryId': [
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
                    'editLiftCameraInfo.liftName': [
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
                    'editLiftCameraInfo.lcId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editLiftCamera: function () {
                if (!$that.editLiftCameraValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/liftCamera.updateLiftCamera',
                    JSON.stringify($that.editLiftCameraInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editLiftCameraModel').modal('hide');
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
            refreshEditLiftCameraInfo: function () {
                $that.editLiftCameraInfo = {
                    lcId: '',
                    cameraName: '',
                    cameraCode: '',
                    factoryId: '',
                    liftName: '',

                }
            }
        }
    });

})(window.vc, window.$that);
