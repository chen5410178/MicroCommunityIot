/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            instrumentManageInfo: {
                instruments: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    communityId: '',
                    typeId: '',
                    charge: '',
                    sign: '',
                    implBean: '',
                }
            },
            instrumentTypeList: [],
            factoryList: [],
        },
        _initMethod: function () {
            $that._listInstrumentType();
            $that._listFactorys();
            $that._listInstruments(DEFAULT_PAGE, DEFAULT_ROWS);

        },
        _initEvent: function () {
            vc.on('instrumentManage', 'listInstrument', function (_param) {
                $that._listInstruments(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listInstruments(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listInstruments: function (_page, _rows) {
                $that.instrumentManageInfo.conditions.page = _page;
                $that.instrumentManageInfo.conditions.row = _rows;
                $that.instrumentManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.instrumentManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/instrument.listInstrument',
                    param,
                    function (json, res) {
                        var _instrumentManageInfo = JSON.parse(json);
                        $that.instrumentManageInfo.total = _instrumentManageInfo.total;
                        $that.instrumentManageInfo.records = _instrumentManageInfo.records;
                        $that.instrumentManageInfo.instruments = _instrumentManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.instrumentManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddInstrumentModal: function () {
                vc.jumpToPage("/#/pages/meter/addInstrument")
            },
            _openEditInstrumentModel: function (_instrument) {
                vc.jumpToPage("/#/pages/meter/editInstrument?machineId=" + _instrument.machineId)
            },
            _openDeleteInstrumentModel: function (_instrument) {
                vc.emit('deleteInstrument', 'openDeleteInstrumentModal', _instrument);
            },
            _queryInstrumentMethod: function () {
                $that._listInstruments(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _resetInstrumentMethod: function () {
                $that.instrumentManageInfo.conditions = {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    communityId: '',
                    typeId: '',
                    charge: '',
                    sign: '',
                    implBean: '',
                }
                $that._listInstruments(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.instrumentManageInfo.moreCondition) {
                    $that.instrumentManageInfo.moreCondition = false;
                } else {
                    $that.instrumentManageInfo.moreCondition = true;
                }
            },
            _listInstrumentType: function() {
                let param = {
                    params: {
                        page: -1,
                        row: 500,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/instrumentType.listInstrumentType',
                    param,
                    function(json, res) {
                        let _instrumentTypeManageInfo = JSON.parse(json);
                        $that.instrumentTypeList = _instrumentTypeManageInfo.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listFactorys: function() {
                let param = {
                    params: {
                        page: -1,
                        row: 500,
                    }
                };
                //发送get请求
                vc.http.apiGet('/instrumentFactory.listInstrumentFactory',
                    param,
                    function(json, res) {
                        let _instrumentFactoryManageInfo = JSON.parse(json);
                        $that.factoryList = _instrumentFactoryManageInfo.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
