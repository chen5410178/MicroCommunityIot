/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            aAccessControlInfo: {
                accessControls: [],
                callingPlatforms: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineCode: '',
                    machineName: '',
                    locationName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.on('selectAdminCommunity','changeCommunity',function(_community){
                $that.aAccessControlInfo.conditions.communityId = _community.communityId;
                $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
            })
        },
        _initEvent: function () {

            vc.on('aAccessControl', 'listAccessControl', function (_param) {
                $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAccessControls(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccessControls: function (_page, _rows) {

                $that.aAccessControlInfo.conditions.page = _page;
                $that.aAccessControlInfo.conditions.row = _rows;

                let param = {
                    params: $that.aAccessControlInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/accessControl.listAdminAccessControl',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.aAccessControlInfo.total = _json.total;
                        $that.aAccessControlInfo.records = _json.records;
                        $that.aAccessControlInfo.accessControls = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.aAccessControlInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryAccessControlMethod: function () {
                $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.aAccessControlInfo.moreCondition) {
                    $that.aAccessControlInfo.moreCondition = false;
                } else {
                    $that.aAccessControlInfo.moreCondition = true;
                }
            },
            _toAccessControlDetail: function (_accessControl) {
                vc.jumpToPage('/#/pages/accessControl/aAccessControlDetail?machineId=' + _accessControl.machineId);
            },

        }
    });
})(window.vc);