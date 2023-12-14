(function (vc) {

    vc.extends({
        data: {
            addMemberCarInfo: {
                memberId: '',
                carId: '',
                carNum: '',
                carType: '',
                remark: '',
                carTypes: []
            }
        },
        _initMethod: function () {
            vc.getDict('owner_car', "car_type", function (_data) {
                $that.addMemberCarInfo.carTypes = _data;
            });
        },
        _initEvent: function () {
            vc.on('addMemberCar', 'openAddMemberCarModal', function (_param) {
                $that.addMemberCarInfo.carId = _param.carId;
                $('#addMemberCarModel').modal('show');
            });
        },
        methods: {
            addMemberCarValidate() {
                return vc.validate.validate({
                    addMemberCarInfo: $that.addMemberCarInfo
                }, {
                    'addMemberCarInfo.carId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "汽车ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "汽车ID不能超过30"
                        },
                    ],
                    'addMemberCarInfo.carNum': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "车牌号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "车牌号不能超过12"
                        },
                    ],
                    'addMemberCarInfo.carType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "车类型不能为空"
                        }
                    ],
                    'addMemberCarInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "备注不能超过200"
                        },
                    ],

                });
            },
            saveMemberCarInfo: function () {
                if (!$that.addMemberCarValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.addMemberCarInfo.communityId = vc.getCurrentCommunity().communityId;


                vc.http.apiPost(
                    '/ownerCar.saveMemberCar',
                    JSON.stringify($that.addMemberCarInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addMemberCarModel').modal('hide');
                            $that.clearAddMemberCarInfo();
                            vc.emit('carDetailMember', 'notify', {});
                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddMemberCarInfo: function () {
                let _carTypes = $that.addMemberCarInfo.carTypes;
                $that.addMemberCarInfo = {
                    carId: '',
                    carNum: '',
                    carType: '',
                    remark: '',
                    carTypes: _carTypes
                };
            }
        }
    });

})(window.vc);
