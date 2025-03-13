/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminBarrierInfo: {
                barriers: [],
                parkingBoxs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineCode: '',
                    machineName: '',
                    boxId: '',
                    communityId:''

                }
            }
        },
        _initMethod: function () {
            vc.on('selectAdminCommunity','changeCommunity',function(_community){
                $that.adminBarrierInfo.conditions.communityId = _community.communityId;
                $that._listAdminBarriers(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            $that._listAdminBarriers(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('adminBarrier', 'listAdminBarrier', function (_param) {
                $that._listAdminBarriers(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAdminBarriers(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAdminBarriers: function (_page, _rows) {

                $that.adminBarrierInfo.conditions.page = _page;
                $that.adminBarrierInfo.conditions.row = _rows;
                let param = {
                    params: $that.adminBarrierInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/barrier.listAdminBarrier',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.adminBarrierInfo.total = _json.total;
                        $that.adminBarrierInfo.records = _json.records;
                        $that.adminBarrierInfo.barriers = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.adminBarrierInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryAdminBarrierMethod: function () {
                $that._listAdminBarriers(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.adminBarrierInfo.moreCondition) {
                    $that.adminBarrierInfo.moreCondition = false;
                } else {
                    $that.adminBarrierInfo.moreCondition = true;
                }
            },

        }
    });
})(window.vc);
