/**
 权限组
 **/
(function(vc) {
    vc.extends({
        data: {
            editCarInfo: {
                carId: '',
                memberId: '',
                carNum: '',
                carBrand: '',
                carType: '',
                carColor: '',
                remark: "",
                startTime: '',
                endTime: '',
                carNumType: '',
                leaseType: '',
            },
            carTypes: [{
                    key: '9901',
                    value: '家用小汽车'
                },
                {
                    key: '9902',
                    value: '客车'
                },
                {
                    key: '9903',
                    value: '货车'
                }
            ]
        },
        _initMethod: function() {

            vc.getDict('owner_car', "car_type", function(_data) {
                $that.carTypes = _data;
            });
            //$that._initEditCarDateInfo();
        },
        _initEvent: function() {
            vc.on('editCar', 'openEditCar', function(_carInfo) {
                vc.copyObject(_carInfo, $that.editCarInfo);
                $('#editCarModal').modal('show');
                $that._initEditCarDateInfo();
            });
        },
        methods: {
            editCarValidate: function() {
                return vc.validate.validate({
                    editCarInfo: $that.editCarInfo
                }, {

                    'editCarInfo.carNum': [{
                        limit: "required",
                        param: "",
                        errInfo: "车牌号不能为空"
                    }],
                    'editCarInfo.carType': [{
                        limit: "required",
                        param: "",
                        errInfo: "车类型不能为空"
                    }],
                    'editCarInfo.leaseType': [{
                        limit: "required",
                        param: "",
                        errInfo: "车牌类型不能为空"
                    }],
                    'editCarInfo.startTime': [{
                        limit: "required",
                        param: "",
                        errInfo: "起租时间不能为空"
                    }],
                    'editCarInfo.endTime': [{
                        limit: "required",
                        param: "",
                        errInfo: "结租时间不能为空"
                    }],
                    'editCarInfo.memberId': [{
                        limit: "required",
                        param: "",
                        errInfo: "车辆数据错误"
                    }],
                });
            },
            _submitEditCarInfo: function() {
                if (!$that.editCarValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.editCarInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/ownerCar.updateOwnerCar',
                    JSON.stringify($that.editCarInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json)
                        if (_json.code == 0) {
                            //关闭model
                            $('#editCarModal').modal('hide');
                            vc.emit('listOwnerCar', 'listOwnerCarData', {});
                            vc.emit('simplifyOwnerCar', 'listOwnerCarData', {});
                            vc.emit('ownerDetailCar', 'notify', {});
                            vc.emit('listOwnerCarMember', 'listOwnerCarData', {});
                            vc.emit('carDetail', 'listCarData', {})
                            for (let key in $that.editCarInfo) {
                                $that.editCarInfo[key] = '';
                            }
                            vc.toast("修改成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            _initEditCarDateInfo: function() {
                //$that.editCarInfo.startTime = vc.dateTimeFormat(new Date().getTime());
                $('.editCarStartTime').datetimepicker({
                    language: 'zh-CN',
                    fontAwesome: 'fa',
                    format: 'yyyy-mm-dd hh:ii:ss',
                    initTime: true,
                    initialDate: new Date(),
                    autoClose: 1,
                    todayBtn: true

                });
                $('.editCarStartTime').datetimepicker()
                    .on('changeDate', function(ev) {
                        var value = $(".editCarStartTime").val();
                        $that.editCarInfo.startTime = value;
                    });
                $('.editCarEndTime').datetimepicker({
                    language: 'zh-CN',
                    fontAwesome: 'fa',
                    format: 'yyyy-mm-dd hh:ii:ss',
                    initTime: true,
                    initialDate: new Date(),
                    autoClose: 1,
                    todayBtn: true
                });
                $('.editCarEndTime').datetimepicker()
                    .on('changeDate', function(ev) {
                        var value = $(".editCarEndTime").val();
                        var start = Date.parse(new Date($that.editCarInfo.startTime))
                        var end = Date.parse(new Date(value))
                        if (start - end >= 0) {
                            vc.toast("结租时间必须大于起租时间")
                            $(".editCarEndTime").val('')
                        } else {
                            $that.editCarInfo.endTime = value;
                        }
                    });
            },

        }
    });

})(window.vc);