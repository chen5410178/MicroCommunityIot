/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorModelManageInfo: {
                monitorModels: [],
                total: 0,
                records: 1,
                moreCondition: false,
                modelId: '',
                conditions: {
                    modelId: '',
                    modelName: '',
                    modelAdapt: '',
                }
            }
        },
        _initMethod: function () {
            $that._listMonitorModels(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('monitorModelManage', 'listMonitorModel', function (_param) {
                $that._listMonitorModels(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorModels(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorModels: function (_page, _rows) {

                $that.monitorModelManageInfo.conditions.page = _page;
                $that.monitorModelManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.monitorModelManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitorModel.listMonitorModel',
                    param,
                    function (json, res) {
                        var _monitorModelManageInfo = JSON.parse(json);
                        $that.monitorModelManageInfo.total = _monitorModelManageInfo.total;
                        $that.monitorModelManageInfo.records = _monitorModelManageInfo.records;
                        $that.monitorModelManageInfo.monitorModels = _monitorModelManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorModelManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMonitorModelModal: function () {
                vc.emit('addMonitorModel', 'openAddMonitorModelModal', {});
            },
            _openEditMonitorModelModel: function (_monitorModel) {
                vc.emit('editMonitorModel', 'openEditMonitorModelModal', _monitorModel);
            },
            _openDeleteMonitorModelModel: function (_monitorModel) {
                vc.emit('deleteMonitorModel', 'openDeleteMonitorModelModal', _monitorModel);
            },
            _queryMonitorModelMethod: function () {
                $that._listMonitorModels(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMonitorModelMethod: function () {
                $that.monitorModelManageInfo.conditions = {
                    modelId: '',
                    modelName: '',
                    modelAdapt: '',
                };
                $that._listMonitorModels(DEFAULT_PAGE, DEFAULT_ROWS);
            }
        }
    });
})(window.vc);
