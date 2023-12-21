/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlLogInfo: {
                accessControlLogs: [],
                accessControls: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    machineId: '',
                    logAction: '',
                    state: '',
                    userName: ''

                }
            }
        },
        _initMethod: function() {
            $that._listAccessControlLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listAccessControls();
        },
        _initEvent: function() {

            vc.on('accessControlLog', 'listAccessControlLog', function(_param) {
                $that._listAccessControlLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAccessControlLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccessControlLogs: function(_page, _rows) {

                $that.accessControlLogInfo.conditions.page = _page;
                $that.accessControlLogInfo.conditions.row = _rows;
                $that.accessControlLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.accessControlLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/accessControlLog.listAccessControlLog',
                    param,
                    function(json, res) {
                        var _accessControlLogInfo = JSON.parse(json);
                        $that.accessControlLogInfo.total = _accessControlLogInfo.total;
                        $that.accessControlLogInfo.records = _accessControlLogInfo.records;
                        $that.accessControlLogInfo.accessControlLogs = _accessControlLogInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlLogInfo.records,
                            currentPage: _page,
                            dataCount:_accessControlLogInfo.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openViewAccessControlLogModel: function(_log) {
                vc.emit('showAccessControlLog', 'openShowAccessControlLogModal', _log);
            },

            _queryAccessControlLogMethod: function() {
                $that._listAccessControlLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _listAccessControls: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.accessControlLogInfo.accessControls = [{
                    machineId: '',
                    machineName: '全部人脸'
                }]

                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _accessControlManageInfo = JSON.parse(json);
                        _accessControlManageInfo.data.forEach(item => {
                            $that.accessControlLogInfo.accessControls.push(item);
                        })
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchAccessControl: function(_accessControl) {
                $that.accessControlLogInfo.conditions.machineId = _accessControl.machineId;
                $that._listAccessControlLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },


        }
    });
})(window.vc);