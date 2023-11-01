/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lockPersonManageInfo: {
                lockPersons: [],
                total: 0,
                records: 1,
                moreCondition: false,
                lpId: '',
                conditions: {
                    lpId: '',
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    personId: '',
                    name: '',
                    openModel: '',
                    state: '',
                    communityId: '',
                    queryTime: '',
                }
            }
        },
        _initMethod: function () {
            $that._listLockPersons(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._loadAllOwnerInfo();
            vc.initDate('queryTime', function(_value) {
                $that.lockPersonManageInfo.conditions.queryTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('lockPersonManage', 'listLockPerson', function (_param) {
                $that._listLockPersons(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLockPersons(_currentPage, DEFAULT_ROWS);
            });
            vc.on('lockPersonManage', 'chooseOwner', function(_owner) {
                $that.lockPersonManageInfo.conditions.name = _owner.name;
                $that.lockPersonManageInfo.conditions.personId = _owner.ownerId;
            });
        },
        methods: {
            _listLockPersons: function (_page, _rows) {
                $that.lockPersonManageInfo.conditions.page = _page;
                $that.lockPersonManageInfo.conditions.row = _rows;
                $that.lockPersonManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.lockPersonManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lockPerson.listLockPerson',
                    param,
                    function (json, res) {
                        var _lockPersonManageInfo = JSON.parse(json);
                        $that.lockPersonManageInfo.total = _lockPersonManageInfo.total;
                        $that.lockPersonManageInfo.records = _lockPersonManageInfo.records;
                        $that.lockPersonManageInfo.lockPersons = _lockPersonManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lockPersonManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLockPersonModal: function () {
                vc.jumpToPage("/#/pages/lock/addLockPerson");
            },
            _openDeleteLockPersonModel: function (_lockPerson) {
                vc.emit('deleteLockPerson', 'openDeleteLockPersonModal', _lockPerson);
            },
            _openRenewalLockPersonModel: function (_lockPerson) {
                vc.emit('renewalLockPerson', 'openRenewalLockPersonModal', _lockPerson);
            },
            _queryLockPersonMethod: function () {
                $that._listLockPersons(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLockPersonMethod: function () {
                $that.lockPersonManageInfo.conditions = {
                    lpId: '',
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    personId: '',
                    name: '',
                    openModel: '',
                    state: '',
                    communityId: '',
                    queryTime: '',
                };
                $that._listLockPersons(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.lockPersonManageInfo.moreCondition) {
                    $that.lockPersonManageInfo.moreCondition = false;
                } else {
                    $that.lockPersonManageInfo.moreCondition = true;
                }
            },
            _openChooseOwner: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {});
            },
            _resetLockPwd: function (_lockPerson) {
                vc.emit('resetLockPwd', 'openResetLockPwd', _lockPerson);
            },
            _switchState: function (_lockPerson) {
                let lockPerson = {
                    lpId: '',
                    machineId: '',
                    personId: '',
                    name: '',
                    startTime: '',
                    endTime: '',
                    openModel: '',
                    state: '',
                    communityId: '',
                    cardNumber: '',
                };
                vc.copyObject(_lockPerson, lockPerson);
                if (_lockPerson.state === '1001') {
                    lockPerson.state = '2002';
                } else if (_lockPerson.state === '2002') {
                    lockPerson.state = '1001';
                }

                vc.http.apiPost(
                    '/lockPerson.updateLockPerson',
                    JSON.stringify(lockPerson),
                    {
                        emulateJSON:true
                    },
                    function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that._listLockPersons(DEFAULT_PAGE, DEFAULT_ROWS);
                            vc.toast("操作成功");
                            return ;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            }
        }
    });
})(window.vc);
