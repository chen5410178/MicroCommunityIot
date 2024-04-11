/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftWarnInfo: {
                liftWarns: [],
                total: 0,
                records: 1,
                moreCondition: false,
                lcId: '',
                conditions: {
                    cameraName: '',
                    cameraCode: '',
                    liftName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listLiftWarns(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftWarn', 'listLiftWarn', function (_param) {
                $that._listLiftWarns(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftWarns(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftWarns: function (_page, _rows) {

                $that.liftWarnInfo.conditions.page = _page;
                $that.liftWarnInfo.conditions.row = _rows;
                $that.liftWarnInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.liftWarnInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftCamera.listLiftWarn',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftWarnInfo.total = _json.total;
                        $that.liftWarnInfo.records = _json.records;
                        $that.liftWarnInfo.liftWarns = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftWarnInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLiftWarnMethod: function () {
                $that._listLiftWarns(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.liftWarnInfo.moreCondition) {
                    $that.liftWarnInfo.moreCondition = false;
                } else {
                    $that.liftWarnInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
