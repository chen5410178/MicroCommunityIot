/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accountInfo: {
                accounts: [],
                total: 0,
                records: 1,
                moreCondition: false,
                scId: '',
                conditions: {
                    ownerName: '',
                    link: '',
                    communityId: ''
                }
            }
        },
        _initMethod: function () {
            $that._listAccounts(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('account', 'listAccount', function (_param) {
                $that._listAccounts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAccounts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccounts: function (_page, _rows) {
                $that.accountInfo.conditions.page = _page;
                $that.accountInfo.conditions.row = _rows;
                $that.accountInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.accountInfo.conditions
                };
                param.params.ownerName = param.params.ownerName.trim();
                param.params.link = param.params.link.trim();
                //发送get请求
                vc.http.apiGet('/account.queryCommunityAccount',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.accountInfo.total = _json.total;
                        $that.accountInfo.records = _json.records;
                        $that.accountInfo.accounts = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accountInfo.records,
                            dataCount: $that.accountInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _queryAccountMethod: function () {
                $that._listAccounts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetAccountMethod: function () {
                $that.accountInfo.conditions.ownerName = "";
                $that.accountInfo.conditions.idCard = "";
                $that.accountInfo.conditions.link = "";
                $that.accountInfo.conditions.acctType = "";
                $that._listAccounts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.accountInfo.moreCondition) {
                    $that.accountInfo.moreCondition = false;
                } else {
                    $that.accountInfo.moreCondition = true;
                }
            },
            _prestoreAccount: function () {
                vc.emit('prestoreAccount', 'openAddModal', {});
            },
            _accountDetail: function (_account) {
                vc.jumpToPage('/#/pages/property/accountDetailManage?acctId=' + _account.acctId);
            },
            _doDeleteAccount:function(_account){
                vc.emit('deleteAccount', 'openDeleteAccountModal',_account);
            }
        }
    });
})(window.vc);