/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            workLicenseFactoryInfo: {
                workLicenseFactorys: [],
                total: 0,
                records: 1,
                moreCondition: false,
                factoryId: '',
                conditions: {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                }
            }
        },
        _initMethod: function () {
            $that._listWorkLicenseFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('workLicenseFactory', 'listWorkLicenseFactory', function (_param) {
                $that._listWorkLicenseFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listWorkLicenseFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listWorkLicenseFactorys: function (_page, _rows) {

                $that.workLicenseFactoryInfo.conditions.page = _page;
                $that.workLicenseFactoryInfo.conditions.row = _rows;
                let param = {
                    params: $that.workLicenseFactoryInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/workLicenseFactory.listWorkLicenseFactory',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.workLicenseFactoryInfo.total = _json.total;
                        $that.workLicenseFactoryInfo.records = _json.records;
                        $that.workLicenseFactoryInfo.workLicenseFactorys = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.workLicenseFactoryInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddWorkLicenseFactoryModal: function () {
                vc.emit('addWorkLicenseFactory', 'openAddWorkLicenseFactoryModal', {});
            },
            _openEditWorkLicenseFactoryModel: function (_workLicenseFactory) {
                vc.emit('editWorkLicenseFactory', 'openEditWorkLicenseFactoryModal', _workLicenseFactory);
            },
            _openDeleteWorkLicenseFactoryModel: function (_workLicenseFactory) {
                vc.emit('deleteWorkLicenseFactory', 'openDeleteWorkLicenseFactoryModal', _workLicenseFactory);
            },
            _queryWorkLicenseFactoryMethod: function () {
                $that._listWorkLicenseFactorys(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.workLicenseFactoryInfo.moreCondition) {
                    $that.workLicenseFactoryInfo.moreCondition = false;
                } else {
                    $that.workLicenseFactoryInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
