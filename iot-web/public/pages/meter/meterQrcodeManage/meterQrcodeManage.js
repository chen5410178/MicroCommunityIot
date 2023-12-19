/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterQrcodeManageInfo: {
                meterQrcodes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mqId: '',
                conditions: {
                    mqId: '',
                    qrcodeNameLike: '',
                    communityId: '',
                    queryWay: '',
                    createStaffId: '',
                    createStaffName: '',
                    state: '',
                }
            },
            queryWayList: []
        },
        _initMethod: function () {
            vc.getDict('meter_qrcode', 'query_way', function (_data) {
                $that.queryWayList = _data;
            })
            $that._listMeterQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('meterQrcodeManage', 'listMeterQrcode', function (_param) {
                $that._listMeterQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMeterQrcodes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterQrcodes: function (_page, _rows) {
                $that.meterQrcodeManageInfo.conditions.page = _page;
                $that.meterQrcodeManageInfo.conditions.row = _rows;
                $that.meterQrcodeManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.meterQrcodeManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/meterQrcode.listMeterQrcode',
                    param,
                    function (json, res) {
                        var _meterQrcodeManageInfo = JSON.parse(json);
                        $that.meterQrcodeManageInfo.total = _meterQrcodeManageInfo.total;
                        $that.meterQrcodeManageInfo.records = _meterQrcodeManageInfo.records;
                        $that.meterQrcodeManageInfo.meterQrcodes = _meterQrcodeManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.meterQrcodeManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMeterQrcodeModal: function () {
                vc.emit('addMeterQrcode', 'openAddMeterQrcodeModal', {});
            },
            _openEditMeterQrcodeModel: function (_meterQrcode) {
                vc.emit('editMeterQrcode', 'openEditMeterQrcodeModal', _meterQrcode);
            },
            _openDeleteMeterQrcodeModel: function (_meterQrcode) {
                vc.emit('deleteMeterQrcode', 'openDeleteMeterQrcodeModal', _meterQrcode);
            },
            _queryMeterQrcodeMethod: function () {
                $that._listMeterQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMeterQrcodeMethod: function () {
                $that.meterQrcodeManageInfo.conditions = {
                    mqId: '',
                    qrcodeNameLike: '',
                    communityId: '',
                    queryWay: '',
                    createStaffId: '',
                    createStaffName: '',
                    state: '',
                }
                $that._listMeterQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            showMeterQrCode:function(_meterQrcode){
                vc.emit('viewQrCode', 'openQrCodeModal',{
                    title:'水电二维码',
                    url:_meterQrcode.qrcodeUrl,
                    remark:'用户扫码充值水电费'
                })
            },
        }
    });
})(window.vc);
