(function (vc) {

    vc.extends({
        data: {
            addCarMonthCardInfo: {
                cardId: '',
                cardName: '',
                cardMonth: '',
                cardPrice: '',
                communityId: '',
                remark: '',
                paId:'',
                parkingAreas:[],
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addCarMonthCard', 'openAddCarMonthCardModal', function () {
                $that._loadAddParkingAreas();
                $('#addCarMonthCardModel').modal('show');
            });
        },
        methods: {
            addCarMonthCardValidate() {
                return vc.validate.validate({
                    addCarMonthCardInfo: $that.addCarMonthCardInfo
                }, {
                    'addCarMonthCardInfo.cardName': [
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
                    'addCarMonthCardInfo.cardMonth': [
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
                    'addCarMonthCardInfo.cardPrice': [
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
                    'addCarMonthCardInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveCarMonthCardInfo: function () {
                if (!$that.addCarMonthCardValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addCarMonthCardInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/carMonth.saveCarMonthCard',
                    JSON.stringify($that.addCarMonthCardInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCarMonthCardModel').modal('hide');
                            $that.clearAddCarMonthCardInfo();
                            vc.emit('carMonthCard', 'listCarMonthCard', {});

                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddCarMonthCardInfo: function () {
                $that.addCarMonthCardInfo = {
                    cardName: '',
                    cardMonth: '',
                    cardPrice: '',
                    communityId: '',
                    remark: '',
                    paId:'',
                    parkingAreas:[],
                };
            },
            _loadAddParkingAreas: function() {
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
                        $that.addCarMonthCardInfo.parkingAreas = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    });
            },
        }
    });

})(window.vc);
