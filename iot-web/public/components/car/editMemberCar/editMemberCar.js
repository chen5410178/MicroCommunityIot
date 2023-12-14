(function (vc, vm) {

    vc.extends({
        data: {
            editMemberCarInfo: {
                memberId: '',
                carId: '',
                carNum: '',
                carType: '',
                remark: '',
                carTypes:[],
            }
        },
        _initMethod: function () {
            vc.getDict('owner_car', "car_type", function (_data) {
                $that.editMemberCarInfo.carTypes = _data;
            });
        },
        _initEvent: function () {
            vc.on('editMemberCar', 'openEditMemberCarModal', function (_params) {
                $that.refreshEditMemberCarInfo();
                $('#editMemberCarModel').modal('show');
                vc.copyObject(_params, $that.editMemberCarInfo);
                $that.editMemberCarInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editMemberCarValidate: function () {
                return vc.validate.validate({
                    editMemberCarInfo: $that.editMemberCarInfo
                }, {
                    'editMemberCarInfo.carId': [
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
                    'editMemberCarInfo.carNum': [
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
                    'editMemberCarInfo.carType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "车类型不能为空"
                        },
                    ],
                    'editMemberCarInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "备注不能超过200"
                        },
                    ],
                    'editMemberCarInfo.memberId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editMemberCar: function () {
                if (!$that.editMemberCarValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/ownerCar.updateOwnerCar',
                    JSON.stringify($that.editMemberCarInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editMemberCarModel').modal('hide');
                            vc.emit('carDetailMember', 'notify', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);
                    });
            },
            refreshEditMemberCarInfo: function () {
                let _carTypes = $that.editMemberCarInfo.carTypes;
                $that.editMemberCarInfo = {
                    memberId: '',
                    carId: '',
                    carNum: '',
                    carType: '',
                    remark: '',
                    carTypes:_carTypes,
                }
            }
        }
    });

})(window.vc, window.$that);
