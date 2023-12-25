/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lockInoutManageInfo: {
                lockInouts: [],
                total: 0,
                records: 1,
                moreCondition: false,
                inoutId: '',
                conditions: {
                    inoutId: '',
                    machineId: '',
                    machineCode: '',
                    userId: '',
                    userName: '',
                    openTypeCd: '',
                    communityId: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function () {
            $that._listLockInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.lockInoutManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.lockInoutManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLockInouts(_currentPage, DEFAULT_ROWS);
            });
            vc.on('lockInoutManage', 'chooseOwner', function(_owner) {
                $that.lockInoutManageInfo.conditions.userName = _owner.name;
                $that.lockInoutManageInfo.conditions.userId = _owner.ownerId;
            });
        },
        methods: {
            _listLockInouts: function (_page, _rows) {

                $that.lockInoutManageInfo.conditions.page = _page;
                $that.lockInoutManageInfo.conditions.row = _rows;
                $that.lockInoutManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.lockInoutManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lockInout.listLockInout',
                    param,
                    function (json, res) {
                        var _lockInoutManageInfo = JSON.parse(json);
                        $that.lockInoutManageInfo.total = _lockInoutManageInfo.total;
                        $that.lockInoutManageInfo.records = _lockInoutManageInfo.records;
                        $that.lockInoutManageInfo.lockInouts = _lockInoutManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lockInoutManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLockInoutMethod: function () {
                $that._listLockInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLockInoutMethod: function () {
                $that.lockInoutManageInfo.conditions = {
                    inoutId: '',
                    machineId: '',
                    machineCode: '',
                    userId: '',
                    userName: '',
                    openTypeCd: '',
                    communityId: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:''
                };
                $that._listLockInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.lockInoutManageInfo.moreCondition) {
                    $that.lockInoutManageInfo.moreCondition = false;
                } else {
                    $that.lockInoutManageInfo.moreCondition = true;
                }
            },
            _openChooseOwner: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {});
            },
        }
    });
})(window.vc);
