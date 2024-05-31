/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            telMachineUserInfo: {
                telMachineUsers: [],
                machines:[],
                total: 0,
                records: 1,
                moreCondition: false,
                tmuId: '',
                conditions: {
                    machineId: '',
                    staffId: '',
                    staffName: '',
                }
            }
        },
        _initMethod: function () {
            $that._listMachines();
        },
        _initEvent: function () {
            vc.on('telMachineUser', 'listTelMachineUser', function (_param) {
                $that._listTelMachineUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listTelMachineUsers(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTelMachineUsers: function (_page, _rows) {

                $that.telMachineUserInfo.conditions.page = _page;
                $that.telMachineUserInfo.conditions.row = _rows;
                $that.telMachineUserInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.telMachineUserInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/telMachineUser.listTelMachineUser',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telMachineUserInfo.total = _json.total;
                        $that.telMachineUserInfo.records = _json.records;
                        $that.telMachineUserInfo.telMachineUsers = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.telMachineUserInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddTelMachineUserModal: function () {
                vc.emit('addTelMachineUser', 'openAddTelMachineUserModal', {
                    machineId:$that.telMachineUserInfo.conditions.machineId
                });
            },
            _openDeleteTelMachineUserModel: function (_telMachineUser) {
                vc.emit('deleteTelMachineUser', 'openDeleteTelMachineUserModal', _telMachineUser);
            },
            _queryTelMachineUserMethod: function () {
                $that._listTelMachineUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.telMachineUserInfo.moreCondition) {
                    $that.telMachineUserInfo.moreCondition = false;
                } else {
                    $that.telMachineUserInfo.moreCondition = true;
                }
            },
            _swatchMachine:function(_machine){
                $that.telMachineUserInfo.conditions.machineId = _machine.machineId;
                $that._listTelMachineUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listMachines: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/telMachine.listTelMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telMachineUserInfo.machines = _json.data;
                        if (_json.data && _json.data.length > 0) {
                            $that._swatchMachine(_json.data[0]);
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

        }
    });
})(window.vc);
