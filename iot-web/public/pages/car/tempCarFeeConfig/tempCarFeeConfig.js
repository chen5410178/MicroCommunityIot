/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            tempCarFeeConfigInfo: {
                tempCarFeeConfigs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                configId: '',
                feeConfigId: '',
                carTypes: [],
                conditions: {
                    areaNum: '',
                    feeName: '',
                    paId: '',
                    carType: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function () {
            //与字典表关联
            vc.getDict('temp_car_fee_config', "car_type", function (_data) {
                $that.tempCarFeeConfigInfo.carTypes = _data;
            });
            $that._listTempCarFeeConfigs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('tempCarFeeConfig', 'listTempCarFeeConfig', function (_param) {
                $that._listTempCarFeeConfigs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listTempCarFeeConfigs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTempCarFeeConfigs: function (_page, _rows) {
                $that.tempCarFeeConfigInfo.conditions.page = _page;
                $that.tempCarFeeConfigInfo.conditions.row = _rows;
                var param = {
                    params: $that.tempCarFeeConfigInfo.conditions
                };
                param.params.areaNum = param.params.areaNum.trim();
                param.params.feeName = param.params.feeName.trim();
                //发送get请求
                vc.http.apiGet('/fee.listTempCarFeeConfigs',
                    param,
                    function (json, res) {
                        var _tempCarFeeConfigInfo = JSON.parse(json);
                        $that.tempCarFeeConfigInfo.total = _tempCarFeeConfigInfo.total;
                        $that.tempCarFeeConfigInfo.records = _tempCarFeeConfigInfo.records;
                        $that.tempCarFeeConfigInfo.tempCarFeeConfigs = _tempCarFeeConfigInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.tempCarFeeConfigInfo.records,
                            dataCount: $that.tempCarFeeConfigInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddTempCarFeeConfigModal: function () {
                vc.emit('addTempCarFeeConfig', 'openAddTempCarFeeConfigModal', {});
            },
            _openEditTempCarFeeConfigModel: function (_tempCarFeeConfig) {
                vc.emit('editTempCarFeeConfig', 'openEditTempCarFeeConfigModal', _tempCarFeeConfig);
            },
            _openDeleteTempCarFeeConfigModel: function (_tempCarFeeConfig) {
                vc.emit('deleteTempCarFeeConfig', 'openDeleteTempCarFeeConfigModal', _tempCarFeeConfig);
            },
            //查询
            _queryTempCarFeeConfigMethod: function () {
                $that._listTempCarFeeConfigs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetTempCarFeeConfigMethod: function () {
                $that.tempCarFeeConfigInfo.conditions.feeName = "";
                $that.tempCarFeeConfigInfo.conditions.areaNum = "";
                $that.tempCarFeeConfigInfo.conditions.carType = "";
                $that._listTempCarFeeConfigs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.tempCarFeeConfigInfo.moreCondition) {
                    $that.tempCarFeeConfigInfo.moreCondition = false;
                } else {
                    $that.tempCarFeeConfigInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);