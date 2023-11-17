/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            appUserInfo: {
                appUsers: [],
                total: 0,
                records: 1,
                moreCondition: false,
                auId: '',
                conditions: {
                    ownerName: '',
                    link: '',
                    roomName: '',
                    state: '10000',

                }
            }
        },
        _initMethod: function () {
            $that._listAppUsers(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('appUser', 'listAppUser', function (_param) {
                $that._listAppUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAppUsers(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAppUsers: function (_page, _rows) {

                $that.appUserInfo.conditions.page = _page;
                $that.appUserInfo.conditions.row = _rows;
                $that.appUserInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.appUserInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/appUser.listAppUser',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.appUserInfo.total = _json.total;
                        $that.appUserInfo.records = _json.records;
                        $that.appUserInfo.appUsers = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.appUserInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAuthAppUserModel: function (_appUser) {
                vc.emit('editAppUser', 'openEditAppUserModal', _appUser);
            },
            _openDeleteAppUserModel: function (_appUser) {
                vc.emit('deleteAppUser', 'openDeleteAppUserModal', _appUser);
            },
            _queryAppUserMethod: function () {
                $that._listAppUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.appUserInfo.moreCondition) {
                    $that.appUserInfo.moreCondition = false;
                } else {
                    $that.appUserInfo.moreCondition = true;
                }
            },
            _switchState:function(_state){
                $that.appUserInfo.conditions.state = _state;
                $that._listAppUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            }


        }
    });
})(window.vc);
