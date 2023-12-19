/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            passQrcodeSettingInfo: {
                passQrcodeSettings: [],
                total: 0,
                records: 1,
                moreCondition: false,
                pqsId: '',
                conditions: {
                    qrcodeName: '',
                    expired: '',
                    state: '',

                }
            }
        },
        _initMethod: function () {
            $that._listPassQrcodeSettings(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('passQrcodeSetting', 'listPassQrcodeSetting', function (_param) {
                $that._listPassQrcodeSettings(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listPassQrcodeSettings(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listPassQrcodeSettings: function (_page, _rows) {

                $that.passQrcodeSettingInfo.conditions.page = _page;
                $that.passQrcodeSettingInfo.conditions.row = _rows;
                $that.passQrcodeSettingInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.passQrcodeSettingInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/passcode.listPassQrcodeSetting',
                    param,
                    function (json, res) {
                        var _json = JSON.parse(json);
                        $that.passQrcodeSettingInfo.total = _json.total;
                        $that.passQrcodeSettingInfo.records = _json.records;
                        $that.passQrcodeSettingInfo.passQrcodeSettings = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.passQrcodeSettingInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddPassQrcodeSettingModal: function () {
                vc.emit('addPassQrcodeSetting', 'openAddPassQrcodeSettingModal', {});
            },
            _openEditPassQrcodeSettingModel: function (_passQrcodeSetting) {
                vc.emit('editPassQrcodeSetting', 'openEditPassQrcodeSettingModal', _passQrcodeSetting);
            },
            _openDeletePassQrcodeSettingModel: function (_passQrcodeSetting) {
                vc.emit('deletePassQrcodeSetting', 'openDeletePassQrcodeSettingModal', _passQrcodeSetting);
            },
            _queryPassQrcodeSettingMethod: function () {
                $that._listPassQrcodeSettings(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            showQrCode:function(_passQrcodeSetting){
                vc.emit('viewQrCode', 'openQrCodeModal',{
                    title:'通行码申请',
                    url:_passQrcodeSetting.qrcodeUrl,
                    remark:'请将二维码解码后，地址绑定到公众号菜单'
                })
            },

        }
    });
})(window.vc);
