(function(vc) {

    vc.extends({
        data: {
            buyMonthCardQrCodeInfo: {
                url: '',
                inspectionName: '',
            }
        },
        _initMethod: function() {},
        _initEvent: function() {
            vc.on('buyMonthCardQrCode', 'openQrCodeModal', function(_param) {
                $('#buyMonthCardQrCodeModel').modal('show');
                $that._loadQrCodeUrl(_param);
            });
        },
        methods: {
            _loadQrCodeUrl: function(_param) {
                //判断是否支付
                var param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/carMonth.getBuyCarMonthCardQrCodeUrl',
                    param,
                    function(json, res) {
                        let _info = JSON.parse(json);
                        $that._viewInBuyMonthCardQr(_info.data.url)
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            // 两分钟后显示遮罩层
            _viewInBuyMonthCardQr: function(_url) {
                document.getElementById("buyMonthCardQrcode").innerHTML = "";
                let qrcode = new QRCode(document.getElementById("buyMonthCardQrcode"), {
                    text: "213", //你想要填写的文本
                    width: 200, //生成的二维码的宽度
                    height: 200, //生成的二维码的高度
                    colorDark: "#000000", // 生成的二维码的深色部分
                    colorLight: "#ffffff", //生成二维码的浅色部分
                    correctLevel: QRCode.CorrectLevel.L
                });
                qrcode.makeCode(_url);
            }
        }
    });

})(window.vc);