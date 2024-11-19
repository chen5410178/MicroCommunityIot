(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addChargePeakAlleyPriceInfo: {
                ruleId: '',
            },
            peakAlleyPrice:{
                prices: [
                ],
            }

        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addChargePeakAlleyPrice', 'openAddChargePeakAlleyPriceModal', function (_param) {
                vc.copyObject(_param,$that.addChargePeakAlleyPriceInfo);
                $('#addChargePeakAlleyPriceModel').modal('show');
                $that._listPeakAlleyPrices();
            });
        },
        methods: {
            _listPeakAlleyPrices: function() {
                $that.addChargePeakAlleyPriceInfo.page = 1;
                $that.addChargePeakAlleyPriceInfo.row = 4;
                $that.addChargePeakAlleyPriceInfo.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.addChargePeakAlleyPriceInfo
                };
                //发送get请求
                vc.http.apiGet('/chargePeakAlleyPrice.listChargePeakAlleyPrice',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.peakAlleyPrice.prices = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            saveChargePeakAlleyPriceInfo: function () {


                //
                // if (!$that.addChargePeakAlleyPriceValidate()) {
                //     vc.toast(vc.validate.errInfo);
                //     return;
                // }
                $that.peakAlleyPrice.ruleId =  $that.addChargePeakAlleyPriceInfo.ruleId;
                $that.peakAlleyPrice.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/chargePeakAlleyPrice.saveChargePeakAlleyPrice',
                    JSON.stringify($that.peakAlleyPrice),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addChargePeakAlleyPriceModel').modal('hide');
                            $that.clearAddChargePeakAlleyPriceInfo();
                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddChargePeakAlleyPriceInfo: function () {
                $that.peakAlleyPrice = {
                    prices: [],
                    ruleId: '',
                };
            }
        }
    });

})(window.vc);
