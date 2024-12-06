/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accountDetailInfo: {
                accountDetails: [],
                shopAccounts: [],
                total: 0,
                records: 1,
                moreCondition: false,
                scId: '',
                conditions: {
                    acctId: '',
                    detailType: '',
                    orderId: ''
                }
            }
        },
        _initMethod: function () {
            $that.accountDetailInfo.conditions.acctId = vc.getParam('acctId');
            $that._listAccountDetails(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('accountDetail', 'listAccountDetail', function (_param) {
                $that._listAccountDetails(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAccountDetails(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccountDetails: function (_page, _rows) {
                $that.accountDetailInfo.conditions.page = _page;
                $that.accountDetailInfo.conditions.row = _rows;
                $that.accountDetailInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.accountDetailInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/account.queryOwnerAccountDetail',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.accountDetailInfo.total = _json.total;
                        $that.accountDetailInfo.records = _json.records;
                        $that.accountDetailInfo.accountDetails = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accountDetailInfo.records,
                            dataCount: $that.accountDetailInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryAccountDetailMethod: function () {
                $that._listAccountDetails(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.accountDetailInfo.moreCondition) {
                    $that.accountDetailInfo.moreCondition = false;
                } else {
                    $that.accountDetailInfo.moreCondition = true;
                }
            },
            _cancelAccountDetail: function (_detail) {
                vc.emit('cancelAccountDetail', 'openAddModal', _detail);
            },
            _goBack: function () {
                vc.goBack();
            }
        }
    });
})(window.vc);