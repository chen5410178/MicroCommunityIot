(function (vc) {
    vc.extends({
        data: {
            editFloorInfo: {
                floorId: '',
                floorName: '',
                floorNum: '',
                floorArea: '',
                remark: '',
                errorInfo: '',
                seq: '',
                lat: 0,
                lng: 0
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('editFloor', 'openEditFloorModal', function (_floor) {
                $that.editFloorInfo.errorInfo = "";
                vc.copyObject(_floor, $that.editFloorInfo);
                $('#editFloorModel').modal('show');

                setTimeout(function () {
                    vc.emit('editMap', 'showMap', $that.editFloorInfo);
                }, 1000)

            });
        },
        methods: {
            editFloorValidate() {
                return vc.validate.validate({
                    editFloorInfo: $that.editFloorInfo
                }, {
                    'editFloorInfo.floorName': [{
                        limit: "required",
                        param: "",
                        errInfo: "楼名称不能为空"
                    },
                        {
                            limit: "maxin",
                            param: "1,64",
                            errInfo: "楼名称长度必须在2位至64位"
                        },
                    ],
                    'editFloorInfo.floorNum': [{
                        limit: "required",
                        param: "",
                        errInfo: "楼编号不能为空"
                    },
                        {
                            limit: "maxin",
                            param: "1,64",
                            errInfo: "楼编号长度必须在1位至64位"
                        },
                    ],
                    'editFloorInfo.floorArea': [{
                        limit: "required",
                        param: "",
                        errInfo: "建筑面积不能为空"
                    },
                        {
                            limit: "money",
                            param: "",
                            errInfo: "建筑面积错误，如300.00"
                        },
                    ],
                    'editFloorInfo.seq': [{
                            limit: "required",
                            param: "",
                            errInfo: "排序不能为空"
                        },
                        {
                            limit: "number",
                            param: "",
                            errInfo: "排序必须是数字"
                        },
                    ],
                    'editFloorInfo.remark': [

                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "备注长度不能超过200位"
                        }
                    ]
                });
            },
            editFloorMethod: function () {
                if (!$that.editFloorValidate()) {
                    $that.editFloorInfo.errorInfo = vc.validate.errInfo;
                    return;
                }
                $that.editFloorInfo.errorInfo = "";
                $that.editFloorInfo.communityId = vc.getCurrentCommunity().communityId;
                $that.editFloorInfo.name = $that.editFloorInfo.floorName;
                vc.http.apiPost(
                    '/floor.editFloor',
                    JSON.stringify($that.editFloorInfo), {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editFloorModel').modal('hide');
                            $that.clearEditFloorInfo();
                            vc.emit('listFloor', 'listFloorData', {});
                            vc.emit('floorUnitTree', 'refreshTree', {
                                floorId: $that.editFloorInfo.floorId
                            })
                            vc.toast("修改成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        $that.editFloorInfo.errorInfo = errInfo;
                    });
            },
            clearEditFloorInfo: function () {
                $that.editFloorInfo = {
                    floorId: '',
                    floorName: '',
                    floorNum: '',
                    floorArea: '',
                    remark: '',
                    errorInfo: '',
                    seq: '',
                    lat: 0,
                lng: 0
                };
            }
        }
    });
})(window.vc);