/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            hardwareManufacturerAttrManageInfo: {
                hardwareManufacturerAttrs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                attrId: '',
                conditions: {
                    attrId: '',
                    hmId: '',
                    specCd: '',
                    value: '',
                }
            }
        },
        _initMethod: function () {
            $that.hardwareManufacturerAttrManageInfo.conditions.hmId = vc.getParam("hmId");
            $that._listHardwareManufacturerAttrs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('hardwareManufacturerAttrManage', 'listHardwareManufacturerAttr', function (_param) {
                $that._listHardwareManufacturerAttrs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listHardwareManufacturerAttrs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listHardwareManufacturerAttrs: function (_page, _rows) {

                $that.hardwareManufacturerAttrManageInfo.conditions.page = _page;
                $that.hardwareManufacturerAttrManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.hardwareManufacturerAttrManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/hm.listHardwareManufacturerAttr',
                    param,
                    function (json, res) {
                        var _hardwareManufacturerAttrManageInfo = JSON.parse(json);
                        $that.hardwareManufacturerAttrManageInfo.total = _hardwareManufacturerAttrManageInfo.total;
                        $that.hardwareManufacturerAttrManageInfo.records = _hardwareManufacturerAttrManageInfo.records;
                        $that.hardwareManufacturerAttrManageInfo.hardwareManufacturerAttrs = _hardwareManufacturerAttrManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.hardwareManufacturerAttrManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddHardwareManufacturerAttrModal: function () {
                vc.emit('addHardwareManufacturerAttr', 'openAddHardwareManufacturerAttrModal', $that.hardwareManufacturerAttrManageInfo.conditions.hmId);
            },
            _openDeleteHardwareManufacturerAttrModel: function (_hardwareManufacturerAttr) {
                vc.emit('deleteHardwareManufacturerAttr', 'openDeleteHardwareManufacturerAttrModal', _hardwareManufacturerAttr);
            },
            _queryHardwareManufacturerAttrMethod: function () {
                $that._listHardwareManufacturerAttrs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);
