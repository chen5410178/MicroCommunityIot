/**
 临时内部车
 **/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            tempInnerCarInfo: {
                tempInnerCars: [],
                tempCarFeeConfigs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                carNum: '',
                conditions: {
                    carNum: '',
                    ticId: '',
                    configId: ''
                }
            }
        },
        _initMethod: function() {
            $that._listTempCarFeeConfigs();
            $that._listTempInnerCars(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {
            vc.on('tempInnerCar', 'listTempInnerCar', function(_param) {
                $that._listTempInnerCars(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listTempInnerCars(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTempCarFeeConfigs: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.tempInnerCarInfo.tempCarFeeConfigs = [{
                    configId: '',
                    feeName: '全部'
                }];
                //发送get请求
                vc.http.apiGet('/fee.listTempCarFeeConfigs',
                    param,
                    function(json, res) {
                        var _tempCarFeeConfigInfo = JSON.parse(json);
                        if (_tempCarFeeConfigInfo.data) {
                            _tempCarFeeConfigInfo.data.forEach(item => {
                                $that.tempInnerCarInfo.tempCarFeeConfigs.push(item);
                            });
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchTempCarFeeConfig: function(_config) {
                $that.tempInnerCarInfo.conditions.configId = _config.configId;
                $that._listTempInnerCars(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listTempInnerCars: function(_page, _rows) {
                $that.tempInnerCarInfo.conditions.page = _page;
                $that.tempInnerCarInfo.conditions.row = _rows;
                $that.tempInnerCarInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.tempInnerCarInfo.conditions
                };
                param.params.ticId = param.params.ticId.trim();
                param.params.carNum = param.params.carNum.trim();
                //发送get请求
                vc.http.apiGet('/car.listTempInnerCars',
                    param,
                    function(json, res) {
                        var _tempInnerCarInfo = JSON.parse(json);
                        $that.tempInnerCarInfo.total = _tempInnerCarInfo.total;
                        $that.tempInnerCarInfo.records = _tempInnerCarInfo.records;
                        $that.tempInnerCarInfo.tempInnerCars = _tempInnerCarInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.tempInnerCarInfo.records,
                            dataCount: $that.tempInnerCarInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddTempInnerCarModal: function() {
                var selectedConfig = $that.tempInnerCarInfo.tempCarFeeConfigs.find(item => item.configId == $that.tempInnerCarInfo.conditions.configId);
                if (!selectedConfig || !selectedConfig.configId) {
                    vc.toast("请先选择左侧的临时车费");
                    return;
                }
                vc.emit('addTempInnerCar', 'openAddTempInnerCarModal', {
                    configId: selectedConfig.configId,
                    feeName: selectedConfig.feeName,
                    paId: selectedConfig.paId
                });
            },
            _openEditTempInnerCarModel: function(_tempInnerCar) {
                vc.emit('editTempInnerCar', 'openEditTempInnerCarModal', _tempInnerCar);
            },
            _openDeleteTempInnerCarModel: function(_tempInnerCar) {
                vc.emit('deleteTempInnerCar', 'openDeleteTempInnerCarModal', _tempInnerCar);
            },
            //查询
            _queryTempInnerCarMethod: function() {
                $that._listTempInnerCars(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetTempInnerCarMethod: function() {
                $that.tempInnerCarInfo.conditions.ticId = "";
                $that.tempInnerCarInfo.conditions.carNum = "";
                $that._listTempInnerCars(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function() {
                if ($that.tempInnerCarInfo.moreCondition) {
                    $that.tempInnerCarInfo.moreCondition = false;
                } else {
                    $that.tempInnerCarInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);

