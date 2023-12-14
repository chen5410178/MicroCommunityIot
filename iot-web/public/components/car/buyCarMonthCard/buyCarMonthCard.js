(function (vc) {

    vc.extends({
        data: {
            buyCarMonthCardInfo: {
                orderId: '',
                cardId: '',
                carNum: '',
                carId:'',
                primeRate: '',
                receivableAmount: '',
                receivedAmount: '',
                remark: '',
                cards:[],
                primeRates:[]
            }
        },
        _initMethod: function () {
            vc.getDict('pay_fee_detail', "prime_rate", function(_data) {
                $that.buyCarMonthCardInfo.primeRates = _data;
            });
        },
        _initEvent: function () {
            vc.on('buyCarMonthCard', 'openBuyCarMonthCardModal', function () {
                $('#buyCarMonthCardModel').modal('show');
            });
        },
        methods: {
            buyCarMonthCardValidate() {
                return vc.validate.validate({
                    buyCarMonthCardInfo: $that.buyCarMonthCardInfo
                }, {
                    'buyCarMonthCardInfo.cardId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "月卡不能为空"
                        },
                    ],
                    'buyCarMonthCardInfo.carNum': [
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
                    'buyCarMonthCardInfo.primeRate': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "支付方式不能为空"
                        }
                    ],
                    'buyCarMonthCardInfo.receivedAmount': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "实收金额不能为空"
                        }
                    ],

                    'buyCarMonthCardInfo.remark': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "备注不能为空"
                        }
                    ],
                });
            },
            saveCarMonthOrderInfo: function () {
                if (!$that.buyCarMonthCardValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.buyCarMonthCardInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/carMonth.buyCarMonthOrder',
                    JSON.stringify($that.buyCarMonthCardInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#buyCarMonthCardModel').modal('hide');
                            $that.clearBuyCarMonthCardInfo();
                            vc.emit('carMonthOrder', 'listCarMonthOrder', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearBuyCarMonthCardInfo: function () {
                let _primeRates = $that.buyCarMonthCardInfo.primeRates;
                $that.buyCarMonthCardInfo = {
                    orderId: '',
                    cardId: '',
                    carId:'',
                    carNum: '',
                    primeRate: '',
                    receivableAmount: '',
                    receivedAmount: '',
                    remark: '',
                    cards:[],
                    primeRates:_primeRates
                };
            },
            _queryOwnerCar:function(){
                if(!$that.buyCarMonthCardInfo.carNum){
                    return ;
                }
                let param = {
                    params: {
                        page:1,
                        row:1,
                        communityId:vc.getCurrentCommunity().communityId,
                        carNum:$that.buyCarMonthCardInfo.carNum,
                        carTypeCd:'1001'
                    }
                }
               
                //发送get请求
                vc.http.apiGet('/ownerCar.queryOwnerCars',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);

                        if(_json.total < 1){
                            vc.toast('月租车不存在,请先添加为月租车');
                            return;
                        }

                        $that.buyCarMonthCardInfo.carId = _json.data[0].carId;
                        $that.buyCarMonthCardInfo.paId = _json.data[0].paId;
                 
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }
        }
    });

})(window.vc);
