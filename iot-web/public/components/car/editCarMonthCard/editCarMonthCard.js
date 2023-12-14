(function (vc, vm) {

    vc.extends({
        data: {
            editCarMonthCardInfo: {
                cardId: '',
                cardName: '',
                cardMonth: '',
                cardPrice: '',
                communityId: '',
                remark: '',
                paId:'',
                parkingAreas:[]
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editCarMonthCard', 'openEditCarMonthCardModal', function (_params) {
                $that.refreshEditCarMonthCardInfo();
                $that._loadEditParkingAreas();
                $('#editCarMonthCardModel').modal('show');
                vc.copyObject(_params, $that.editCarMonthCardInfo);
                $that.editCarMonthCardInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editCarMonthCardValidate: function () {
                return vc.validate.validate({
                    editCarMonthCardInfo: $that.editCarMonthCardInfo
                }, {
                    'editCarMonthCardInfo.cardName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "名称不能超过200"
                        },
                    ],
                    'editCarMonthCardInfo.cardMonth': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "月不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "月不能超过12"
                        },
                    ],
                    'editCarMonthCardInfo.cardPrice': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "月价不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "月价不能超过12"
                        },
                    ],
                    'editCarMonthCardInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注'不能超过512"
                        },
                    ],
                    'editCarMonthCardInfo.cardId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editCarMonthCard: function () {
                if (!$that.editCarMonthCardValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/carMonth.updateCarMonthCard',
                    JSON.stringify($that.editCarMonthCardInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editCarMonthCardModel').modal('hide');
                            vc.emit('carMonthCard', 'listCarMonthCard', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);
                    });
            },
            refreshEditCarMonthCardInfo: function () {
                $that.editCarMonthCardInfo = {
                    cardId: '',
                    cardName: '',
                    cardMonth: '',
                    cardPrice: '',
                    communityId: '',
                    remark: '',
                    paId:'',
                    parkingAreas:[]
                }
            },
            _loadEditParkingAreas: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/parkingArea.listParkingArea', param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.editCarMonthCardInfo.parkingAreas = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    });
            },
        }
    });

})(window.vc, window.$that);
