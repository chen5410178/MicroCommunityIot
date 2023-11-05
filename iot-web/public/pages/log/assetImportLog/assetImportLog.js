/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            assetImportLogInfo: {
                logs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                carNum: '',
            }
        },
        _initMethod: function() {
            $that._listAssetImportLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('assetImportLog', 'listAssetImportLog', function(_param) {
                $that._listAssetImportLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAssetImportLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAssetImportLogs: function(_page, _rows) {
                var param = {
                    params: {
                        page: _page,
                        row: _rows,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/log.queryAssetImportLog',
                    param,
                    function(json, res) {
                        let _assetImportLogInfo = JSON.parse(json);
                        $that.assetImportLogInfo.total = _assetImportLogInfo.total;
                        $that.assetImportLogInfo.records = _assetImportLogInfo.records;
                        $that.assetImportLogInfo.logs = _assetImportLogInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.assetImportLogInfo.records,
                            dataCount: $that.assetImportLogInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryData: function() {
                $that._listAssetImportLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openDetail: function(_log) {
                vc.jumpToPage('/#/pages/log/assetImportLogDetail?logId=' + _log.logId + "&logType=" + _log.logType);
            }
        }
    });
})(window.vc);