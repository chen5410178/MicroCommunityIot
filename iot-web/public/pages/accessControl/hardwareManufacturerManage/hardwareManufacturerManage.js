/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            hardwareManufacturerManageInfo: {
                hardwareManufacturers: [],
                total: 0,
                records: 1,
                moreCondition: false,
                hmId: '',
                conditions: {
                    hmId: '',
                    hmName: '',
                    version: '',
                    protocolImpl: '',
                    hmType: '1001',
                    author: '',
                    link: '',
                    prodUrl: '',
                    defaultProtocol: '',
                }
            }
        },
        _initMethod: function () {
            $that._listHardwareManufacturers(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('hardwareManufacturerManage', 'listHardwareManufacturer', function (_param) {
                $that._listHardwareManufacturers(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listHardwareManufacturers(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listHardwareManufacturers: function (_page, _rows) {

                $that.hardwareManufacturerManageInfo.conditions.page = _page;
                $that.hardwareManufacturerManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.hardwareManufacturerManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/hm.listHardwareManufacturer',
                    param,
                    function (json, res) {
                        var _hardwareManufacturerManageInfo = JSON.parse(json);
                        $that.hardwareManufacturerManageInfo.total = _hardwareManufacturerManageInfo.total;
                        $that.hardwareManufacturerManageInfo.records = _hardwareManufacturerManageInfo.records;
                        $that.hardwareManufacturerManageInfo.hardwareManufacturers = _hardwareManufacturerManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.hardwareManufacturerManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddHardwareManufacturerModal: function () {
                vc.emit('addHardwareManufacturer', 'openAddHardwareManufacturerModal', {});
            },
            _openEditHardwareManufacturerModel: function (_hardwareManufacturer) {
                vc.emit('editHardwareManufacturer', 'openEditHardwareManufacturerModal', _hardwareManufacturer);
            },
            _openDeleteHardwareManufacturerModel: function (_hardwareManufacturer) {
                vc.emit('deleteHardwareManufacturer', 'openDeleteHardwareManufacturerModal', _hardwareManufacturer);
            },
            _openHardwareManufacturerAttr: function (_hardwareManufacturer) {
                vc.jumpToPage('/#/pages/accessControl/hardwareManufacturerAttrManage?hmId=' + _hardwareManufacturer.hmId);
            },
            _queryHardwareManufacturerMethod: function () {
                $that._listHardwareManufacturers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetHardwareManufacturerMethod: function () {
                $that.hardwareManufacturerManageInfo.conditions = {
                    hmId: '',
                    hmName: '',
                    version: '',
                    protocolImpl: '',
                    hmType: '1001',
                    author: '',
                    link: '',
                    prodUrl: '',
                    defaultProtocol: '',
                }
                $that._listHardwareManufacturers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openModel: function (_data, _title) {
                vc.emit('viewData', 'openEventViewDataModal', {
                    title: _title,
                    data: _data
                });
            },
            _moreCondition: function () {
                if ($that.hardwareManufacturerManageInfo.moreCondition) {
                    $that.hardwareManufacturerManageInfo.moreCondition = false;
                } else {
                    $that.hardwareManufacturerManageInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
